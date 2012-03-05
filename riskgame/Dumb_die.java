/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package riskgame;

public class Dumb_die implements Comparable<Dumb_die> {
	protected int side;
	protected static final int DEFAULT_SIDE=1;
	protected int DIE_SIZE;
	protected static final int DEFAUT_DIE_SIZE=6;
	public Dumb_die() {
	    setSide(DEFAULT_SIDE);
	    setSize(DEFAUT_DIE_SIZE);
	}
	public Dumb_die(int side_on) {
	    setSide(DEFAULT_SIDE);
	    setSize(DEFAUT_DIE_SIZE);
	    setSide(side_on);
	}
 	public String toString() {
	    return "Die=" + side;
	}
	protected void setSide(int newSide) {
		if (newSide>=1 && newSide<=DIE_SIZE) side=newSide;
		else side=DEFAULT_SIDE;
	}
	public boolean equals(Dumb_die that) {
		if (this.getSide()==that.getSide()) return true;
		else return false;
	}
	public int getSide() {
		return side;
	}
	protected void setSize(int size) {
	    if (size > 1)
		DIE_SIZE = size;
	}
	public int getSize() { return DIE_SIZE; }
	public int compareTo(Dumb_die that) {
	    if (this.equals(that))
		return 0;
	    else if (this.getSide() < that.getSide())
		return -1;
	    else
		return 1;
	}
}