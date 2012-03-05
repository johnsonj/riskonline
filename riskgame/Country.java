/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package riskgame;
public class Country
{
	private static int instantiations;
	private String name;
	private String continent;
	private Player player;
	private int row;
	private char column;
	private int army_units;
	private static final char MAX_COL = 'K';
	private static final char MIN_COL = 'A';
	private static final int MAX_ROW = 9;
	private static final char DEFAULT_PLAYER = '_';

	public Country(String n, String c, int r, char col, int a_u) {
		setName(n);
		setContinent(c);
		setRow(r);
		setColumn(col);
		setArmyUnits(a_u);
		instantiations++;
		setPlayer(null);
	}

	public int getInstances() {
		return instantiations;
	}

	public void setName(String n) {
			if (n != null)
				this.name = n;
			else
				System.err.println("Error: Country.setName(String n) sent null. \n" + toString());
	}
	/*
	public void setPlayer(char p) {
		if ((p <= 'Z') && (p >= 'A') || (p == '_'))
		{
			player = p;
		}
	}
	 */
	public void setPlayer(Player p) {
	    player = p;
	}
	/*
	public char getPlayer() {
		return player;
	}
	*/
	public Player getPlayer() {

	    return player;
	}
	public void setContinent(String c) {
		if (c != null)
			this.continent = c;
		else
			System.err.println("Error: Country.setContinent(String c) sent null. \n" + toString());
	}
	public String getName() {
		return this.name;
	}
	public String getContinent() {
		return this.continent;
	}
	public void setRow(int r) {
		if ((r <= 9) && (r >=1))
			this.row = r;
		else
			System.err.println("Invalid row number (" + r + "), must integar be between 1-9; \n" + this.toString());
	}

	public int getRow() {
		return this.row;
	}
	public static int getMAXROW() {
		return Country.MAX_ROW;
	}
	public static char getMAXCOL() {
		return Country.MAX_COL;
	}

	public void setColumn(char col) {
		// validate col:
		if (Character.isLetter(col))
			this.column = Character.toUpperCase(col);
		else
			System.err.println("Invalid column letter (" + col + "), must letter be between A-Z; \n" + this.toString());

	}

	public char getColumn() {
		return this.column;
	}

	public void setArmyUnits(int a_u) {
		if ((a_u <= 40) && (a_u >= 0))
			this.army_units = a_u;
		else if (a_u < 0)
			this.army_units = 0;
		else
		System.err.println("Invalid army units value (" + a_u + "); \n" + this.toString());
	}

	public int getArmyUnits() {
		return this.army_units;
	}

	public String toString() {
		return "Country: " + this.getName() + " Located at: "+this.getColumn()+this.getRow()+" On: "+this.getContinent()+" with: " +this.getArmyUnits() +
		" army units. Country class has "+this.getInstances()+" instances";
	}
	public boolean nextTo(Country otherCountry) {
		int otherCountryRow = otherCountry.getRow();
		char otherCountryCol = otherCountry.getColumn();

		int myRow = getRow();

		char myCol = getColumn();
		char myColUp = (char)(((int)myCol) + 1);
		char myColDown = (char)(((int)myCol) - 1);

		// Handle wrap around
		if (myCol == MIN_COL)
		    myColDown = getMAXCOL();
		if (myCol == getMAXCOL())
		    myColUp = MIN_COL;

		boolean return_value = false;

		if ((otherCountryRow == (myRow + 1)) || (otherCountryRow == (myRow - 1)) || (otherCountryRow == myRow))
		{
			if ((otherCountryCol == myCol) || (otherCountryCol == myColUp) || (otherCountryCol == myColDown))
			{
				return_value = true;
			}
		}

		return return_value;
	}

	public boolean equals(Country otherCountry) {
		int otherCountryRow = otherCountry.getRow();
		char otherCountryCol = otherCountry.getColumn();
		int myRow = getRow();
		char myCol = getColumn();
		boolean return_value = false;

		if ((otherCountryRow == myRow) && (otherCountryCol == myCol))
		{
			return_value = true;
		}

		return return_value;
	}

}
