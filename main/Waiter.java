/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import alert.AlertMsg;

/**
 *
 * @author jeffrey
 */
public class Waiter {
    private String message;
    private boolean have_message;
    private Object notifyObject;
    private Thread holder;
    private AlertMsg alert;
    private int code;


    public Waiter() {

    }
    public synchronized void pause() {
	try {wait();} catch(InterruptedException e) { }
    }
    public synchronized AlertMsg pauseAlert() {
	pause();
	return alert;
    }
    public synchronized Object pause(boolean expectObject) {
	Object return_obj = null;

	holder = Thread.currentThread();
	
	    pause();
	    if (expectObject) {
		return_obj = notifyObject;
		notifyObject = null;
	//	System.out.println("Pausing got "+return_obj);
	    }

	

	return return_obj;
    }
    public synchronized void go() {

	holder = null;
	notify();
    }
    public synchronized void go(AlertMsg a) {
	alert = a;
//	System.out.println("Waiter got"+a);
	holder = null;
	notify();
    }
    public synchronized void go(Object object, int c, String msg) {
	notifyObject = object;
	code = c;
	sendMsg(msg);
	notify();
    }
    public void sendMsg(String msg) {
	if (msg == null || have_message)
	    return;

	have_message = true;
	message = msg;
    }
    public String getMsg() {
	if (!have_message)
	    return null;

	have_message = false;

	return message;
    }
    public int getCode() {
	return code;
    }
    public boolean haveMessage() {
	return have_message;
    }
}
