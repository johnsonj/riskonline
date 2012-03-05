
package riskgame;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import networking.Listener;
import console.ConsoleApp;

public class Board
{
	private Continent continents[];
	private Country territories[][];
	private int territory_size;
	private static final int DEFAULT_A_U = 0;
	private int numberOfPlayers;
	private boolean allTerritoriesOwned;
	private int territoriesPlaced;
	//private Player players[];
	private Scanner in;

	/*
	 * Initializes continents, territories arrays
	 */
	public Board(String filename) {
		File board_file = new File(filename);
		Scanner read_file = null;
		try {
		read_file = new Scanner(board_file);
		} catch (IOException ioe) {
		    System.err.println("Can not read board file.");
		    System.err.println(ioe);
		}
		StringTokenizer token;

		boolean found_header = false;
		int continent_count = 0;
		// Search for the "Continents" header in the boardfile
		while (!found_header)
		{
			token = new StringTokenizer(read_file.nextLine(), ",");
			if (token.nextToken().equals("Continents"))
			{
				found_header = true;
				// Next token should be num continents
				continent_count = Integer.parseInt(token.nextToken());
			}
		}
		continents = new Continent[continent_count];

		// Now read the following N lines and create continents
		for (int i = 0; i<continent_count; i++)
		{
			token = new StringTokenizer(read_file.nextLine(), ",");
			String name = token.nextToken();
			char symbol = token.nextToken().charAt(0);
			int reinf = Integer.parseInt(token.nextToken());
			continents[i] = new Continent(name, symbol, reinf);
		}

		found_header = false;
		int territory_count = 0;
		// Search for the "Territories" header in the boardfile
		while (!found_header)
		{
			token = new StringTokenizer(read_file.nextLine(), ",");
			if (token.nextToken().equals("Territories"))
			{
				found_header = true;
				// Next token should be num territories
				territory_count = Integer.parseInt(token.nextToken());
			}
		}

		// Initialize array
		int rows = Country.getMAXROW() + 1;
		char cols = Country.getMAXCOL();
		int col_num = (int)cols - 63; // Shift making A=1
		territories = new Country[rows][col_num];

		// Read next N lines and populate array
		for (int i = 0; i<territory_count ; i++)
		{
			// Line goes: STRING,STRING,INT,CHAR
			token = new StringTokenizer(read_file.nextLine(), ",");
			String name = token.nextToken();
			String continent = token.nextToken();
			int row = Integer.parseInt(token.nextToken());
			char col = token.nextToken().charAt(0);
			Country country = new Country(name, continent, row, col, DEFAULT_A_U);
			int c_num = (int)col - 64;
			territories[row][c_num] = country;
			territory_size++;
			// Pass the continent this country is on our new refrence
			this.makeContinentFromName(continent).addCountry(country);
		}

		// Setup scanner for use throughout rest of file
		in = new Scanner(System.in);
	}
	public String toString() {
		String return_string = "";

		for (int i = 0; i<=(Country.getMAXROW()); i++)
		{
			char max_col = Country.getMAXCOL();
			int col_num = ((int)max_col)-63;
			for (int x = 0; x<col_num; x++)
			{
				if (territories[i][x] != null)
				{
					char current_col_char = (char)(x + 64);
					return_string += "(R:"+i+" Col:"+current_col_char+") "+territories[i][x].getName()+"\n";
				}
			}
		}
		return return_string;
	}

	// Returns board as a string
	public String makeBoard() {
		// Populate header
		String header = makeContinentsHeader() + "\n";
		header += makePlayersHeader() + "\n";
		String board ="";
		char max_col = Country.getMAXCOL();
		int col_num = ((int)max_col)-63;

		// Create A->MAX_COL across top of board
		for (char c = 'A'; c<=max_col; c++)
			header += "    " + c;

		// Step through first part of array, each entry = new row
		for (int i = 1; i<=Country.getMAXROW(); i++)
		{
			// Display row number
			board += (i) +" ";
			// Loop through array at i position, containts territories
			for (int x = 1; x<col_num; x++)
			{
				board += makeBoardIcon(territories[i][x]);
			}
			board += "\n";
		}
		return header + "\n" + board;
	}
	// Displays continents ledgend
	private String makeContinentsHeader() {
		String return_string = "Continents: ";
		for (int i = 0; i< continents.length ;i++ ) {
			return_string += continents[i].getSymbol()+"="+continents[i].getName()+" ";
		}
		return return_string;
	}

	private String makePlayersHeader() {
	    Player[] players = Listener.getClients();
	    String return_string = "Players: ";
	    for (int i = 0; i<players.length; i ++) {
		return_string += " "+players[i].getLetter()+"="+players[i].getName();
	    }
	    return return_string;
	}
	// Displays board icon, EG: #_0 with proper spacing
	private String makeBoardIcon(Country c) {
		String return_string;
		if (c != null) {
		    Continent continent = makeContinentFromName(c.getContinent());
		    String army_units = ""+c.getArmyUnits();
		    if (c.getArmyUnits() < 10)
			army_units +=" ";
		    if (c.getPlayer() != null)
			return_string = " "+continent.getSymbol()+c.getPlayer().getLetter()+army_units;
		    else
			return_string = " "+continent.getSymbol()+"_"+army_units;
		} else
			return_string = "     ";
		return return_string;
	}
	private Continent makeContinentFromName(String name) {
		Continent result = null;
		// Go through array until either we've reached the end
		// or symbol is found
		for (int i = 0; (i< Array.getLength(continents) && (result == null)) ;i++ )
		{
			// If continent at position == name, found it
			if (continents[i].getName().equals(name))
			{
				result = continents[i];
			}
		}
		return result;
	}
	// Takes (int) row and (char) col
	// Returns a Country if there is one at that position
	// Returns null if there is none or position is out of bounds

	public Country getValidTerritory(int row, char col) {
		// Default return is null if not found
		Country result = null;
		// convert char to array int
		int colnum = (int)col - 64;
		// Protection from out of bounds
		if ((territories.length > row) && (row > -1))
		{
			if (territories[0].length > colnum)
			{
				if (territories[row][colnum] != null)
				{
					result = territories[row][colnum];
				}
			}
		}
		return result;
	}
	
	public void setPlayers(int players) {
		numberOfPlayers = players;

	}

	// Prompts user for a row and column choice
	// Will only return a Country, never null
	// String prompt is repeated to user every time row/coulmn is prompted
	
	private Country getCountryChoice(String prompt) {
	    boolean valid_input = false;
	    Country chosen = null;
	    while (!valid_input)
	    {
		System.out.print(makeBoard());
		System.out.println(prompt);
		int row = -1;
		System.out.print("Row: ");
		if (in.hasNextInt())
		{
			row = in.nextInt();
		} else {
			String garbage = in.next();
		}
		char column = '0';
		System.out.print("Column: ");
		if (in.hasNext())
		{
			column = in.next().toUpperCase().charAt(0);
		}
		chosen = getValidTerritory(row, column);
		if (chosen != null)
		    valid_input = true;
	    }

	    return chosen;
	}

	public int getBonusForPlayer(Player player) {
	    int bonusArmyUnits = 3;
	    int numTer = player.getNumCountries();
	    // Player gets additonal army units for every 3 territories
	    // over 9 that he owns
	    if (numTer > 9)
		bonusArmyUnits += ((int)(numTer - 9)/3);

	  //  System.out.println("You own "+numTer+" so a we're up to "+bonusArmyUnits);
	    // Check to see if player owns an entire continent,
	    // if so, more bonus
	    for (int i = 0; i < continents.length; i++) {
		// Check to see if this continent is conquered by one player
		Player playerConquer = continents[i].onePlayerConquer();
		if (playerConquer != null) {
		    // Is it us?
		    if (playerConquer.equals(player)) {
		//	System.out.println("You own all of "+continents[i].getName()+"! Bonus of "+continents[i].getReinforcements());
			bonusArmyUnits += continents[i].getReinforcements();
		    }
		}
	    }
	    return bonusArmyUnits;
	}



	public boolean placeArmyUnit(Player player, Country country, int amount) {
	    if (country == null || player == null)
		return false;
	    
	    if (!allTerritoriesOwned) {
		if (country.getPlayer() == null) {
		    reassignCountry(player, country);
		    country.setArmyUnits(amount);
		    territoriesPlaced++;
		    if (territoriesPlaced == territory_size) {
			allTerritoriesOwned = true;
			ConsoleApp.println("All territories placed");
		    }

		    return true;
		}
	    } else {
		if (country.getPlayer().equals(player)) {
		    country.setArmyUnits((country.getArmyUnits() + amount));
		    return true;
		}
	    }

	    return false;
	}

	public Country[][] getAllTerritories() {
	    return territories;
	}
	public boolean getAllTerritoriesOwned() { return this.allTerritoriesOwned; }

	
	public void reassignCountry(Player new_owner, Country country) {
	//    System.err.println("Given P: " + new_owner + " C: " + country);
	//    System.err.println("Taking from " + country.getPlayer());
	    // Remove ownership of old player
	    if (country.getPlayer() != null)
		country.getPlayer().removeCountry(country);
	    
	    // Set new ownership
	    country.setPlayer(new_owner);
	    new_owner.addCountry(country);
	//    System.err.println("Now P: " + new_owner + " C: " + country);
	}

}
