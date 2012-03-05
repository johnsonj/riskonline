
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package riskgame;
import networking.*;
import main.*;
import alert.*;

/**
 *
 * @author jeffrey
 */
public class TestBoard {
	public static void main(String[] args) {
	Waiter AlertMain =  new Waiter();
	Listener server = new Listener(5, 6969, AlertMain);
	Thread serverThread = new Thread(server);
	System.out.println("BOARD TEST-------------");
	serverThread.start();
	//GameHandler game = new GameHandler(AlertMain, server);
	Board board = new Board("F:/school/CS201/Lab 10/RiskBoard.txt");
	System.out.println(board.makeBoard());
	while (true) {
	    synchronized (AlertMain) {
		AlertMsg alert = AlertMain.pauseAlert();
		//(new Thread(game)).start();
		server.writeToAllClients("Hello");
		String[] ba = board.makeBoard().split("\n");
		server.getPlayerById(0).setWriteLock(false);
		server.getPlayerById(0).setState(1);
		System.out.println(server.getPlayerById(0));
		for (int i = 0; i < ba.length; i++) {
		    server.getPlayerById(0).gameWrite(ba[i]);
		//    server.writeToAllClients(ba[i]);
		//    server.getPlayerById(0).rawWrite(ba[i]+"\n");
		    System.out.println(ba[i]);
		}
	    }
	}
	}
}
