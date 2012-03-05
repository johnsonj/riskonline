
/**
 * Created in class in CS201 at IIT on 10/15/09 by Matthew Bauer and class
 * Modified by Jeffrey Johnson for use in Risk game
 */


package riskgame;

import java.util.Random;


public class Die extends Dumb_die {
	private Random random;
	private int DIE_SIZE;
	private static final int DEFAUT_DIE_SIZE=6;
	public Die() {
	    super();
	    random = new Random();
	}
	public Die(int size) {
	    super();
	    setSize(size);
	    random = new Random();
	}
	public int roll() {
	    // Random generates number from [0, n), shift one up
	    // to make it [1, n-1]
		setSide((random.nextInt(getSize()) + 1));
		return side;
	}
	public Dumb_die clone() {
	    return (new Dumb_die(this.getSide()));
	}

}