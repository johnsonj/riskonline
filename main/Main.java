/******************************************
 ** eRisk 98 - a networked text based	***
 ** domination experience		***
 ******************************************
 ** By Jeffrey Johnson			***
 ** <jjohns14@iit.edu>			***
 ******************************************/

package main;

import networking.NetworkPlayer;
import networking.Listener;
import alert.AlertPM;
import alert.AlertChat;
import alert.AlertMsg;
import alert.AlertGeneral;
import riskgame.Player;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import console.*;

public class Main {
    public static final int portnum = 6969;

    public static void main(String[] args) {
	ConsoleApp.main(null);
	
	try { Thread.sleep(3000); } catch (InterruptedException iee) {}
	ConsoleApp.println("");
	ConsoleApp.println("Starting server...");
	Waiter AlertMain =  new Waiter();
	Listener server = new Listener(5, 6969, AlertMain);
	Thread serverThread = new Thread(server);
	serverThread.start();
	GameHandler game = new GameHandler(AlertMain, server);
	while (true) {
	    synchronized (AlertMain) {
		AlertMsg alert = AlertMain.pauseAlert();
		ConsoleApp.println("Main got alert: "+alert);
		switch (alert.getAlertCode()) {
		    case (AlertMsg.CHAT_MESSAGE): {
			   Player sender = (Player)alert.getSender();
			   server.writeToAllButOneClient(sender,
			    "["+sender.getName()+"]: "+((AlertChat)alert).getMsg());
		    break;
		    }
		    case (AlertMsg.CHANGE_NANE): {
			//System.out.println("Change name");
			server.writeToAllClients(((AlertGeneral)alert).getMsg());
		    break;
		    }
		    case (AlertMsg.PRIVATE_MESSAGE): {
			AlertPM msg = ((AlertPM)alert);
			NetworkPlayer to = server.getPlayerByName(msg.getTo());
			if (to != null) {
			    to.writeToClient("PRIVATE MESSAGEE from \"" + ((Player)msg.getSender()).getName() + "\": "
				    + msg.getMsg());
			} else {
			    ((NetworkPlayer)msg.getSender()).writeToClient("Error: Player \""+msg.getTo()+"\" doesn't exist");
			}
		    break;
		    }
		    case (AlertMsg.LIST_USERS): {
			NetworkPlayer to = (NetworkPlayer)alert.getSender();
			NetworkPlayer[] players = server.getPlayersArray();
			to.writeToClient(" ");
			to.writeToClient("####### Player List #######");
			for (int i = 0; i < players.length; i++) {
			    to.writeToClient(""+(i+1)+". "+players[i].getName());
			}
			to.writeToClient(" ");
			break;
		    }
		    case (AlertMsg.NEW_PLAYER): {
			server.writeToAllClients(((AlertGeneral)alert).getMsg());
			break;
		    }
		    case (AlertMsg.CLIENT_DISCONNECT): {
			NetworkPlayer player = (NetworkPlayer)alert.getSender();
			server.deletePlayer(player);
			server.writeToAllClients("- "+player.getName()+" has disconnected.");
		    }
		    case (AlertMsg.READY_TO_PLAY): {
			NetworkPlayer player = (NetworkPlayer)alert.getSender();
			String param = ((AlertGeneral)alert).getMsg();
			if (server.getConnectedPlayers() >= GameHandler.MIN_PLAYERS) {
			  if (param != null) {
			      if (param.equals("chose")) {
				server.writeToAllClients("Player: "+player.getName()+" has started the game!");
				(new Thread(game)).start();
			      }
			      if (param.equals("random")) {
				server.writeToAllClients("Player: "+player.getName()+" has started the game!");
				game.setState(1);
				(new Thread(game)).start();
			      } else {
				player.writeToClient("- ERROR: Invalid paramter, try /startgame random");
			      }
			  } else {
			      // Do random by default
				server.writeToAllClients("Player: "+player.getName()+" has started the game!");
				game.setState(1);
				(new Thread(game)).start();
			  }

			} else {
			    player.writeToClient("- ERROR: Not enough players connected to do that. "
				    +GameHandler.MIN_PLAYERS+" players needed.");
			}
			
		    }
		}
	    }

	}

    }
}
