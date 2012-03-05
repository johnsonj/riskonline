/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package riskgame;

import java.util.*;
import java.util.regex.*;
public class Player {
    private ArrayList<Country> countries;
    private char player_letter;
    private int myid;
    private static int instantiations;
    private Die dice[];
    public static final int MAX_DICE = 3;
    public static final int DIE_SIZE = 6;
    private String name;
    private int army_unit_bonus;

    public Player(char p_l, char setid) {
	player_letter = p_l;
	myid = setid;
	setName("Player"+ this.getLetter());
//	pipe = cr;
	countries = new ArrayList<Country>();
	initDie();
    }
    public Player() {
	//pipe = cr;
	instantiations++;
	myid = instantiations;
	player_letter = (char)(myid + 64);
	setName("Player"+ this.getLetter());
	countries = new ArrayList<Country>();
	initDie();
    }
    private void initDie() {
	dice = new Die[MAX_DICE];
	for (int i = 0; i < MAX_DICE; i++)
	    dice[i] = new Die(DIE_SIZE);
    }
    public char getLetter() {
	return player_letter;
    }
    public int getId() {
	return myid;
    }
    public void addCountry(Country c) {
	if (c != null)
	    countries.add(c);
    }

    public void removeCountry(Country c) {
	if (c != null)
	    countries.remove(c);
    }

    public Object[] getCountries() {
	return countries.toArray();
    }
    public int getNumCountries() {
	return countries.size();
    }
    public boolean equals(Player that) {
	boolean result = false;
	if (that.getId() == this.getId())
	    result = true;

	return result;
    }

    public boolean setName(String n) {
	if (n != null) {
	  //  if (n.matches("[A-Z][a-zA-Z]*?")) {
	    if (n.indexOf(" ") < 0) {
		name = n;
		return true;
	    } else {
		return false;
	    }
	} else
	    return false;
    }
    public String getName() {
	return name;
    }
    public int getArmyUnitBonus() { return army_unit_bonus; }
    public void setArmyUnitBonus(int a_u) { if (a_u >= 0) army_unit_bonus = a_u; }

    // Returns dumb_die clones of int num dice
    // Sorted in decending order
    public Dumb_die[] rollDice(int num) {
	if (num > MAX_DICE)
	    return null;

	Dumb_die return_dice[] = new Dumb_die[num];
	ArrayList<Dumb_die> to_sort = new ArrayList<Dumb_die>(num);

	for (int i = 0; i < num; i++) {
	    // Roll our die
	    dice[i].roll();
	    // Pass a dumb dice to an array list
	    to_sort.add(dice[i].clone());
	}
	// Sort dumb die
	Collections.sort(to_sort);
	Collections.reverse(to_sort);
	return_dice = to_sort.toArray(return_dice);

	return return_dice;
    }
}
