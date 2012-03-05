/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public class AlertGame extends AlertMsg {
    private String message;
    public AlertGame(Object from, String msg) {
	super(from, AlertMsg.GAME_INPUT);
	setMsg(msg);
    }
    public void setMsg(String msg) {
	message = msg;
    }
    public String getMsg() {
	return message;
    }
    public String toString() {
	return super.toString()+ " Game input: "+message;
    }
}