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
package org.radiommender.overlay;

import java.util.HashSet;
import java.util.Set;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import org.radiommender.model.Message;
import org.radiommender.model.Rating;
import org.radiommender.model.RecommenderMap;
import org.radiommender.model.Song;
import org.radiommender.model.VotingMessage;
import org.radiommender.recommender.searchterm.SearchTermProvider;
import org.radiommender.songhandler.MusicStorageWatcher;
import org.radiommender.tagger.SongTagger;
import org.radiommender.utils.CountingBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MessageListener handles message requests from remote peers (e.g. file transfer).
 * 
 * @author nicolas baer
 */
public class MessageListener implements ObjectDataReply{

	// logger
	Logger logger = LoggerFactory.getLogger(Overlay.class);
	
	private Overlay overlay; 
	
	private MusicStorageWatcher musicStorage;
	private SongTagger songTagger;
	private SearchTermProvider searchTermProvider;
	
	
	/**
	 * default constructor
	 * @param musicStorage handler of the local music storage
	 */
	public MessageListener(MusicStorageWatcher musicStorage, SongTagger songTagger, Overlay overlay){
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
