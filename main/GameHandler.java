/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package main;

import riskgame.*;
import networking.*;
import alert.*;
import java.util.ArrayList;
import java.util.Random;
import console.ConsoleApp;


/**
 *
 * @author jeffrey
 */
public class GameHandler implements Runnable {
    private static boolean gameStatus;
    private static int instantiations;
    private NetworkPlayer current_player;
    private NetworkPlayer prompted_player;
    private int current_state;
    private static int current_player_id;
    private Board board;
    private Waiter AlertMain;
    private Listener server;
    private Thread myThread;
    public static final int MIN_PLAYERS = 2;
    public static final String BOARD_PATH = "F:/school/CS201/Lab 10/RiskBoard.txt";
    public static final int STATE_INIT_SETUP = 0;
    private Waiter GameWaiter;
    public GameHandler(Waiter amain, Listener serv) {
	instantiations++;
	if (instantiations > 1) {
	    System.err.println("GameHandler may not be used lightly");
	    System.exit(1);
	}
	board = new Board(BOARD_PATH);
	if (amain == null || serv == null) {
	    System.err.println("GameHandler passed nulls");
	    System.exit(1);
	}

	AlertMain = amain;
	server = serv;
	GameWaiter = new Waiter();
    }

    public void run() {
	// Start of a new game

	boolean gameOver = false;
	server.writeToAllClients("- A new game of risk begins!");
	server.gameWriteMultilineToAllClients(board.makeBoard());
	current_player = server.getPlayerById(0); // First player is 0
	while (!gameOver) {
	    server.writeToAllClients("- "+current_player.getName()+"'s Turn!");
	    current_player.setWriteLock(true);
	    current_player.setMyTurn(this.GameWaiter);
	    current_player.setState(2);
	    switch (current_state) {
		case 0: {
		    // We're setting up first territories
		    Country choice = getCountryChoice("["+current_player.getName()+"] Choose a territory");
		    if (board.placeArmyUnit(current_player, choice, 1)) {
			server.writeToAllClients(" - ["+current_player.getName()+"] chose "+choice.getName());
			current_player.gameWrite("- "+choice.getName()+" chosen");
			nextPlayer();
		    }
		    if (board.getAllTerritoriesOwned()) {
			current_state = 2;
			distributeRemainingArmyUnits();
		    }
		    break;
		}
		case 1: {
		    // Randomly assign territories
		    randomlyAssignTerritories();
		    distributeRemainingArmyUnits();
		    server.writeToAllClients(" - All territories distributed, Game on!");
		    // Now players need to place individual army units
		    current_state = 2;
		    break;
		}

		case 2: {
		    if (current_player.getArmyUnitBonus() >= 0) {
			Country choice = getCountryChoice("["+current_player.getName()+"] Choose a " +
				"territory to place army unit ["+current_player.getArmyUnitBonus()+"" +
				"] remain");
			if (board.placeArmyUnit(current_player, choice, 1)) {
			    server.writeToAllClients(" - ["+current_player.getName()+"] Reinforced "+choice.getName());
			    current_player.gameWrite("- "+choice.getName()+" chosen");
			    current_player.setArmyUnitBonus((current_player.getArmyUnitBonus() - 1));
			    nextPlayer();
			}
		    } else {
			// Game on!
			nextPlayer();
			if (current_player.getArmyUnitBonus() <= 0) {
			    server.writeToAllClients(" - Game On!");
			    try { Thread.sleep(1000); } catch (Exception e) {}
			    current_state = 3;
			}
		    }
		}
		case 3: {
		    server.gameWriteMultilineToAllClients(board.makeBoard());
		    try { Thread.sleep(1000); } catch (Exception e) {}
		    playerTurn();
		    nextPlayer();
		}


	    }
	}
    }
    private void nextPlayer() {
	if (current_player != null) {
	    current_player.setMyTurn(null);
	    current_player.setState(1);
	}
	current_player_id++;
	if (current_player_id == server.getMaxPlayerCount())
	    current_player_id = 0;

	current_player = server.getPlayerById(current_player_id);
	
	if (current_player == null)
	    nextPlayer();
	else {
	    current_player.setMyTurn(GameWaiter);
	    current_player.setState(2);
	}


    }
    private Country getCountryChoice(String prompt) {
	    boolean valid_input = false;
	    Country chosen = null;
	    while (!valid_input)
	    {
		current_player.gameWriteMultiline(board.makeBoard());
		try {Thread.sleep(100);} catch(InterruptedException e) { }
		current_player.rawWrite("\n"+prompt +"\n");
		try {Thread.sleep(100);} catch(InterruptedException e) { }
		int row = -1;
		current_player.rawWrite("Row: ");
		AlertGame alert = null;
		alert = (AlertGame)GameWaiter.pauseAlert();
		// Try to parse the input to an int
		String input = alert.getMsg();
		try {
		    row = Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
		    
		}

		if (row != -1) {
		    char column = '0';
		    current_player.rawWrite("Column: ");
		    alert = (AlertGame)GameWaiter.pauseAlert();
		    input = alert.getMsg();
		    column = input.toUpperCase().charAt(0);
	
		    chosen = board.getValidTerritory(row, column);
		}
		if (chosen != null)
		    valid_input = true;
		else
		    current_player.gameWrite("Invalid choice, try again");
	    }

	    return chosen;
	}
    public void setState(int s) { current_state = s; }
    public static boolean gameStatus() { return gameStatus; }

    private void playerTurn() {
	int bonus = board.getBonusForPlayer(current_player);
	current_player.setArmyUnitBonus(bonus);
	// Place bonus army units
	while (current_player.getArmyUnitBonus() > 0) {
		Country choice = getCountryChoice("["+current_player.getName()+"] Choose a " +
				"territory to place army unit ["+current_player.getArmyUnitBonus()+"" +
				"] remain");
		if (board.placeArmyUnit(current_player, choice, 1)) {
		    server.writeToAllClients(" - ["+current_player.getName()+"] Reinforced "+choice.getName());
		    current_player.gameWrite("- "+choice.getName()+" reinforced");
		    current_player.setArmyUnitBonus((current_player.getArmyUnitBonus() - 1));
		}
	}

	    // Attack time
	boolean valid_input = false;
	while (!valid_input) {
	    Country base_attack = getCountryChoice("- [" + current_player.getLetter() + "] choose territory to attack from:");
	    if (base_attack.getPlayer().equals(current_player)) {
		Country country_to_attack = getCountryChoice("- [" + current_player.getName() + "] choose territory to attack:");
		if (!base_attack.getPlayer().equals(country_to_attack.getPlayer())) {
			if (playerAttack(base_attack, country_to_attack) != 0)
			  valid_input = true;
		     } else {
			 System.err.println("Can not attack own country");
		     }
		 } else {
		     System.err.println("You don't own that territory");
		 }
	}

    }
	// Makes an attack from Country from to Country to
	// Returns 0 if attack was impossible
	// Returns a negitive # if from lost army units
	// Returns a positive # if to lost army units
	//  Will return positive if both lost
	public int playerAttack(Country from, Country to) {
	    NetworkPlayer from_player = (NetworkPlayer)from.getPlayer();
	    NetworkPlayer to_player = (NetworkPlayer)to.getPlayer();
	    int army_unit_result = 0;
	    // Validate country choice
	    if (from.getPlayer().equals(to.getPlayer())) {
		to_player.gameWrite("Error: You can't attack your own territory");
		return army_unit_result;
	    } if (!from.nextTo(to)) {
		to_player.gameWrite("Error: You can't attack a territory there");
		return army_unit_result;
	    } if (!(from.getArmyUnits() > 1) && (to.getArmyUnits() != 0)) {
		to_player.gameWrite("Error: You have too few army units to attack from there");
		return army_unit_result;
	    }

	    // Prompt from player for dice
	    int from_a_u = from.getArmyUnits();
	    int attack_die_choice = 0;
	    Dumb_die[] from_die = null;

	    if (from_a_u > 3)
		attack_die_choice = 3;
	    else if (from_a_u > 2)
		attack_die_choice = 2;
	    else
		attack_die_choice = 1;

	    from_die = playerDieChoice(from_player, "How many dice to roll for attack?", attack_die_choice);

	    int to_a_u = to.getArmyUnits();
	    Dumb_die[] to_die = null;
	    int defend_die_choice = 1;

	    if (to_a_u > 1)
		defend_die_choice = 2;

	   to_die = playerDieChoice(to_player, "How many dice to roll for defence?", defend_die_choice);

	   // Report rolls to users
	   server.gameWriteToAllClients("Player "+from.getPlayer().getLetter()+" rolls: ");
	   for (int i = 0; i < from_die.length; i++)
	       System.out.print(from_die[i].getSide()+" ");
	   server.gameWriteToAllClients("");
	   server.gameWriteToAllClients("Player "+to.getPlayer().getLetter()+" rolls: ");
	   for (int i = 0; i < to_die.length; i++)
	       System.out.print(to_die[i].getSide()+" ");
	   server.gameWriteToAllClients("");


	   int num_evaluate_die = to_die.length;

	   // If true, defender must have picked 2 and attacker picked 1
	   if (from_die.length < to_die.length)
	       num_evaluate_die = 1;

	   int killed_from = 0;
	   int killed_to = 0;

	   // Compare dice
	   for (int i = 0; i < num_evaluate_die; i++) {
	       if (from_die[0].compareTo(to_die[0]) > 0) {
		   killed_to++;
	       } else {
		   killed_from++; // even if tie, defender wins
	       }
	   }

	   // Update army units per territory
	   from.setArmyUnits((from.getArmyUnits() - killed_from));
	   to.setArmyUnits((to.getArmyUnits() - killed_to));

	   // Defender wins, no army units lost
	   if (killed_to == 0) {
	       server.gameWriteToAllClients(from.getName() + " has defended themselves from "
		       +to.getName()+", Player " + to.getPlayer().getLetter()
		       + " loses " + killed_from + " army units");
	       return (0 - killed_from);
	   } else {
	       // Player has conquored territory
	       if (to.getArmyUnits() == 0) {
		   server.gameWriteToAllClients(from.getName()+" looses "+killed_from+" army units, " +
			    "Player "+from.getPlayer().getLetter()+" losses "+killed_to+" from "+from.getName());
		    server.gameWriteToAllClients("Player "+from.getPlayer().getLetter()+" has conquored "+to.getName()+"!");
		    board.reassignCountry(from.getPlayer(), to);
		    server.gameWriteToAllClients("");
		    server.gameWriteToAllClients("Player "+from.getPlayer().getLetter()+" must move army units to "+to.getName()+"!");
		    this.fortify((NetworkPlayer)from.getPlayer(), from, to);
		    // TODO Risk card given here
		    return killed_to;
	       } else if (killed_from == 0) {
		   server.gameWriteToAllClients("Player "+from.getPlayer().getLetter()+" attack sucessful! \n"
			    +from.getName()+" looses "+killed_from+" army units, " +
			    "Player "+to.getPlayer().getLetter()+" losses "+killed_to+" from "+to.getName());
		   return killed_to;
	       } else {
		    server.gameWriteToAllClients("Player "+from.getPlayer().getLetter()+" attack semi-sucessful. \n"
			    +from.getName()+" looses "+killed_from+" army units, " +
			    "Player "+to.getPlayer().getLetter()+" losses "+killed_to+" from "+to.getName());
		    return killed_to;
	       }
	   }
	}
    public void distributeRemainingArmyUnits() {
	ArrayList<Country> clist = getTerritoriesArrayList();
	// Find out how many a_u's we give out total
	int totalArmyUnits = server.getConnectedPlayers() * (50 - (5 * server.getConnectedPlayers()));
	// Subtract the ones that are automatically placed
	totalArmyUnits -= clist.size();
	// Divide it by clients, this is how many each player will have left to place
	int playerBonus = (totalArmyUnits/server.getConnectedPlayers());
	// Give them out to each plaeyr
	NetworkPlayer[] clients = Listener.getClients();

	// For testing
	playerBonus = 3;
	for (int i = 0; i < clients.length; i++) {
	    clients[i].setArmyUnitBonus(playerBonus);
	}
    }

    public ArrayList<Country> getTerritoriesArrayList() {

	Country countries[][] = board.getAllTerritories();
	ArrayList<Country> clist = new ArrayList<Country>();
	// Populate country array list, no nulls please
	for (int i = 0; i < countries.length; i++) {
	    if (countries[i] != null) {
		for (int j = 0; j < countries[i].length; j++) {
		    if (countries[i][j] != null)
			clist.add(countries[i][j]);
		}
	    }
	}
	return clist;
    }
    	public Dumb_die[] playerDieChoice(NetworkPlayer player, String prompt, int max_die) {
	    boolean valid_input = false;
	    Dumb_die[] return_dice = null;
	    while (!valid_input) {
		player.setMyTurn(GameWaiter);
		player.gameWrite("Player "+player.getLetter()+","+ prompt + " (1-"+max_die+"): ");
		AlertGame alert = (AlertGame)GameWaiter.pauseAlert();
		String input = alert.getMsg();
		int numberChoice = 0;
		try {
		    numberChoice = Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
		}
		if ((numberChoice > 0) && (numberChoice <= max_die)) {
			return_dice = player.rollDice(numberChoice);
			valid_input = true;
		 } else
			player.gameWrite("Error: Die value out of range");
	    }

	    return return_dice;
	}

	private void fortify(NetworkPlayer player, Country from, Country to) {
	    boolean valid_input = false;
	    while (!valid_input) {
		valid_input = true;
		if (from == null) {
		    from = getCountryChoice("Player " + player.getLetter() + " choose territory take get reinforcements from");
			if (from.getPlayer().equals(player)) {
			    if (from.getArmyUnits() < 2) {
				    valid_input = false;
				    player.gameWrite("Error: You can not take territories from there");
			    }
		    }
		    if (to == null) {
			to = getCountryChoice("Player " + player.getLetter() + " choose territory to place reinfrocements");
			if (!to.getPlayer().equals(player) || !to.nextTo(from) || !to.equals(from)) {
			    valid_input = false;
			}
		    }
		}
	    }

	    player.gameWrite("Player " + player.getLetter() + " how many do you want to move?: ");
	    AlertGame alert = (AlertGame)GameWaiter.pauseAlert();
	    int to_move = 0;
	    try {
		to_move = Integer.parseInt(alert.getMsg());
	    } catch (NumberFormatException nfe) { }
		if (to_move < from.getArmyUnits()) {
		// Everything is valid, we can move these units
			from.setArmyUnits(from.getArmyUnits() - to_move);
			to.setArmyUnits(to.getArmyUnits() + to_move);
			System.out.println("Player " + player.getLetter() + " has moved " + to_move
					 +" army units from " + from.getName() + " to " + to.getName());
		} else {
		    System.err.println("You can't move that many units");
		    this.fortify(player, from, to);
		}
	}
    public void randomlyAssignTerritories() {
	ConsoleApp.println("Randomly Assigning Territories...");
	ArrayList<Country> clist = getTerritoriesArrayList();
	int size = clist.size();

	for (int i = 0; i < size; i++) {
	    // Randomly chose from the array list
	    Country give = clist.get(((new Random()).nextInt(clist.size())));
	    // Tell the player
	    current_player.gameWrite("- You randomly get: "+give.getName());
	    try { Thread.sleep(100); } catch (Exception e) {}
	    // Give the player that territory
	    board.placeArmyUnit(current_player, give, 1);
	    // Remove it from our array
	    clist.remove(give);
	    // Move on
	    nextPlayer();

	}
	distributeRemainingArmyUnits();

    }


}
