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
package org.radiommender.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.radiommender.controller.music.SongPlayer;
import org.radiommender.controller.recommender.RecommenderFeeder;
import org.radiommender.gui.Ui;
import org.radiommender.model.Song;
import org.radiommender.utils.ExecutorPool;


/**
 * The Player has two important functionalities.
 * Firstly it controls and updates the user interface.
 * Secondly it controls the music playback.
 * 
 * @author nicolas baer
 */
public class Player extends Observable{
	// modules
	private SongPlayer songPlayer;
	private SongHandler songHandler;
	private RecommenderSystem recommenderSystem;
	
	// internal vars
	private String searchTerm;
	
	/**
	 * default constructor
	 */
	public Player(){
		this.songPlayer = new SongPlayer(this);
		this.searchTerm = null;
	}
	
	/**
	 * Starts the music player
	 */
	public void start() {
		ExecutorPool.getGeneralExecutorService().execute(this.songPlayer);
	}
	
	/**
	 * Registers the songhandler to fetch playlist
	 * @param songHandler
	 */
	public void registerSongHandler(SongHandler songHandler){
		this.songHandler = songHandler;
		this.songPlayer.setSongHandler(songHandler);
	}
	
	/**
	 * Register the recommender system. This needs to be done to communicate with the recommender e.g.
	 * give feedback on played or skipped song.
	 * @param recommenderSystem
	 */
	public void registerRecommenderSystem(RecommenderSystem recommenderSystem){
		this.recommenderSystem = recommenderSystem;
	}
	
	/**
	 * Returns the current search term of the user
	 * @return
	 */
	public String currentSearchTerm(){
		return this.searchTerm;
	}
	
	/**
	 * Notifies the recommender about the skipped song.
	 * @param song
	 */
	private void songSkipped(Song song){
		TreeMap<Song, Integer> report = new TreeMap<Song, Integer>();
		report.put(song, RecommenderFeeder.SKIPPED);
		this.notifyMyObservers(report);
	}
	
	/**
	 * Notifies the recommender about the played song
	 * @param song
	 */
	public void songPlayed(Song song){
		TreeMap<Song, Integer> report = new TreeMap<Song, Integer>();
		report.put(song, RecommenderFeeder.PLAYED);
		this.notifyMyObservers(report);
	}
	
	/**
	 * 
	 * @param searchTerm
	 */
	public void play(String searchTerm){
		this.songHandler.updateCurrentSong(null, "");
		this.searchTerm = searchTerm.toLowerCase();
		
		// stops the current song
		this.songPlayer.stop();
		
		// stop downloading songs for the moment
		this.songHandler.stopSongListWorker();
		
		// clear recommender system
		this.recommenderSystem.setSearchTermChanged();
		
		// clear playlist
		this.songHandler.clearRdySongs();
		
		// start downloading songs again
		this.songHandler.startSongListWorker();

		// start the player again
		this.songPlayer.play();
	}
	
	/**
	 * 
	 */
	public void skip(){
		// skip the current song
		Song song = this.songPlayer.skipSong();
		
		// notify recommender system
		if (song!=null) {
			this.songSkipped(song);
		}
		if (this.songHandler != null) {
			this.songHandler.updateCurrentSong(null, "");
		}
		
	}
	
	/**
	 * 
	 * @param song
	 * @param origin 
	 */
	public void songPlaying(Song song, String origin){
		this.notifyMyObservers(song);
		
		if (this.songHandler != null) {
			this.songHandler.updateCurrentSong(song, origin);
		}

	}
	
	
	/**
	 * Helper function to update with one call.
	 * @param arg0
	 */
	private void notifyMyObservers(Object arg0) {
		this.setChanged();
		this.notifyObservers(arg0);
	}

	public void stop() {
		this.songPlayer.stop();
	}

}
