/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public class AlertChat extends AlertMsg {
    private String message;
    public AlertChat(Object from, String msg) {
	super(from, AlertMsg.CHAT_MESSAGE);
	setMsg(msg);
    }
    public void setMsg(String msg) {
	message = msg;
    }
    public String getMsg() {
	return message;
    }
    public String toString() {
	return super.toString()+ " Chat Message: "+message;
    }
}