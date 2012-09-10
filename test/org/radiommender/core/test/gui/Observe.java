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
package org.radiommender.core.test.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;


import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Observe extends Observable implements Runnable{
    
	Logger logger = LoggerFactory.getLogger(Observe.class);
	
    private boolean m_keepRunning=true;
    private String Song= new String();
    ObservableValue ob= new ObservableValue(null);
    public Observe()
    {
        logger.debug("Creating the observable now..");
    }

    public void startEngine()
    {
        logger.debug("Starting the observable engine.");
        m_keepRunning=true;
        new Thread(this).start();
    }

    public void stopEngine()
    {
        logger.debug("Stopping the observable engine.");
        m_keepRunning=false;
    }

    /**
    * Starts a thread which does the dumb job of notifying all observers every 3 seconds
    * with a new Integer
    *
    * @see Thread#run()
    */
    public void run()
    {
        int count=0;
        while(m_keepRunning)
        {
            try
            {
                Thread.sleep(3000);
                count++;
                logger.debug("Tester"+count);
                /*Song.add(count);      //I tried to do it with the string list....
                Song.add("mahi");
                Song.add("dhejwkh");
                for ( Iterator Iter = Song.iterator(); Iter.hasNext(); ) {
            	      logger.debug( Iter.next() );
                   }*/
                ob.setSong("bdje");   ///Just add the changing values here that are needed to be updated
                
               //Song.clear();  //clearance of song list(if the input from the player is in the string list form)
                
                
            
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            }
    } 

}
