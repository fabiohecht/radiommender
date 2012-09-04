/**
 * 
 */
package p2pct.controller.songhandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.P2POverlay;
import p2pct.model.Message;
import p2pct.model.Song;
import p2pct.utils.CountingBloomFilter;

/**
 * Handles the songlist fetching either from the DHT or from the other peers directly by messaging service.
 * @author nicolas baer
 */
public class SonglistFetcher {
	// logger
	Logger logger = LoggerFactory.getLogger(SongFetcher.class);
	
	// external modules
	private P2POverlay overlay;

	/**
	 * default constructor
	 */
	public SonglistFetcher(P2POverlay overlay){
		this.overlay = overlay;
		
	}
	
	/**
	 * Fetches the remote song list from the given peer.
	 * It will first try to find it on the DHT. If this fails for whatever reason, it will ask the peer
	 * directly through messages to send the song list.
	 * @param peerAddress peer to fetch song list from
	 * @return song list
	 * @throws IOException 
	 */
	public CountingBloomFilter<Song> fetchRemoteSongList(PeerAddress peerAddress) throws IOException{
		// fetch from DHT
		Data dhtData = this.overlay.get(peerAddress.getID().toString());
		try {
			if(dhtData != null && dhtData.getObject() != null && dhtData.getObject() instanceof Set<?>){
				CountingBloomFilter<Song> songList = (CountingBloomFilter<Song>) dhtData.getObject();
				return songList;
			} else{
				// DHT failed, try by message
				logger.warn("songlist fetcher: couldn't fetch songlist of peer (" + peerAddress.getID().toString() + ") by DHT storage.");
				CountingBloomFilter<Song> songList = this.fetchRemoteSongListByMessage(peerAddress);
				
				return songList;
			}
		} catch (Exception e) {
			logger.warn("songlist fetcher: couldn't fetch songlist of peer (" + peerAddress.getID().toString() + ") by DHT storage.");
			logger.warn(e.getMessage());
			this.fetchRemoteSongListByMessage(peerAddress);
		} 
		
		return null;
	}
	
	/**
	 * Fetches the remote song list by message. Rather use the fetchRemoteSongList function in order to check the DHT first.
	 * @param peerAddress peer to fetch song list from
	 * @return song list
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public CountingBloomFilter<Song> fetchRemoteSongListByMessage(PeerAddress peerAddress) throws IOException{
		Message message = new Message();
		message.setCommand(Message.CMD_SONGLIST_REQUEST);
		
		Object response = this.overlay.sendMessage(peerAddress, message);
		if(response != null && response instanceof Set<?>){
			return (CountingBloomFilter<Song>) response;
		}
		
		return null;
	}
	
	
}
