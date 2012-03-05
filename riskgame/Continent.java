/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package riskgame;
import java.util.ArrayList;

public class Continent
{
	private String name;
	private char symbol;
	private int reinforcements;
	private ArrayList<Country> countries;

	public Continent(String n, char s, int reinf) {
		setName(n);
		setSymbol(s);
		setReinforcements(reinf);
		countries = new ArrayList<Country>();
	}
	public void setName(String n) {
		if (!(n == null))
			name = n;
		else
			System.err.println("Error: Continent.setName(string n) sent null \n" + toString());
	}
	public void addCountry(Country c) {
		countries.add(c);
	}

	public void removeCountry(Country c) {
	    countries.remove(c);
	}

	public Object[] getCountries() {
	    return countries.toArray();
	}
	public int getNumCountries() {
	    return countries.size();
	}
	public void setSymbol(char s) {
		symbol = s;
	}
	public Player onePlayerConquer() {
	    // Set player result to first player who owns a territory in the country
	    Player result = countries.get(0).getPlayer();
	    // Loop until through or result becomes null
	    // Result becomes null if it finds a player not equal to the first player
	    for (int i = 0; ((i < getNumCountries()) && (result != null)); i++)
			if (!result.equals(countries.get(i).getPlayer())) result = null;

	    return result;
	}
	public void setReinforcements(int reinf) {
		if (reinf >= 0)
			reinforcements = reinf;
		else
			System.err.println("Error: Continent.setReinforcements(int reinf) sent invalid value ("+reinf+") \n"+ toString());
	}
	public String getName() {
		return name;
	}
	public char getSymbol() {
		return symbol;
	}
	public int getReinforcements() {
		return reinforcements;
	}
	public String toString() {
		return "Continent: " + getName() + " at: " + getSymbol() + " with a: " + getReinforcements() + " bonus.";
	}

}
