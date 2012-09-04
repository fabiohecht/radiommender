/**
 * 
 */
package p2pct.controller.overlay;

import java.util.HashSet;
import java.util.Set;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.P2POverlay;
import p2pct.controller.recommender.SearchTermProvider;
import p2pct.controller.songhandler.MusicStorageWatcher;
import p2pct.controller.songhandler.SongTagger;
import p2pct.model.Message;
import p2pct.model.Rating;
import p2pct.model.RecommenderMap;
import p2pct.model.Song;
import p2pct.model.VotingMessage;
import p2pct.utils.CountingBloomFilter;

/**
 * MessageListener handles message requests from remote peers (e.g. file transfer).
 * 
 * @author nicolas baer
 */
public class MessageListener implements ObjectDataReply{

	// logger
	Logger logger = LoggerFactory.getLogger(P2POverlay.class);
	
	private P2POverlay overlay; 
	
	private MusicStorageWatcher musicStorage;
	private SongTagger songTagger;
	private SearchTermProvider searchTermProvider;
	
	
	/**
	 * default constructor
	 * @param musicStorage handler of the local music storage
	 */
	public MessageListener(MusicStorageWatcher musicStorage, SongTagger songTagger, P2POverlay overlay){
		this.musicStorage = musicStorage;
		this.songTagger = songTagger;
		this.overlay = overlay;
		this.searchTermProvider = new SearchTermProvider();
	}
	
	
	/* (non-Javadoc)
	 * @see net.tomp2p.rpc.ObjectDataReply#reply(net.tomp2p.peers.PeerAddress, java.lang.Object)
	 */
	@Override
	public Object reply(PeerAddress sender, Object request) throws Exception {

		// check if request is a message
		if(request != null && request instanceof Message){
			Message msg = (Message) request;
			if(msg.getCommand().equals(Message.CMD_FILE_REQUEST)){
				this.logger.info("p2p.overlay.message: received file request for " + msg.getArguments()[0]);
				int hashCode = (Integer) msg.getArguments()[0];			
				
				return this.musicStorage.getLocalFileInBytes(hashCode);
			}
			
			if(msg.getCommand().equals(Message.CMD_SONGLIST_REQUEST)){
				return this.musicStorage.getMusicLibraryAsBloomFilter();
			}
			
			
			
			if(msg.getCommand().equals(Message.CMD_SEARCHTERM_REQUEST)){
				Data recommenderMap = this.overlay.getLocal((String)msg.getArguments()[0]);
				CountingBloomFilter<Object> filter = null;
				if(msg.getArguments()[1] != null && msg.getArguments()[1] instanceof CountingBloomFilter<?>){
					filter = (CountingBloomFilter<Object>) msg.getArguments()[1];
				}
				if(recommenderMap != null){
					RecommenderMap<Object, Rating> reply = this.searchTermProvider.pruneSearchTermMap((RecommenderMap<Object, Rating>) recommenderMap.getObject(), filter);
					return reply;
				}
				return null;
			}
			
			//Msg from the affinity recommender, requesting a random song not contained in a bloom filter.
			if(msg.getCommand().equals(Message.CMD_AFFINITY_SONGREQUEST)){
				
				Set<Song> checkedBlockedSongs;
				if(msg.getArguments()[0] != null && msg.getArguments()[0] instanceof Set<?>){
					checkedBlockedSongs = (Set<Song>) msg.getArguments()[0];	
				}
				else{
					checkedBlockedSongs = new HashSet<Song>();
				}

				
				return this.musicStorage.handleAffinityRequest(checkedBlockedSongs);
			}
			
			
			/*
			if(msg.getCommand().equals(Message.CMD_SONGTAGGER_VOTE)){
				Data result = this.overlay.getLocal((String)msg.getArguments()[0]);
				RecommenderMap<Object, Rating> recommenderMap;
				if(result == null){
					recommenderMap = new RecommenderMap<Object, Rating>();
				}
				else{
					recommenderMap = (RecommenderMap<Object, Rating>)result.getObject();
				}
				
				
				if(recommenderMap.get(msg.getArguments()[1]) == null){
					recommenderMap.put(msg.getArguments()[1], new Rating());
				}
				if((Integer)msg.getArguments()[2] == 1){
					recommenderMap.get(msg.getArguments()[1]).voteUp();
				}
				else if((Integer)msg.getArguments()[2] == 2){
					recommenderMap.get(msg.getArguments()[1]).voteDown();
				}
				
				
				this.overlay.putLocal((String)msg.getArguments()[0], new Data(recommenderMap));
				
			}
			*/
			
			
		}
		
		
		// check if request is a voting message
		if(request != null && request instanceof VotingMessage){
			
			VotingMessage votingMessage = (VotingMessage) request;
			logger.info("p2p.overlay.message: voting message received: "+votingMessage.toString());

			this.songTagger.getMessageQueue().put(votingMessage);
			
			return request;
		}
		
		logger.error("p2p.overlay.message: unknown message request");
		
		return null;
	}
	
	
	

}
