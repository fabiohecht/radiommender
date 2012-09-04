/**
 * 
 */
package p2pct.controller.songhandler;

import java.io.IOException;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.P2POverlay;
import p2pct.utils.SafeTreeMap;

/**
 * Fetches the affinity list of another peer.
 * Since the affinity list is always saved to the DHT, this operation is nothing more than a single lookup.
 * 
 * @author nicolas baer
 */
public class AffinityFetcher {
	// logger
	Logger logger = LoggerFactory.getLogger(AffinityFetcher.class);
	
	// static vars
	public static final String AFFINITY_PREFIX = "A";
	
	// external modules
	private P2POverlay overlay;
	
	
	
	/**
	 * default constructor
	 */
	public AffinityFetcher(P2POverlay overlay){
		this.overlay = overlay;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public SafeTreeMap<Float, PeerAddress> fetchRemoteAffinity(PeerAddress peerAddress) throws ClassNotFoundException, IOException{
		// look for affinity list in DHT
		Data dhtData = overlay.get(AFFINITY_PREFIX + peerAddress.getID().toString());
		
		if(dhtData != null && dhtData.getObject() != null && dhtData.getObject() instanceof SafeTreeMap<?, ?>){
			return (SafeTreeMap<Float, PeerAddress>) dhtData.getObject();
		}
		
		return null;
	}
}
