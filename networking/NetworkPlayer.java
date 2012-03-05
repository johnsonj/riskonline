/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;
import main.*;
import alert.*;
import riskgame.Player;
import java.net.Socket;
import java.io.IOException;
import networking.Listener;
import console.ConsoleApp;

/**
 *
 * @author jeffrey
 */
public class NetworkPlayer extends Player implements Runnable {
    private ClientWriter write;
    private ClientReader read;
    private Thread t_w;
    private Thread t_r;
    private Thread t_player;
    private Waiter ActionWaiter;
    private final Waiter AlertMain;
    private Waiter GameWaiter;
    private Socket socket;
    private String chat_data;
    private int state;
    private int STATE_CHAT = 0;
    private int STATE_FORCE_NAME_CHANGE = 1;
    private boolean outsideWriteLock;
    private boolean my_turn;
    
    
    public NetworkPlayer (Socket sock, Waiter amain) {
	super();

	ConsoleApp.println("New player connected on "+sock.getInetAddress());
	ActionWaiter = new Waiter();
	AlertMain = amain;
	socket = sock;
	
	if (sock != null) {
	    write = new ClientWriter(sock);
	    read = new ClientReader(sock, write, ActionWaiter);
	} else {
	    System.err.println("Can not open null socket");
	    System.exit(1);
	}
	(t_w = new Thread(write)).start();
	(t_r = new Thread(read)).start();
	(t_player = new Thread(this)).start();

    }
    public void disconnect() {
	try {
	    socket.close();
	} catch (IOException ioe) {
		System.err.println("Error IOE in NetworkPlayer\n"+ioe);
	}
	read.destory();
	write.destory();
	read = null;
	write = null;

	ActionWaiter.go();

    }
    public void run() {
	write.write("Please enter your in game name: (no spaces)");
	this.state = this.STATE_FORCE_NAME_CHANGE;
	outsideWriteLock = true;
	while (socket.isConnected() && write != null && read != null) {
	    // Should pause till our buffer has input or we need to do something
	//    ConsoleApp.println("Paused");
	    if (state == this.STATE_CHAT)
		write.rawWrite("["+this.getName()+"] ");
	    
	    ActionWaiter.pause();
	//    ConsoleApp.println("Go");
	    if (read != null && !read.bufferIsEmpty()) {
		String input = read.readBuffer(true);
		ConsoleApp.println("Input: "+input);
		if (input == null) {
		    // If the client passes null they probabbly disconnected
		    AlertMain.go(new AlertClientDisconnected(this));
		} else {
		    if (this.my_turn) {
			 AlertGame alert = new AlertGame(this, input);
			 ConsoleApp.println("Players turn and passing passing:" +alert);
			 GameWaiter.go(alert);
		    } else if (!processCommand(input)) {
		//    AlertMain.sendMsg(input);
			synchronized (AlertMain) {
			    AlertChat alert = new AlertChat(this, input);
			    AlertMain.go(alert);
			}
			ConsoleApp.println("Need to process: "+input);
		    }
		}
		read.advanceReadBuffer();
	    }
	}
	ConsoleApp.println("Network player dead");
	//this.disconnect();

    }
    public boolean processCommand(String input) {
	if (input == null)
	    return false;
	boolean valid_cmd = false;
	int space_pos = input.indexOf(" ");
	String cmd = input;
	String param = null;
	if (space_pos > 0) {
	    cmd = input.substring(0, space_pos);
	    param = input.substring((space_pos +1), input.length());
	}
	switch (state) {
	    case 2: {
		// It's our turn and we are expected input, give it straight to
		// the game handler object
		AlertGame alert = new AlertGame(this, input);
		ConsoleApp.println("Player state 2 passing:" +alert);
		GameWaiter.go(alert);
		return true;
		
	    }
	    case 1: {
		/// FORCE CHANGE NAME //////////////
		String old_name = this.getName();
		if (this.setName(input)) {
		    write.write("- Welcome to the game " + this.getName() + "!");
		    AlertMain.go(new AlertGeneral(this, AlertMsg.CHANGE_NANE, 
			    old_name+" has changed their name to "+this.getName()));

		    write.write("- The game is currently in chat mode, you may chat");
		    write.write("- by plainly typing in messages or type /help to see");
		    write.write("- a list of commands currently avaliable to you");
		    try { Thread.sleep(100);} catch (Exception e) {}
		    this.state = this.STATE_CHAT;
		    this.outsideWriteLock = false;

		} else {
		    write.write("");
		    write.write("Error: invalid name choice, try again");
		    write.write("Remember: Names can not have spaces, must be a-Z");
		    write.write("");
		    write.write("Please enter your in game name: (no spaces)");
		}

	    return true;
	    }
	    case 0: {
		///////////////// STATE 0 Commands ///////////////////
	    if (cmd.equals("/version")) {
		write.write("eRisk 98 - Version 0.1-pocketing");
		valid_cmd = true;
	    }
	    if (cmd.equals("/name")) {
		String old_name = this.getName();
		AlertMsg alert = null;
		if (this.setName(param)) {
		    alert = new AlertGeneral(this, AlertMsg.CHANGE_NANE,
				old_name+" has changed their name to "+this.getName());
		    ConsoleApp.println("Alert to pass: "+alert);
		
		    AlertMain.go(alert);
		} else {
		    write.write("ERROR: Names may not have spaces");
		}
		return true;
	    }
	    if (cmd.equals("/help")) {
		write.write("COMMANDS: ");
		write.write("/version - Displays version of eRisk");
		write.write("/name [name] - Changes your name");
		write.write("/pm [user] [message] - Sends message to another user");
		write.write("/players - Lists all currently connected players");
		if (!GameHandler.gameStatus())
		    write.write("/startgame [random|chose] - Starts game with current players, specify how game is started by paramater");

		return true;
	    }
	    if (cmd.equals("/startgame")) {
		// Check to see if there's more than one player
		 AlertMain.go(new AlertGeneral(this, AlertGeneral.READY_TO_PLAY, param));

		return true;
	    }
	    if (cmd.equals("/pm")) {
		space_pos = param.indexOf(" ");
		if (space_pos > 0) {
		    String name = param.substring(0, space_pos);
		    String msg = param.substring((space_pos +1));
		    AlertPM alert = new AlertPM(this, name, msg);
		    AlertMain.go(alert);
		} else {
		    write.write("ERROR: Missing input, try /help");
		}

		return true;
	    }
	    if (cmd.equals("/players")) {
		AlertGeneral alert = new AlertGeneral(this, AlertMsg.LIST_USERS, null);
		AlertMain.go(alert);
		return true;
	    }
	    break;
	    }
	}
	return valid_cmd;
    }

    public boolean getMyTurn() { return this.my_turn; }

    public void setMyTurn(Waiter gw) {
	if (gw == null) {
	    this.GameWaiter = null;
	    this.my_turn = false;
	} else {
	    this.GameWaiter = gw;
	    this.my_turn = true;
	}
    }
    public void setState(int state) { this.state = state; }

    public boolean gameWrite(String text) {
	return write.write(text);
    }
    public boolean writeToClient(String writeToClient) {
	if (outsideWriteLock)
	    return false;
	if (write != null)
	    return write.write(writeToClient);
	else
	return false;
    }

    public String readFromClient() {
	return read.read(false);
    }

    public boolean hasData() {
	return (!read.bufferIsEmpty());
    }

    public void setWriteLock(boolean wl) { this.outsideWriteLock = true; }
    public boolean getWriteLock() { return this.outsideWriteLock; }
    public void rawWrite(String message) { this.write.rawWrite(message); }
    public void gameWriteMultiline(String message) {
	String[] msg = message.split("\n");
	for (int i = 0; i < msg.length; i++) {
	    gameWrite(msg[i]);
	}
    }
    public String toString() {
	return "NetworkPlayer ["+super.getName()+"]:"+socket.getInetAddress();
    }
}
