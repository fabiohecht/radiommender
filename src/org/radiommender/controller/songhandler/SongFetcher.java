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
package org.radiommender.controller.songhandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.TrackerData;

import org.radiommender.controller.P2POverlay;
import org.radiommender.model.Message;
import org.radiommender.model.Song;
import org.radiommender.model.SongFile;
import org.radiommender.utils.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handles the song fetching from the DHT and other peers.
 * It will first look for the reference in the DHT and than download the song from the other peer
 * by sending a message.
 * 
 * @author nicolas baer
 */
public class SongFetcher {
	// logger
	Logger logger = LoggerFactory.getLogger(SongFetcher.class);
	
	// configuration property
	private final File localStorage;
	
	// external modules
	private P2POverlay overlay;
	private MusicStorageWatcher musicStorageWatcher;

	
	/**
	 * default constructor
	 * - read temporary storage path from configuration file
	 */
	public SongFetcher(P2POverlay overlay, MusicStorageWatcher musicStorageWatcher){
		this.localStorage = new File(ConfigurationFactory.getProperty("file.tmp.path"));
		this.overlay = overlay;
		this.musicStorageWatcher = musicStorageWatcher;
	}
	
	/**
	 * default constructor with temporary storage path
	 * @param localStorage
	 */
	public SongFetcher(P2POverlay overlay, File localStorage){
		this.localStorage = localStorage;
		this.overlay = overlay;
	}
	
	/**
	 * 
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public SongFile retrieveSong(Song song) throws ClassNotFoundException, IOException{
		// look for song in DHT
		if(song!= null){
			HashSet<TrackerData> trackers = (HashSet<TrackerData>) this.overlay.getTrackers(song.getKey().toString());
			
			if(trackers != null && trackers.size() > 0){
				for(TrackerData tracker : trackers){
					// fetch peer address
					PeerAddress peerAddress = tracker.getPeerAddress();
					
					// check if file is stored locally
					if(!peerAddress.equals(this.overlay.getPeer().getPeerAddress())){
						Message message = new Message();
						message.setCommand(Message.CMD_FILE_REQUEST);
						message.setArguments(new Object[]{song.getKey()});
						
						logger.info("songfetcher: send message to receive file: " + song.getKey());
						
						Object response = this.overlay.sendMessage(peerAddress, message);
						
						// check response
						if(response != null && response instanceof byte[]){
							byte[] byteFile = (byte[]) response;
							File file = this.storeLocally(byteFile, song);
							
							// create song file
							SongFile songFile = new SongFile(file, song);
							
							return songFile;
						}
					} else{
						return this.musicStorageWatcher.getSongMapping().get(song.getKey());
					}
				}
			}
		}
		
		if(song != null){
			logger.error("songfetcher: couldn't fetch song " + song.getKey());
		} else{
			logger.error("songfetcher: couldn't fetch song: null");
		}
		
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param byteFile
	 * @param song
	 * @return
	 * @throws IOException 
	 */
	private File storeLocally(byte[] byteFile, Song song) throws IOException{
		if(localStorage.isDirectory()){
			// fetch file name from song
			File file = this.resolveTempFileFromSong(song);
			
			// create file
			file.createNewFile();
			
			// write byte array to file
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(byteFile);
			fos.close();
			
			return file;
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * 
	 * @param song
	 * @return
	 * @throws IOException
	 */
	private File resolveTempFileFromSong(Song song) throws IOException{
		String key = song.getKey().toString();
		
		if(key != null && key.length() > 0){
			return new File(localStorage.getCanonicalPath() + System.getProperty("file.separator") + song.getKey());
			
		}
		
		return null;
	}
}
