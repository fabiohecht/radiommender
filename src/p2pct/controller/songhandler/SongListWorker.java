package p2pct.controller.songhandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.RecommenderSystem;
import p2pct.controller.SongHandler;
import p2pct.model.PlayListEntry;
import p2pct.model.Song;
import p2pct.model.SongFile;

/**
 * Worker thread to download the songs proposed by the recommender system and offer it to the player.
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 * 
 */
public class SongListWorker implements Runnable {
	Logger logger = LoggerFactory.getLogger(SongListWorker.class);
	
	
	private SongHandler songHandler;
	private RecommenderSystem recommenderSystem;
	private SongFetcher songFetcher;
	private boolean running = true;
	private boolean active;

	public SongListWorker(SongHandler songHandler, RecommenderSystem recommenderSystem, SongFetcher songFetcher){
		this.songHandler = songHandler;
		this.recommenderSystem = recommenderSystem;
		this.songFetcher = songFetcher;
		this.active = true;
	}
	
	/**
	 * Stop SongListWorkerThread. Will finish downloading the current song first. Thread will die.
	 */
	public void stop(){
		this.active = false;
		this.running = false;
	}
	
	/**
	 * Pause SongListWorkerThread. Will finish downloading the current song first.
	 */
	public void pause(){
		this.active = false;
	}

	@Override
	public void run() {
		while(this.running){
			
			if (this.active) {
				// tell the SongFetcher to fetch a song;
				try {
					// tell the SongFetcher to fetch a song;
					logger.debug("SONG LIST WORKER will get");
					PlayListEntry nextSong = this.recommenderSystem.getNextSong();
					
					if (nextSong != null && this.active) {
						logger.debug("got="+nextSong);
						SongFile songFile = this.songFetcher.retrieveSong(nextSong.getSong()); 
						if(songFile != null && songFile.getFile() != null){
							nextSong.setSongFile(songFile);
						}
						
					}
					else {
						if(this.active){
							logger.debug("got NULL");
						}
						else{
							logger.debug("thread stopped");
						}
					}
					
					// check if song is really fetched
					if(nextSong.getSongFile() != null && this.active){
						//add songfile to queue, wait if necessary... BLOCKING!
						this.songHandler.addSongToPlayList(nextSong);
					}
					logger.debug("activated");
					
					Thread.sleep(1000);
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	/**
	 * @return the activated
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the activated to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
