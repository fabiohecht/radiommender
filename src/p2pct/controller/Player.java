/**
 * 
 */
package p2pct.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import p2pct.controller.music.SongPlayer;
import p2pct.controller.recommender.RecommenderFeeder;
import p2pct.gui.Ui;
import p2pct.model.Song;
import p2pct.utils.ExecutorPool;

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
