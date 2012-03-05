/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alert;

/**
 *
 * @author jeffrey
 */
public class AlertClientDisconnected extends AlertMsg {
    public AlertClientDisconnected(Object client) {
	super(client, AlertMsg.CLIENT_DISCONNECT);
    }
}
