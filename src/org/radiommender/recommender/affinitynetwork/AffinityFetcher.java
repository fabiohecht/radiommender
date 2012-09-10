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
package org.radiommender.recommender.affinitynetwork;

import java.io.IOException;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.radiommender.overlay.Overlay;
import org.radiommender.utils.SafeTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	private Overlay overlay;
	
	
	
	/**
	 * default constructor
	 */
	public AffinityFetcher(Overlay overlay){
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
