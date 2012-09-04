package p2pct.controller.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.RecommenderSystem;
import p2pct.model.Message;
import p2pct.model.PlayListEntry;
import p2pct.model.Song;
import p2pct.utils.CountingBloomFilter;

import net.tomp2p.peers.PeerAddress;

/**
 * This class creates the affinity playlist used by the recommender system.
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 * 
 */
public class AffinityPlayList implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(AffinityPlayList.class);
	
	private HashSet<PeerAddress> processedPeers;
	private boolean activated;
	private LinkedBlockingQueue<PlayListEntry> affinitySongs;
	private RecommenderSystem recommenderSystem;
	private TreeMap<Float, PeerAddress> affinityList;
	private ArrayList<Song> recommendedSongs;

	public AffinityPlayList(RecommenderSystem recommenderSystem) {
		this.recommenderSystem = recommenderSystem;
		this.activated = true;
		this.affinitySongs = recommenderSystem.getAffinitySongsQueue();
		this.affinityList = recommenderSystem.getAffinityList();
		this.processedPeers = new HashSet<PeerAddress>();
		this.recommendedSongs = new ArrayList<Song>();
	}

	/**
	 * Stop thread.
	 */
	public void stopThread() {
		this.activated = false;
	}
	
	private Song retrieveSongFromPeer(PeerAddress highest){
		
		
		CountingBloomFilter<Song> filter = new CountingBloomFilter<Song>(100, new int[500]);
		filter.addAll(this.recommendedSongs);
		
		Message songRequest = new Message();
		songRequest.setArguments(new Object[]{filter});
		songRequest.setCommand(Message.CMD_AFFINITY_SONGREQUEST);
		Object tmp = null;
		try {
			tmp = this.recommenderSystem.getOverlay().sendMessage(highest, songRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		if(tmp != null){
			Song song = (Song)tmp;
			return song;
		}
		else{
			logger.error("Did not recieve a Song from remote peer");
			return null;
		}
		
	}

	@Override
	public void run() {
		// repeat until stopped;
		
		while (this.activated) {
			//logger.debug("Starting PlayList");
			
			while (this.affinityList.size()==0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			while(this.affinityList.lastKey()<0.1f){
				try {
					Thread.sleep(1000);
					System.out.println("xxxx no suitable peer yet");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println("yyyy" + this.affinityList);
			
			// clear processed peers to start new the complete list was
			// processed once.
			//TODO LIMIT Queue size and enable clear again.

			// repeat until all peers in affinityList are processed.
			logger.debug("processed peers: " + this.processedPeers.size()+ ", affinity list: " + this.affinityList.size());
			
			PeerAddress currentPeer = null;
			for(float current : this.affinityList.descendingKeySet()){
				if(!this.processedPeers.contains(this.affinityList.get(current)) && current > 0.1f){
					currentPeer = this.affinityList.get(current);
				}
			}
			
			
			
			
			if(currentPeer == null){
				this.processedPeers.clear();
				this.recommendedSongs.clear();
				currentPeer = this.affinityList.lastEntry().getValue();
			}
			
			
			
			Song song = this.retrieveSongFromPeer(currentPeer);
			try {
				if(song != null){
					this.affinitySongs.offer(new PlayListEntry(song, "AN", -1f), Integer.MAX_VALUE, TimeUnit.SECONDS);
					logger.error("Size of RecommendedSongs: " + this.affinitySongs.size());
					this.recommendedSongs.add(song);
				}
				else{
					this.processedPeers.add(this.affinityList.lastEntry().getValue());
				}
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			try {
				Thread.sleep(5000); // prevent the peer from flooding
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		}
	}

}
