/******************************************
 ** eRisk 98 - a networked text based	***
 ** game of world domination		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package networking;
import main.*;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
/**
 * Handles reading from client
 */
public class ClientReader extends ClientConnection implements Runnable {
    private static final int READ_BUFFER_SIZE = 5;
    ClientWriter writer;
    Waiter waiter;
    BufferedReader in;
    public ClientReader(Socket sock, ClientWriter w, Waiter wait) {
	super(sock, READ_BUFFER_SIZE);
	writer = w;
	waiter = wait;
    }
    public void destory() {
	try {
	in.close();
	} catch (IOException ioe) {}
	in = null;
	
    }
    public void run() {
	in = null;
	try {
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   // PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
	} catch (IOException ioe) {
	    ClientConnection.consoleError("ClientReader can't get input stream\nIOException: "+ioe, true);
	}

	while (socket.isConnected() && in != null) {
	    try {
		 String message = in.readLine();
		// write.writeMessage("-> " + message);
		 if (!super.writeBuffer(message)) {
		     write("ERROR: Slow down, too much data");
		 } else {
		     waiter.go();
		 }
		 super.sleep(100);
	    } catch (IOException ioe) {
		ClientConnection.consoleError("ClientReader can't read from input stream\nIOException: "+ioe, true);
	    }

	}
	System.out.println("Client reader dead");
    }
    public boolean write(String message) {
	return writer.write(message);
    }
    public boolean canRead() {
	return(!super.bufferIsEmpty()); 
    }
    public String read() {
	return super.readBuffer();
    }
    public String read(boolean retain) {
	return super.readBuffer(retain);
    }
}
