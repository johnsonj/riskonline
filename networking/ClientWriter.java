/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/
package networking;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;
import console.ConsoleApp;

/*
 * Handles writting to client
 */
public class ClientWriter extends ClientConnection implements Runnable {

    private static final int WRITE_BUFFER_SIZE = 50;
    private PrintWriter out;
    //   private ReentrantLock writeLock;
    //private final Condition writeTime;

    public ClientWriter(Socket sock) {
	super(sock, WRITE_BUFFER_SIZE);
	//writeLock = new ReentrantLock();
	//writeTime = writeLock.newCondition();
	//writeLock.lock();
    }

    public synchronized void run() {
	out = null;
	try {
	    out = new PrintWriter(socket.getOutputStream(), true);
	} catch (IOException ioe) {
	    ClientConnection.consoleError("Error in ClientWriter (" + Thread.currentThread().getName() + ": " + ioe, false);
	}
	while (socket.isConnected() && out != null) {
	    if (super.bufferIsEmpty()) {
		try {
		    wait();
		} catch (InterruptedException e) {
		}
	    }
	    while (!super.bufferIsEmpty()) {
		out.println(super.readBuffer());
	    }
	    //  try {Thread.sleep(100);} catch(InterruptedException e) { }
	}
	ConsoleApp.println("Client writter dead");
    }

    public void rawWrite(String text) {
	if (out != null) {
	    out.print(text);
	    out.flush();
	}
    }

    public synchronized boolean write(String message) {
	boolean result = super.writeBuffer(message);

	if (message != null) {
	    notify();
	}

	return result;
    }

    public synchronized void destory() {
	out.close();
	out = null;
	notify();
    }
}
