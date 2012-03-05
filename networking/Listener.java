/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/


package networking;

import main.*;
import alert.AlertMsg;
import alert.AlertGeneral;
import riskgame.Player;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.SocketException;
import console.ConsoleApp;
/**
 *
 * @author jeffrey
 */
public class Listener implements Runnable{
    private static int max_players;
    private static int connected;
    private static int portnum;
    private static final int DEFAULT_PORT_NUMBER = 6969;
    private static final int DEFAULT_PLAYERS = 2;
    private static ArrayList<NetworkPlayer> clients;
    private Waiter AlertMain;
    private boolean gameFull;

    public Listener(int p, int pn, Waiter amain) {
	if (p > 0)
	    max_players = p;
	else
	    max_players = DEFAULT_PLAYERS;

	if (pn > 0)
	    portnum = pn;
	else
	    pn = DEFAULT_PORT_NUMBER;

	if (amain == null) {
	    ConsoleApp.println("Listern passed null waiter");
	    System.exit(1);
	} else
	    AlertMain = amain;

	clients = new ArrayList<NetworkPlayer>();
    }
    public void run() {
	ServerSocket serv = null;
	//Socket sock = null;
	try
	{
	    serv = new ServerSocket(portnum);
	}
	catch (UnknownHostException uhe)
	{
	    ClientConnection.consoleError("UnknownHostException\nError:"+uhe, true);
	} catch (IOException ioe) {
	    ClientConnection.consoleError("Can not bind port\nError:"+ioe, true);
        }
	ConsoleApp.println("Binded to port ("+portnum+"), waiting for connections....");
	Socket connection = null;

	while (max_players > connected) {

	    try {
		connection = serv.accept();
	//	clientWrite = new ClientWriter(connection);
	//	clientRead = new ClientReader(connection, clientWrite);
		//out = new PrintWriter(sock.getOutputStream(), true);
		//in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    } catch (IOException e) {
		ConsoleApp.println("Can't get I/O");
	    }
	    try {
		connection.setKeepAlive(true);
	    } catch (SocketException se) {
		ConsoleApp.println("Socket Exception in Listener on socket open: "+se);
	    }
	    NetworkPlayer newPlayer = new NetworkPlayer(connection, AlertMain);
	    if (addPlayer(newPlayer)) {
		AlertGeneral alert = new AlertGeneral(newPlayer,
			AlertMsg.NEW_PLAYER, " -- "+ newPlayer.getName() + " has now connected -------");
		AlertMain.go(alert);
	    }
	}
    }
    public boolean addPlayer(NetworkPlayer client) {
	if (max_players > connected && !gameFull) {
	    clients.add(client);
	    connected++;
	    return true;
	} else {
	    if (client != null) {
		client.writeToClient("Sorry game is full, try again later");
		client.disconnect();
	    }
	    return false;
	}
    }
    public NetworkPlayer[] getPlayersArray() {
	NetworkPlayer[] n = new NetworkPlayer[clients.size()];
	return clients.toArray(n);
    }
    public NetworkPlayer getPlayerById(int id) {
	if (clients.size() > id)
	    return clients.get(id);
	else
	    return null;
    }
    public int getMaxPlayerCount() { return this.max_players; }
    public NetworkPlayer getPlayerByName(String name) {
	NetworkPlayer p = null;
	for (int i = 0; ((i < clients.size()) && (p == null)); i++) {
	    if ((clients.get(i) != null) && (clients.get(i).getName() != null))
	    if (clients.get(i).getName().equals(name))
		p = clients.get(i);
	}
	return p;
    }
    public void writeToAllClients(String message) {
	if (clients.size() == 0)
	    return;
	for (int i = 0; i < clients.size(); i++) {
	    clients.get(i).writeToClient(message);
	}
    }
    public void gameWriteToAllClients(String message) {
	if (clients.size() == 0)
	    return;
	for (int i = 0; i < clients.size(); i++) {
	    clients.get(i).gameWrite(message);
	}
    }
    public void gameWriteMultilineToAllClients(String message) {
	String[] msg = message.split("\n");
	for (int i = 0; i < msg.length; i++) {
	    gameWriteToAllClients(msg[i]);
	}
    }
    public void writeToAllButOneClient(Player ignore, String message) {
	if (clients.size() == 0)
	    return;
	for (int i = 0; i < clients.size(); i++) {
	    Player current = clients.get(i);
	    if (!ignore.equals(current))
		clients.get(i).writeToClient(message);
	}
    }
    public void setGameFull(boolean gf) { this.gameFull = gf; }
    public int getConnectedPlayers() { return connected; }
    public NetworkPlayer anyData() {
	if (clients.size() == 0)
	    return null;
	
	NetworkPlayer found = null;
	for (int i = 0;  ((i < clients.size()) && (found == null)); i++) {
	    if (clients.get(i).hasData())
		found = clients.get(i);
	}
	return found;
    }
    public boolean deletePlayer(NetworkPlayer p) {
	clients.remove(p);
	p.disconnect();
	

	return true;
    }
    public static NetworkPlayer[] getClients() {
	NetworkPlayer cl[] = new NetworkPlayer[connected];
	return Listener.clients.toArray(cl);
    }

}
