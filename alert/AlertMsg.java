/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public abstract class AlertMsg {
    public static final int CHAT_MESSAGE = 100;
    public static final int CHANGE_NANE = 102;
    public static final int PRIVATE_MESSAGE = 110;
    public static final int LIST_USERS = 201;
    public static final int NEW_PLAYER = 202;
    public static final int CLIENT_DISCONNECT = 300;
    public static final int READY_TO_PLAY = 301;
    public static final int GAME_INPUT = 400;
    private int alertCode;
    private Object sender;

    public AlertMsg() {

    }
    public AlertMsg(Object s, int aC) {
	alertCode = aC;
	sender = s;
    }
    public int getAlertCode() {
	return alertCode;
    }
    public Object getSender() {
	return sender;
    }
    public String toString() {
	return "Alert Code: "+alertCode+" Sender: "+sender;
    }
}
