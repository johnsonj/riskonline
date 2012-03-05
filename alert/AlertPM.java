/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public class AlertPM extends AlertMsg {
    private String message;
    private String to;
    public AlertPM(Object from, String t, String msg) {
	super(from, AlertMsg.PRIVATE_MESSAGE);
	to = t;
	setMsg(msg);
    }
    public void setMsg(String msg) {
	message = msg;
    }
    public String getTo() {
	return to;
    }
    public String getMsg() {
	return message;
    }
    public String toString() {
	return super.toString()+ " Chat Message: "+message;
    }
}
