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

import java.io.IOException;

import org.radiommender.model.Message;


import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMap;
import net.tomp2p.storage.Data;

/**
 * Default interface for a peer implementation.
 * 
 * @author nicolas baer
 */
public interface OverlayPeer {
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
