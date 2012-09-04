package p2pct.controller.recommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.P2POverlay;
import p2pct.controller.Player;
import p2pct.controller.RecommenderSystem;
import p2pct.controller.songhandler.SongTagger;
import p2pct.gui.Ui;
import p2pct.model.Song;
import p2pct.model.SongTag;
import p2pct.model.SongVoteEntry;
import p2pct.model.VotingMessage;

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
