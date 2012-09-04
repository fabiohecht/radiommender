/**
 * 
 */
package p2pct.controller.overlay;

import java.io.IOException;

import p2pct.model.Message;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMap;
import net.tomp2p.storage.Data;

/**
 * Default interface for a peer implementation.
 * 
 * @author nicolas baer
 */
public interface IPeer {
	/**
	 * bootstraps the node
	 * @return 
	 * @throws IOException
	 */
	public boolean bootstrap(String bootstrapIp, int bootstrapPort, int port) throws IOException;
	
	/**
	 * put a key value pair into dht
	 * @param key identifier of value
	 * @param value value to put in
	 * @return
	 */
	public boolean put(String key, Data value);
	
	/**
	 * gets the value belonging to the given key from the dht
	 * @param key
	 * @return
	 */
	public Data get(String key);
	
	/**
	 * remove a key/value from dht
	 * @param key
	 * @return
	 */
	public boolean remove(String key);
	
	/**
	 * shuts down the peer and logs off the network
	 */
	public void shutdown();
	
	/**
	 * sends a message to another peer
	 * @param peerAddress
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public Object sendMessage(PeerAddress peerAddress, Object message) throws IOException;
	
	/**
	 * gets all known neighbors
	 * @return
	 */
	public PeerMap getNeighbors();
	
	/**
	 * returns the ID of this peer
	 * @return
	 */
	public String getId();
	
}
