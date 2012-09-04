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

import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMap;

import org.radiommender.controller.recommender.AffinityComputer;
import org.radiommender.controller.recommender.AffinityPlayList;
import org.radiommender.controller.recommender.RecommenderFeeder;
import org.radiommender.controller.recommender.RecommenderPlayList;
import org.radiommender.gui.Ui;
import org.radiommender.model.AffinityEntry;
import org.radiommender.model.PlayListEntry;
import org.radiommender.model.Song;
import org.radiommender.utils.ConfigurationFactory;
import org.radiommender.utils.SafeTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recommender System. This module generates a queue of recommended songs based
 * on the files provided by the users and the preferences expressed during usage 
 * of the system.
 * 
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 * 
 */
public class RecommenderSystem {
	private static final int PLAYLIST_SONG_LIMIT = 3;


	// logger
	Logger logger = LoggerFactory.getLogger(RecommenderSystem.class);
	

	private AffinityComputer affinityComputer;
	private AffinityPlayList playList;
	private Set<Song> localSongs;
	private final LinkedBlockingQueue<PlayListEntry> affinitySongs;
	private final LinkedBlockingQueue<PlayListEntry> recommendedSongs;
	private volatile SafeTreeMap<Float, PeerAddress> affinityList;
	private final P2POverlay overlay;
	private final SongHandler songHandler;
	private Player player;
	private RecommenderPlayList recommenderPlayListThread;
	private Thread rplThread;
	private RecommenderFeeder  recommenderFeeder;
	private int recommenderSwitcher;
	private final int maxSizeAffList;

	private Ui ui;

	/**
	 * Initialize Recommender System. HAS TO BE STARTED SEPERATELY with startRecommending()
	 * @param localSongs
	 */
	public RecommenderSystem(P2POverlay overlay, SongHandler songHandler) {
		logger.debug("Start initializing recommender system");
		
		// Queue initialized with maximum size defined in configuration.
		this.affinitySongs = new LinkedBlockingQueue<PlayListEntry>(new Integer(ConfigurationFactory.getProperty("recommender.recommendedsongs.length")));
		this.recommendedSongs = new LinkedBlockingQueue<PlayListEntry>(new Integer(ConfigurationFactory.getProperty("recommender.recommendedsongs.length")));
		this.affinityList = new SafeTreeMap<Float, PeerAddress>();
		this.overlay = overlay;
		this.songHandler = songHandler;
		this.recommenderSwitcher = 0;
		this.maxSizeAffList = new Integer(ConfigurationFactory.getProperty("recommender.affinitylist.length"));
		
		
		logger.debug("End initializing recommender system");
	}

	public SongHandler getSongHandler() {
		return songHandler;
	}

	public P2POverlay getOverlay() {
		return overlay;
	}

	public PeerMap getNeighbours() {
		return this.overlay.getNeighbors();
	}

	/**
	 * Start the process of finding suitable songs.
	 * CALL ONLY IF THE SYSTEM IS FULLY INITIALIZED. Has dependencies within the overlay module.
	 * @return
	 */
	public RecommenderSystem startRecommending(Player player){
		logger.debug("starting recommender system...");
		
		//init player
		this.player = player;
		
		this.recommenderPlayListThread = new RecommenderPlayList(this);
		
		
		//get local song list
		this.localSongs = this.songHandler.fetchLocalSongList();
		
		
		//register with SongHandler
		this.songHandler.registerRecommenderSystem(this);
		
		// starting affinity computer
		this.affinityComputer = new AffinityComputer(this.localSongs, this, this.overlay, this.ui);
		new Thread(this.affinityComputer).start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.recommenderFeeder = new RecommenderFeeder(this, this.player, this.overlay, this.ui);
		
		// starting AffinityPlayList
		this.playList = new AffinityPlayList(this);
		new Thread(this.playList) .start();
		
		logger.debug("recommender system started.");
		return this;
	}
	
	
	public void setSearchTermChanged(){
		logger.info("been notified that the search term was changed! restarting RecommenderPlayList Thread");
		
		// Handle the RecommenderPlayList Thread
		this.recommenderPlayListThread.stopThread();
		
		try {
			int counter = 0;
			if(this.rplThread != null){
			while(this.rplThread.isAlive()){
				counter++;
				Thread.sleep(1000);
			}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.recommenderPlayListThread.setCrawled(false);
		this.rplThread = new Thread(this.recommenderPlayListThread);
		this.rplThread.start();
		
		// Clear the votes history
		this.recommenderFeeder.clearVotesHistory();
	}

	/**
	 * Get the next song in the queue of recommended songs.
	 * Blocking if queue is empty
	 * 
	 * @return First song object from the queue of recommended songs.
	 * @throws InterruptedException 
	 */
	public PlayListEntry getNextSong() throws InterruptedException {
		
		//prio1... from recommender
		while(!this.recommenderPlayListThread.isCrawled()){
			Thread.sleep(500);
			logger.debug("No Songs from SearchTerm System yet, sleeping...");
		}
		
		//TODO only for debug purposes
		if(this.recommendedSongs.size() == 0){
			System.out.println("GetNextSong: sleeping because ST list is empty!");
			Thread.sleep(3000);
		}
		
		while(true){
			if(this.recommendedSongs.size()>0 && this.recommenderSwitcher<PLAYLIST_SONG_LIMIT){
				logger.debug("size: " + this.recommendedSongs.size());
				PlayListEntry song = this.recommendedSongs.take();
				logger.debug("size: " + this.recommendedSongs.size());
				logger.debug("Song from recommender! " + song.toString());
				this.recommenderSwitcher++;
				return song;
			}
			else{
				//prio2... from affinity
				if(!this.affinitySongs.isEmpty()){
					PlayListEntry song = this.affinitySongs.take();
					logger.debug("Song from affinity! " + song.toString());
					this.recommenderSwitcher = 0;
					return song;
				} else{
					this.recommenderSwitcher = 0;
				}				
			}
		}

	}

	
	public SafeTreeMap<Float, PeerAddress> getAffinityList() {
		return affinityList;
	}
	
	/**
	 * INTERNAL FUNCTION ONLY, don't use outside recommender system.
	 * Use getNextSong() instead!!
	 * @return
	 */
	public LinkedBlockingQueue<PlayListEntry> getAffinitySongsQueue(){
		return this.affinitySongs;
	}
	
	/**
	 * INTERNAL FUNCTION ONLY, don't use outside recommender system.
	 * Use getNextSong() instead!!
	 * @return
	 */
	public LinkedBlockingQueue<PlayListEntry> getRecommendedSongsQueue(){
		return this.recommendedSongs;
	}
	
	
	/**
	 * Add an affinity to the Affinity List. Methods cuts down the TreeMap to the size of the configuration
	 * parameter recommender.affinitylist.length. Direct access to the TreeMap can avoid this but map will 
	 * be cut down with the next regular call of this function again.
	 * @param key
	 * @param value
	 */
	public void addAffinity(Float key, PeerAddress value){
		
		
		// cut down to the size mentioned in the configuration parameter
		while(this.affinityList.size() >= this.maxSizeAffList){
			logger.debug("RecommenderSystem: cutting down affinityList" + this.affinityList.pollFirstEntry());
		} 
		
		this.affinityList.safePut(key, value);
		
		List<AffinityEntry> affinityEntries = new ArrayList<AffinityEntry>(this.affinityList.size());
		for (Entry<Float, PeerAddress> affinityEntry : this.affinityList.entrySet()) {
			affinityEntries.add(new AffinityEntry(P2POverlay.shortenPeerId(affinityEntry.getValue().getID()), affinityEntry.getKey()));
		}
		this.ui.updateActualAffinity(affinityEntries);
		
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public RecommenderFeeder getRecommenderFeeder(){
		return this.recommenderFeeder;
	}

	public void registerUi(Ui ui) {
		this.ui = ui;
	}
	
	public Ui getUi(){
	    return ui;
	}

	
}
