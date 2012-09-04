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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.radiommender.controller.P2POverlay;
import org.radiommender.controller.Player;
import org.radiommender.controller.RecommenderSystem;
import org.radiommender.controller.songhandler.SongTagger;
import org.radiommender.gui.Ui;
import org.radiommender.model.Song;
import org.radiommender.model.SongTag;
import org.radiommender.model.SongVoteEntry;
import org.radiommender.model.VotingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Observes the p2pct.controller.Player and feeds the information into the
 * recommender system.
 * 
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 */
public class RecommenderFeeder implements Observer {

	Logger logger = LoggerFactory.getLogger(RecommenderFeeder.class);

	// "Enums"
	public static final int SKIPPED = 0;
	public static final int PLAYED = 1;

	private final RecommenderSystem recommenderSystem;
	private final Player player;
	private final P2POverlay overlay;
	private final Ui ui;
	private final List<SongVoteEntry> songVotes;
	

	public RecommenderFeeder(RecommenderSystem recommenderSystem, Player player, P2POverlay overlay, Ui ui) {
		this.recommenderSystem = recommenderSystem;
		this.player = player;
		this.overlay = overlay;
		this.ui = ui;
		this.songVotes = new ArrayList<SongVoteEntry>();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof TreeMap){
			logger.info("Recieved update from Player, will submit it to DHT");
			
			String searchTerm = this.player.currentSearchTerm().toLowerCase();
			@SuppressWarnings("unchecked")
			TreeMap<Song, Integer> feedback = (TreeMap<Song, Integer>) arg1;

			// get song object
			Song song = feedback.firstKey();
			

			if (feedback.firstEntry().getValue() == RecommenderFeeder.PLAYED) { // song was played --> VOTE UP				
				// vote in the DHT
				remoteVoteUp(searchTerm, song);
				//update the GUI
				this.songVotes.add(new SongVoteEntry(song, searchTerm, true));
				logger.info("Sent upvote message for " + searchTerm + " and " + song);
				
			} else if (feedback.firstEntry().getValue() == RecommenderFeeder.SKIPPED) { // song was skipped
				// vote in the DHT
				remoteVoteDown(searchTerm, song);
				// update the GUI
				this.songVotes.add(new SongVoteEntry(song, searchTerm, false));
				logger.info("Sent downvote message for " + searchTerm + " and " + song);
			
			} else {
				logger.error("RecommenderFeeder recieved incorrect update by the Player");
			}
			
			//update the gui
			ui.updateVotes(this.songVotes);
			
			
		}
		
	}

	/**
	 * Vote up a Song for a given SearchTerm in the DHT.
	 * @param searchTerm
	 * @param song
	 */
	public void remoteVoteDown(String searchTerm, Song song) {
		try{
		// send message to update search term recommender map by peer holding it
		VotingMessage votingMessageSearchTerm = new VotingMessage(searchTerm, new SongTag[]{new SongTag(song.getArtist())}, song, SongTagger.DOWNVOTE);
		this.overlay.lookupAndSendMessage(searchTerm, votingMessageSearchTerm);
		
		// send message to update artist recommender map by peer holding it
		VotingMessage votingMessageArtist = new VotingMessage(song.getArtist(), new SongTag[]{new SongTag(searchTerm)}, song, SongTagger.DOWNVOTE);
		this.overlay.lookupAndSendMessage(searchTerm, votingMessageArtist);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Vote down a Song for a given SearchTerm in the DHT.
	 * @param searchTerm
	 * @param song
	 */
	public void remoteVoteUp(String searchTerm, Song song) {
		try{
			// send message to update search term recommender map by peer holding it
			VotingMessage votingMessageSearchTerm = new VotingMessage(searchTerm, new SongTag[]{new SongTag(song.getArtist())}, song, SongTagger.UPVOTE);
			this.overlay.lookupAndSendMessage(searchTerm, votingMessageSearchTerm);
			
			// send message to update artist recommender map by peer holding it
			VotingMessage votingMessageArtist = new VotingMessage(song.getArtist(), new SongTag[]{new SongTag(searchTerm)}, song, SongTagger.UPVOTE);
			this.overlay.lookupAndSendMessage(searchTerm, votingMessageArtist);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Clear the votes history
	 */
	public void clearVotesHistory() {
		this.songVotes.clear();
	}
	



	
		/*

		// normalize search term
		searchTerm = searchTerm.toLowerCase().trim();
		
		logger.debug("attempting to store maps...");
		// store the search term related ratings into the DHT
		this.overlay.put(searchTerm, new Data((Object)maps.get(0)));
		
		this.overlay.put(song.getArtist(),  new Data((Object)maps.get(1)));
		logger.debug("song stored..");
		
		*/
}
