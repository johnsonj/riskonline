/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public class AlertGeneral extends AlertMsg {
    private String message;
    public AlertGeneral(Object from, int code, String msg) {
	super(from, code);
	setMsg(msg);
    }
    public void setMsg(String msg) {
	message = msg;
    }
    public String getMsg() {
	return message;
    }
    public String toString() {
	return super.toString() + " General Message: " +message;
    }
}
