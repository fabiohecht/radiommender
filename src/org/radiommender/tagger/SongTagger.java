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
package org.radiommender.tagger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.radiommender.model.Rating;
import org.radiommender.model.RecommenderMap;
import org.radiommender.model.SongTag;
import org.radiommender.model.VotingMessage;
import org.radiommender.overlay.Overlay;
import org.radiommender.songhandler.SongHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.p2p.builder.DHTBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * This class handles all the graph building to connect tags with other tags and tags with songs.
 * 
 * 
 * TODO Thread and Queue
 * 
 * @author nicolas baer
 */
public class SongTagger implements Runnable{
	public static final short UPVOTE = 1;
    public static final short DOWNVOTE = -1;
    
    public static final Set<String> COMMON_WORDS = new HashSet<String>(Arrays.asList(new String[]{"the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us"}));
    
    // logger
 	Logger logger = LoggerFactory.getLogger(SongHandler.class);
    
	// module vars
	private Overlay overlay;
	
	// thread control
	private boolean activated;
	
	// message queue
	LinkedBlockingQueue<VotingMessage> messageQueue;

	/**
	 * default constructor
	 * @param overlay P2P overlay
	 */
	public SongTagger(Overlay overlay){
		this.overlay = overlay;
		this.activated = true;
		this.messageQueue = new LinkedBlockingQueue<VotingMessage>(Integer.MAX_VALUE);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(activated){
			
			try {
				VotingMessage message = this.messageQueue.poll(1000000, TimeUnit.SECONDS);
				if(message != null){
					for(SongTag tag : message.getTags()){
						try {
							this.alterTagRelation(message.getKey(), tag, message.getVote());
							this.alterTagRelation(message.getKey(), message.getSong(), message.getVote());
							
							logger.info("songtagger: saved song tag ("+tag+") relation to key: " + message.getKey());
							
						} catch (Exception e) {
							logger.error("songtagger: error saving song tag ("+tag+") relation to key: " + message.getKey() + " error=" + e.getMessage());
						}
					}
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
		
	}
	
	/**
	 * Alters a tag based on the search term. You can either increase or decrease (use static vars).
	 * @param dhtKey keyword entered by user
	 * @param mapObject SongTag or Song
	 * @param mode increase or decrease
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void alterTagRelation(String dhtKey, Object mapObject, int mode) throws ClassNotFoundException, IOException{
		// search term lower case
		dhtKey = dhtKey.toLowerCase();
		Number160 hash = Number160.createHash(dhtKey);
			
		// fetch search term from DHT
		Data search = this.overlay.getPeer().getPeerBean().getStorage().get(hash, DHTBuilder.DEFAULT_DOMAIN, Number160.ZERO);
		 
		RecommenderMap<Object, Rating> recommenderMap;
		
		// check for map
		if(search == null || search.getObject() == null){
			logger.info("No map found associated to the key: " + dhtKey + " creating new map");
			recommenderMap = new RecommenderMap<Object, Rating>();
		} else{
			recommenderMap = (RecommenderMap<Object, Rating>) search.getObject();
			logger.info("Retrieved map from DHT for key: " + dhtKey);
		}
		
		// check for keyword
		if(recommenderMap.get(mapObject) == null){
			// TODO This does not work!
			logger.info("Map for " + dhtKey + " does not yet contain: " + mapObject);
			recommenderMap.put(mapObject, new Rating());
		}
		else{
			logger.info("Map already contains: " + mapObject);
		}
		
		
		// vote up or down
		if(mode == SongTagger.UPVOTE){
			recommenderMap.get(mapObject).voteUp();
		}
		
		if(mode == SongTagger.DOWNVOTE){
			recommenderMap.get(mapObject).voteDown();
		}
		
		this.overlay.getPeer().getPeerBean().getStorage().put(hash, DHTBuilder.DEFAULT_DOMAIN, Number160.ZERO, new Data(recommenderMap));
		
	}

	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	/**
	 * @return the messageQueue
	 */
	public LinkedBlockingQueue<VotingMessage> getMessageQueue() {
		return messageQueue;
	}

	/**
	 * returns the set of words in a given string
	 * 
	 * @param string
	 * @return
	 */
	public Set<String> getWords(String string) {
		Set<String> words = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for (int p=0; p<string.length(); p++) {
			if (this.isSpace(string.charAt(p))) {
				if (sb.length()>0) {
					String word = sb.toString();
					if (!COMMON_WORDS.contains(word)) {
						words.add(word);
					}
					sb = new StringBuilder();
				}
			}
			else {
				sb.append(string.charAt(p));
			}
		}
		return words;
	}
	
	/**
	 * Returns whether given char is a space (non-word for index purposes) character.
	 * Assumes always lowercase characters.
	 * 
	 * @param character
	 * @return
	 */
	private boolean isSpace(char character) {
		return !(character >= 'a' && character <= 'z' || character >= '0' && character <= '9');
	}
}
