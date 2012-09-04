/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.tests.gui;

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


