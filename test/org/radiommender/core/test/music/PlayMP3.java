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
package org.radiommender.core.test.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map.Entry;

import org.radiommender.model.Song;
import org.radiommender.model.SongFile;


public class PlayMP3 implements Runnable {
	Boolean activated = true;
	
	
	@Override
	public void run() {
		org.radiommender.songhandler.MusicStorageWatcher lib;
				
		try {
				lib = new org.radiommender.songhandler.MusicStorageWatcher(new File("/home/bkey/BlackViolin")).initSongMapping();
				
				for(Entry<Integer, SongFile> entry : lib.getSongMapping().entrySet()){
					SongFile songFile = entry.getValue();
					File f = songFile.getFile();
					Song s = songFile.getSong();
					
					while(activated) {
					System.out.println(s.getArtist()+s.getAlbum()+s.getTitle()+s.getGenre());
					System.out.println();
					
						try {
							FileInputStream stream = new FileInputStream(f);
							javazoom.jl.player.Player testPlayer = new javazoom.jl.player.Player(stream);
							
							testPlayer.play();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
					
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void deactivate() {
		activated = false;
	}
	
}