/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package networking;

import java.net.Socket;

/*
 * Outlines functions for both write/reade interfaces
 */
public abstract class ClientConnection {
    private String[] buffer;
    private boolean buffer_full;
    private boolean buffer_empty;
    private int buffer_size;
    private int buffer_read_pos;
    private int buffer_write_pos;
    private static final int DEFAULT_BUFFER_SIZE = 10;
    protected Socket socket;
    public ClientConnection() {
    }
    public ClientConnection(Socket sock, int b_size) {
	if (b_size > 0)
	    buffer_size = b_size;
	else
	    buffer_size = DEFAULT_BUFFER_SIZE;

	buffer = new String[buffer_size];
	socket = sock;

	buffer_full = false;
	buffer_empty = true;
	buffer_read_pos = 0;
	buffer_write_pos = 0;
    }

    /*
     * Reads one line from buffer if there is data, else returns null
     */
    protected String readBuffer() {
	if (bufferIsEmpty())
	    return null;
	// Read current read position if there is data
	String read = buffer[buffer_read_pos];

	advanceReadBuffer();

	buffer_full = false;

	return read;
    }
    protected String readBuffer(boolean retain) {
	if (bufferIsEmpty())
	    return null;
	
	// Read current read position if there is data
	String read = buffer[buffer_read_pos];

	buffer_full = false;

	if (!retain)
	    advanceReadBuffer();
	
	return read;

    }
    protected void advanceReadBuffer() {
	// Increment current read position
	buffer_read_pos++;

	    // If we're at the end of the array reset position
	if (buffer_read_pos == buffer_size )
	    buffer_read_pos = 0;

	// Pointers are at the same location and we just read
	// That means we must of emptied the buffer
	if (buffer_read_pos == buffer_write_pos)
	    buffer_empty = true;
    }
    protected boolean writeBuffer(String write) {
	if (bufferIsFull())
	    return false;

	buffer[buffer_write_pos] = write;

	buffer_write_pos++;
	if (buffer_write_pos == buffer_size)
	    buffer_write_pos = 0;

	if (buffer_write_pos == buffer_read_pos)
	    buffer_full = true;

	buffer_empty = false;

//	System.out.println("buffer_write_pos: "+buffer_write_pos);

	return true;
    }

   /*
    * Returns true if buffer is full
    * Returns false if buffer has room
    */
    protected boolean bufferIsFull() {
	if (buffer_full)
	    return true;
	else
	    return false;
    }

    /*
     * Returns true if buffer is empty
     * Returns false if buffer has data
     */
    protected boolean bufferIsEmpty() {
	if (buffer_empty)
	    return true;
	else
	    return false;
    }

    public abstract boolean write(String message);
    public abstract void run();

    static void consoleError(String message, boolean deadly) {
	System.err.println("ERROR ############################\nThread: "
		+Thread.currentThread().getName()+"\nMessage: "+message);
	if (deadly)
	    System.exit(1);
    }

    protected void sleep(int time) {
	if (time > 0)
	    try { Thread.sleep(100); } catch (InterruptedException iee) { }
    }
}
