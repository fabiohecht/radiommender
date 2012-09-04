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
package org.radiommender.controller.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.radiommender.controller.P2POverlay;
import org.radiommender.controller.Player;
import org.radiommender.controller.RecommenderSystem;
import org.radiommender.model.Message;
import org.radiommender.model.PlayListEntry;
import org.radiommender.model.Rating;
import org.radiommender.model.RecommenderMap;
import org.radiommender.model.SearchTermRankingEntry;
import org.radiommender.model.Song;
import org.radiommender.model.SongRating;
import org.radiommender.model.SongTag;
import org.radiommender.model.SongTagRating;
import org.radiommender.utils.CountingBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * T
 * @author robert erdin
 * @mail robert.erdin@gmail.com
 *
 */
public class RecommenderPlayList extends Thread {
	
	Logger logger = LoggerFactory.getLogger(RecommenderPlayList.class);

	boolean activated;
	private final RecommenderSystem recommenderSystem;
	private final LinkedBlockingQueue<PlayListEntry> recommendedSongs;
	private final P2POverlay overlay;
	private final Player player;
	private final ArrayList<SongRating> tmpOrdering;
	private final HashSet<String> visitedNodes;
	private boolean crawled = false;
	
	private HashSet<SongTag> visitedTags;
	private HashSet<Song> visitedSongs;
	private ArrayList<SongRating> playedSongs;
	private ArrayList<SongRating> songs;
	private ArrayList<SongTagRating> tags;
	
	public RecommenderPlayList(RecommenderSystem recommenderSystem) {
		this.activated = true;
		this.recommenderSystem = recommenderSystem;
		this.recommendedSongs = this.recommenderSystem.getRecommendedSongsQueue();
		this.overlay = this.recommenderSystem.getOverlay();
		this.player = this.recommenderSystem.getPlayer();
		this.tmpOrdering = new ArrayList<SongRating>();
		this.visitedNodes = new HashSet<String>();
		this.visitedTags = new HashSet<SongTag>();
		this.visitedSongs = new HashSet<Song>();
		this.playedSongs = new ArrayList<SongRating>();
		this.songs = new ArrayList<SongRating>();
		this.tags = new ArrayList<SongTagRating>();
	}
	
	
	/**
	 * Stop Thread
	 */
	public void stopThread(){
		this.activated = false;
		this.recommendedSongs.clear();
		
		recommenderSystem.getUi().updateSearchTermRanking(new ArrayList<SearchTermRankingEntry>());
	}
	
	
	/**
	 * 
	 * @param key
	 */
	private void recommendSongsBySearchTerm(String key){
		
		
		// loop through tags and find all songs
		// loop while tags are remaining to crawl and less than 15 songs are found
		float multiplicator = 1;
		String oldkey = "";
		boolean first = true;
		do{
			// select key
			SongTagRating bestSongTagRating = new SongTagRating();
			if(!tags.isEmpty() && this.activated){
				// sort tags
				Collections.sort(tags);
				
				// get tag with best rating and remove it from list
				bestSongTagRating = tags.get(tags.size()-1);
				tags.remove(tags.size()-1);
				
				// add to visited tags
				this.visitedTags.add(bestSongTagRating.getSongTag());
				
				// set new search key
				oldkey = key + "("+multiplicator+")";
				key = bestSongTagRating.getSongTag().getSongTag();
				
				
				// set multiplicator
				multiplicator = bestSongTagRating.getRating().getLocalRating();
				
				
			}
			
			if(first){
				// add first key to visited tags to avoid loops
				this.visitedTags.add(new SongTag(key));
				
				bestSongTagRating.setSearchTermHistory(key);
				
				first = false;
			}
			
			// get remote recommender map
			RecommenderMap<Object, Rating> recommendations = this.fetchRemoteRecommenderMap(key, this.buildBloomFilter());
			if(recommendations != null && this.activated){
				for(Map.Entry<Object, Rating> entry : recommendations.entrySet()){
					// its a song
					if(entry.getKey() instanceof Song){
						// check if song is already in visited songs list
						if(!this.visitedSongs.contains((Song)entry.getKey())){
							SongRating songRating = new SongRating((Song)entry.getKey(), entry.getValue());
							
							// calculate transitive rating
							songRating.getRating().setLocalRating(songRating.getRating().getRating() * multiplicator);
							songRating.setSearchTerm(bestSongTagRating.getSearchTermHistory());
							
							// add song rating
							songs.add(songRating);
							
							this.visitedSongs.add((Song)entry.getKey());
						}
					}
					
					// its a tag
					if(entry.getKey() instanceof SongTag){
						// check if song tag is already in visited list - should already be removed by filter
						if(!this.visitedTags.contains((SongTag) entry.getKey())){
							SongTagRating songTagRating = new SongTagRating((SongTag)entry.getKey(), entry.getValue());
							
							// calculate transitive rating
							songTagRating.getRating().setLocalRating(songTagRating.getRating().getRating() * multiplicator);
							songTagRating.setSearchTermHistory(bestSongTagRating.getSearchTermHistory()+"/"+songTagRating.getSongTag().getSongTag()+"("+songTagRating.getRating().getLocalRating()+")");
							
							// add song tag rating
							tags.add(songTagRating);
						}
					}
				}
			}
			
			// eventually add some songs to the queue
			if(!songs.isEmpty() && songs.size() >= 5 && this.activated){
				// sort songs
				Collections.sort(songs);
				
				// get best song so far
				SongRating bestSongRating = songs.get(songs.size()-1);
				
				// remove from songs
				songs.remove(songs.size()-1);
				
				boolean attempt = false;
				do{
					try {
						attempt = this.recommendedSongs.offer(new PlayListEntry(bestSongRating.getSong(), "ST", bestSongRating.getRating().getLocalRating()), 10, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				this.setCrawled(true);
				}while(!attempt && this.activated);
				this.updateUI(bestSongRating);
			}
			
			
		} while (!tags.isEmpty() && songs.size() <= 15 && this.activated);	
		
		// add at least one song to the queue
		if(songs.size() <= 5 && this.activated){
			this.setCrawled(true);
			for(SongRating sr : songs){
				boolean attempt = false;
				do{
					try {
						attempt = this.recommendedSongs.offer(new PlayListEntry(sr.getSong(), "ST", sr.getRating().getLocalRating()), 10, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}while(!attempt && this.activated);
				this.updateUI(sr);
			}
			this.songs.clear();
		}
	}
	
	
	private CountingBloomFilter<Object> buildBloomFilter(){
		CountingBloomFilter<Object> filter = new CountingBloomFilter<Object>(50, new int[500]);
		
		// add all songs
		for(Song song : this.visitedSongs){
			filter.add(song);
		}
		
		
		// add all songtags
		for(SongTag songTag : this.visitedTags){
			filter.add(songTag);
		}
		
		return filter;
	}
	
	
	private RecommenderMap<Object, Rating> fetchRemoteRecommenderMap(String key, CountingBloomFilter<Object> filter){
		// create message and request object from peer.
		// return will be a pruned map.
		Message mapRequest = new Message();
		mapRequest.setCommand(Message.CMD_SEARCHTERM_REQUEST);
		mapRequest.setArguments(new Object[]{key.toLowerCase(), filter});
		Object tmp;
		try {
			tmp = this.overlay.lookupAndSendMessage(key, mapRequest);
			if(tmp != null){
				return (RecommenderMap<Object, Rating>) tmp;
			}
		} catch (IOException e) {
			logger.debug("error sending recommender map request for key "+ key);
		}
		
		return null;
	}

	
	private void updateUI(SongRating songRating){
		this.playedSongs.add(songRating);
		ArrayList<SearchTermRankingEntry> ranking = new ArrayList<SearchTermRankingEntry>();
		for(SongRating sr : this.playedSongs){
			ranking.add(new SearchTermRankingEntry(sr.getSearchTerm(), sr.getSong(), sr.getRating().getLocalRating()));
		}
		recommenderSystem.getUi().updateSearchTermRanking(ranking);
	}


    @Override
	public void run() {		
		this.recommendedSongs.clear();
		this.visitedSongs.clear();
		this.visitedTags.clear();
		this.playedSongs.clear();
		
		this.songs.clear();
		this.tags.clear();
				
		recommenderSystem.getUi().updateSearchTermRanking(new ArrayList<SearchTermRankingEntry>());
		
		
		logger.debug("recommendedSongs cleared, size: " + this.recommendedSongs.size());
		this.activated = true;
		
		String searchTerm = this.player.currentSearchTerm();
		while(this.activated){
			this.visitedTags.clear();
			this.recommendSongsBySearchTerm(searchTerm);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (this.visitedSongs.size() > 12) {
				this.visitedSongs.clear();
			}
		}		
	}


	/**
	 * @return the tmpOrdering
	 */
	public ArrayList<SongRating> getTmpOrdering() {
		return tmpOrdering;
	}


	/**
	 * @return the crawled
	 */
	public boolean isCrawled() {
		return crawled;
	}


	/**
	 * @param crawled the crawled to set
	 */
	public void setCrawled(boolean crawled) {
		this.crawled = crawled;
	}

}
