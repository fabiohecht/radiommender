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
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TextObserver implements Observer {
    
	Logger logger = LoggerFactory.getLogger(TextObserver.class);
	
    private ObservableValue user_input;
   
    private Object tmpo;
    
    public TextObserver(ObservableValue user_input)
    {
    
	this.user_input=user_input;
	
    }
    public void update(Observable obs, Object obj)
    {     
    
       //for ( Iterator Iter = user_input.iterator(); Iter.hasNext(); ) {
	    //  logger.debug( Iter.next() );
       //logger.debug(user_input);
      // }
       
        ObservableValue tmp = (ObservableValue) obs;
        //logger.debug("++" + tmp.getSong() + "++");
        this.tmpo=tmp.getSong()+"Change";
        
       logger.debug("+++"+this.tmpo+"+++");
      }

    /**
     * @return
     */
    public String GetValue(){
	return (String) this.tmpo;
    }
    
    
    }

