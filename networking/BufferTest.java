/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package networking;

/**
 *
 * @author jeffrey
 */
public class BufferTest extends ClientConnection {
    public BufferTest() {
	super(null, 10);
	for (int i = 0; i < 100; i++) {
	    System.out.println("i="+i+" "+super.writeBuffer("I'm Number:" + i));
	}
	while (!super.bufferIsEmpty()) {
	    System.out.println(super.readBuffer());
	}
    }
    public static void main(String[] args) {
	new BufferTest();
    }
    public void run() { return; }
    public boolean write(String e) { return true; }

}
