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

import java.util.List;
import java.util.Observable;

/**
 * @author Marium Zeeshan
 *
 */
public class ObservableValue extends Observable{
    
    private String Song;
    
    /**
     * @param genre
     * @param Album
     * @param Artist
     */
    public ObservableValue(String Song)
    {
       this.Song=Song;
       //addObserver(new TextObserver);
       /*this.genre=genre;
       this.Album = Album;
       this.Artist=Artist;
       */
    }
    public String getSong(){
	return this.Song;
    }
    
    public void setSong(String Song) {
        this.Song = Song;
        //logger.debug("Set Song");
        setChanged();
        notifyObservers();
    }


  }
