package p2pct.tests.gui;

import java.awt.EventQueue;

/**
 * @author Marium Zeeshan
 *
 */
public class UI_Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    UI_Module frame = new UI_Module();
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    }


