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
package org.radiommender.songhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Random;
import java.util.Set;

import org.radiommender.core.Application;
import org.radiommender.model.Song;
import org.radiommender.model.SongFile;
import org.radiommender.tagger.SongTagReader;
import org.radiommender.utils.ConfigurationFactory;
import org.radiommender.utils.CountingBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The music storage watcher handles the local music library.
 * It will first search through all the folders and build an index.
 * 
 * @author nicolas baer
 */
public class MusicStorageWatcher extends Observable {
	// logger
	Logger logger = LoggerFactory.getLogger(MusicStorageWatcher.class);
	
	// storage vars
	private final File localStorage;

	// song mapping
	private HashMap<Integer, SongFile> songMapping;

	/**
	 * Constructor with a custom local storage parameter.
	 * 
	 * @param localStorage folder the local music lies in
	 */
	public MusicStorageWatcher(File localStorage) {
		this.localStorage = localStorage;
		this.songMapping = new HashMap<Integer, SongFile>();
	}

	/**
	 * Initializes the song mapping based on the local storage folder.
	 * 
	 * @return itself (for fast instantiation: new MusicStorageWatcher().initSongMapping())
	 * @throws FileNotFoundException local storage not found
	 */
	public MusicStorageWatcher initSongMapping() throws FileNotFoundException{
		this.exploreLocalStorage();
		
		// notify observers
		setChanged();
		notifyObservers(songMapping);
		
		return this;
	}
	
	/**
	 * Builds the song mapping based on the files in the local storage folder.
	 * It will find all files in the local storage and try to extract the song information.
	 * If the song information is accessible, the song is mapped. Otherwise the song is lost.
	 * 
	 * @throws FileNotFoundException local storage not found
	 */
	private void exploreLocalStorage() throws FileNotFoundException{
		// fetch all files in directory including subdirectories
		List<File> files = this.folderTraverse(this.localStorage);
		
		// fill song mapping
		for(File file : files){
			try {
				Song song = new SongTagReader(file).getSongTags();
				SongFile songFile = new SongFile(file, song);
				logger.debug("will put song file "+file.getName());
				this.songMapping.put(song.getKey(), songFile);
			} catch (Exception e) {
				// in any case of failure, just skip the file... hopefully there are enough to parse :)
				logger.warn(file.getName() + ": " + e.getMessage());
			} 
		}
	}
	
	/**
	 * Gets the file belonging to the hashCode of the song and returns it as byte array.
	 * 
	 * @param hashCode to identify song with
	 * @return file as byte array
	 * @throws IOException any kind of problem with the file :)
	 */
	public byte[] getLocalFileInBytes(Integer hashCode) throws IOException{
		// fetch song mapping
		SongFile songFile = this.songMapping.get(hashCode);
		
		// check if file exists
		if(songFile != null && songFile.getFile().exists()){

			// get byte array from file
			FileInputStream fileInputStream = new FileInputStream(songFile.getFile());
			byte[] data = new byte[(int) songFile.getFile().length()];
			fileInputStream.read(data);
			fileInputStream.close();
			
			return data;
		}
		
		return null;
	}
	
	
	/**
	 * Fetches the local song list.
	 * @return local song list
	 */
	public Set<Song> getMusicLibrary(){
		if(this.songMapping != null && this.songMapping.size() > 0){
			// fetch all songs from mapping
			HashSet<Song> songList = new HashSet<Song>();
			for(SongFile songFile : this.songMapping.values()){
				songList.add(songFile.getSong());
			}
			
			return songList;
		}
		return null;
	}
	
	/**
	 * Fetches the local music library. With complete Information on Genre, Artist, Album, Title, each song
	 * is added 4 times into the CountingBloomFilter
	 * Genre
	 * Genre, Artist
	 * Genre, Artist, Album
	 * Genre, Artist, Album, Title
	 * @return
	 */
	public CountingBloomFilter<Song> getMusicLibraryAsBloomFilter(){
		if(this.songMapping != null && this.songMapping.size() > 0){
			// fetch all songs from mapping
			CountingBloomFilter<Song> songList = new CountingBloomFilter<Song>(500, new int[1000]);
			for(SongFile songFile : this.songMapping.values()){
				// adding the song as received from the MusicStoreWatcher
				songList.add(songFile.getSong());
				// creating Song objects for the Borda Affinity computation.
				songList.add(new Song(songFile.getSong().getGenre(), songFile.getSong().getArtist(), songFile.getSong().getAlbum(), null)); 
				songList.add(new Song(songFile.getSong().getGenre(), songFile.getSong().getArtist(), null, null));
				songList.add(new Song(songFile.getSong().getGenre(), null, null, null));
				
				// count up the size of the music collection
				songList.increaseUniqueIdentityCount();
			}
			
			return songList;
		}
		return null;
	}
	
	
	/**
	 * Searches the provided folder and its subfolders for files.
	 * @return all files found in folder and subfolders
	 * @throws FileNotFoundException couldn't find the provided folder 
	 */
	private List<File> folderTraverse(File folder) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = folder.listFiles();
		if(filesAndDirs != null){
			List<File> filesDirs = Arrays.asList(filesAndDirs);
			for (File f : filesDirs) {			
				// check for directory
				if (!f.isFile()) {
					// recursive call
					List<File> deeperList = this.folderTraverse(f);
					result.addAll(deeperList);
				} else{
					result.add(f);
				}
			}
		}
		
		return result;
	}

	/**
	 * @return the songMapping
	 */
	public HashMap<Integer, SongFile> getSongMapping() {
		return songMapping;
	}
	
	public Song	handleAffinityRequest(Set<Song> checkedBlockedSongs){
		Song result = null;

		/*
		for(Entry<Integer, SongFile> current : this.songMapping.entrySet()){
			if(!checkedBlockedSongs.contains(current.getValue().getSong())){
				result = current.getValue().getSong();
				return result;
			}
		}
		*/
		
		int stopper = 0;
		do{
			result = this.getRandomSong();
			stopper++;
		}
		while(checkedBlockedSongs.contains(result) && stopper < 50);
		
		if(stopper == 50){
			result = null;
		}
		return result;
	}
	
	/**
	 * Retrieve a random song from the local songs.
	 * TODO: this is slow, think about another data structure for the local songs.
	 * @return random local Song
	 */
	private Song getRandomSong(){
		int size = this.songMapping.size();
		int item = Application.random.nextInt(size);
		int i = 0;
		for(Entry<Integer, SongFile> entry : this.songMapping.entrySet())
		{
		    if (i == item)
		        return entry.getValue().getSong();
		    i = i + 1;
		}
		
		return null;
	}

}
