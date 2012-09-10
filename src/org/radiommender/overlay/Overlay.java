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
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.futures.FutureTracker;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.p2p.RequestP2PConfiguration;
import net.tomp2p.p2p.builder.DHTBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMap;
import net.tomp2p.storage.Data;
import net.tomp2p.storage.TrackerData;

import org.radiommender.utils.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//NativeLibrary.addSearchPath("libvlc", "C:\Program Files\VideoLAN\VLC");

/**
 * MyPeer builds the local peer instance and handles the interaction with the overlay network.
 * All basic networking (e.g. bootstrap) and DHT (e.g. put/get) mechanisms are implemented for the challenge task use case.
 * In order to work with this class, the following configurations have to be set in the configuration file:
 * - overlay.network.port
 * - overlay.network.bootstrap.ip
 * - overlay.network.bootstrap.port
 * 
 * @author nicolas baer
 */
public class Overlay implements OverlayPeer{
	// logger
	Logger logger = LoggerFactory.getLogger(Overlay.class);
	
	// peer ip
	private final static String PEER_IP = ConfigurationFactory.getProperty("overlay.network.peer.ip");
	
	// random number
	private final static Random rnd = new Random();
	
	// peer
	private Peer peer;
			
	
	/**
	 * Bootstraps to the bootstrapping node as defined in the configuration file.
	 * If the bootstrapping node is configured as 127.0.0.1, the node will only be initialized with a peer id, since it is the
	 * bootstrapping node itself.
	 * 
	 * @throws IOException
	 */
	public boolean bootstrap(String bootstrapIp, int bootstrapPort, int port) throws IOException{
		// initialize peer
		InetAddress address;
		if(PEER_IP == null || PEER_IP.isEmpty()){
			address = InetAddress.getLocalHost();
		} else {
			address = InetAddress.getByName(PEER_IP);
		}
		
		if (this.peer==null || !this.peer.isListening()) {
			Number160 peerId = new Number160(rnd);
			this.peer = new PeerMaker(peerId).setPorts(port).setBindings(new Bindings(address)).makeAndListen();
			logger.info("overlay.peer: new peer id is " + peerId);
		}
		
		// check if this node is not the bootstrapping node
		if(bootstrapIp!=null && !bootstrapIp.isEmpty()) {
			// discover bootstrap peer
			//System.out.println("ABCD: " + bootstrapIp);
			InetAddress addressBootstrap = InetAddress.getByName(bootstrapIp);
			//System.out.println("EFGH: " + addressBootstrap.getHostAddress());
			//FutureDiscover fd = peer.discover().setInetAddress(addressBootstrap).setPorts(bootstrapPort).start();
			//fd.awaitUninterruptibly();
			//if (fd.isFailed()) {
			//	logger.warn("cannot find bootstrap peer ("+bootstrapIp+":"+bootstrapPort+"), bootstrap failed");
			//	return false;
			//}
			
			//logger.info("overlay.network: discovered peer: " + fd.getPeerAddress().getID());
			
			// bootstrap
			FutureBootstrap fb = peer.bootstrap().setInetAddress(addressBootstrap).setPorts(bootstrapPort).start();
			fb.awaitUninterruptibly();
			if (fb.isFailed()) {
				logger.warn("bootstrap failed to peer ("+bootstrapIp+":"+bootstrapPort+")");
				return false;
			}
			
			//logger.info("overlay.network: bootstrapped to peer: " + fd.getPeerAddress().getID());
		}
		
		return true;
	}
	
	/**
	 * Bootstraps to a well known peer. Instead of using the ip address and port from the configuration file
	 * to resolve the bootstrap peer, a well known peer is provided.
	 * This function is mainly used to build test peers locally.
	 * 
	 * @param bootstrapPeer peer to bootstrap to
	 * @throws IOException
	 */
	public void bootstrapToKnownPeer(Peer bootstrapPeer) throws IOException{
		// initialize peer
		InetAddress address = InetAddress.getLocalHost();
		logger.debug(address.toString());
		Number160 peerId = new Number160(rnd);
		this.peer = new PeerMaker(peerId).setMasterPeer(bootstrapPeer).makeAndListen();
		logger.info("overlay.peer: new peer id is " + peerId);
		
		// discover bootstrap peer
		FutureDiscover fd = peer.discover().setPeerAddress(bootstrapPeer.getPeerAddress()).start();
		fd.awaitUninterruptibly();
		logger.info("overlay.network: discovered peer: " + fd.getPeerAddress().getID());
		
		// bootstrap
		FutureBootstrap fb = peer.bootstrap().setPeerAddress(fd.getPeerAddress()).start();
		fb.awaitUninterruptibly();
		logger.info("overlay.network: bootstrapped to peer: " + fd.getPeerAddress().getID());
		
		FutureBootstrap tmp = bootstrapPeer.bootstrap().setPeerAddress(this.peer.getPeerAddress()).start();
		tmp.awaitUninterruptibly();

	}
	
	
	/**
	 * Puts the given key/value pair in the DHT. The key will be hashed before insert.
	 * 
	 * @param key Arbitrary String to hash as a key
	 * @param value Data to put into DHT
	 * @return success or not
	 */
	public boolean put(String key, Data value){		
		Number160 hash = Number160.createHash(key);
		FutureDHT dht = peer.put(hash).setData(value).start();
		try {
			dht.await(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("overlay.DHT: put " + key + " as " + hash);
		
		return dht.isSuccess();
	}
	
//	/**
//	 * Adds a value to an existing key.
//	 * @param key key to add data to
//	 * @param value data to add
//	 * @return success or not
//	 */
//	public boolean add(String key, Data value){
//		Number160 hash = Number160.createHash(key);
//		
//		FutureDHT dht = this.peer.add(hash).setData(value).start();
//		dht.awaitUninterruptibly();
//		
//		if(dht.isSuccess()){
//			logger.info("p2p.DHT: added value to key: " + key);
//			
//			return true;
//		}
//		
//		logger.error("overlay.DHT: couldn't add value to key: " + key);
//		
//		return false;
//	}
	
	/**
	 * Fetches the data to the provided key. The key will be hashed before the lookup starts.
	 * @param key Arbitrary String to look up in the DHT
	 * @return data stored in the DHT with the provided key
	 */
	public Data get(String key){
		FutureDHT dht = peer.get(Number160.createHash(key) ).start();
		dht.awaitUninterruptibly();
		
		logger.info("overlay.DHT: get request " + key);
		
		if(dht.isSuccess()){
			logger.info("overlay.DHT: got data for key: " + key);
			return dht.getData();
		}
		
		logger.error("overlay.DHT: get failed for key: " + key);
		
		return null;
	}
	
	/**
	 * Get data from local DHT storage.
	 * 
	 * @param key
	 * @return
	 */
	public Data getLocal(String key){
		Number160 hash = Number160.createHash(key);
		
		return this.peer.getPeerBean().getStorage().get(hash, DHTBuilder.DEFAULT_DOMAIN, Number160.ZERO);
	}
	
	/**
	 * Adds a key to tracker.
	 * @param key key to add to tracker
	 * @return
	 */
	public boolean addToTracker(String key){
		Number160 hash = Number160.createHash(key);
	
		// add to tracker
		FutureTracker tracker = peer.addTracker(hash).start();
		tracker.awaitUninterruptibly();
		
		// check for success
		if(tracker.isSuccess()){
			logger.info("overlay.DHT: tracker add " + key + " as " + hash);
			return true;
		}
		
		logger.error("overlay.DHT: tracker add FAILED "+ key + " as " + hash + " reason="+tracker.getFailedReason());
	
		return false;
	}
	
	
	public Collection<TrackerData> getTrackers(String key){
		// hash key
		Number160 hash = Number160.createHash(key);
		
		// find trackers
		FutureTracker tracker = peer.getTracker(hash).start();
		tracker.awaitUninterruptibly();
		
		if(tracker.isSuccess()){
			logger.info("overlay.DHT: got " + tracker.getTrackers().size() + " tracker(s) for key " + key);
			return tracker.getTrackers();
		}
		
		logger.error("overlay.DHT: tracker get failed for key + " + key);
		
		return null;
	}
	
	/**
	 * Sends a message to the provided peer address.
	 * 
	 * @param peerAddress address of peer to send a message to
	 * @param message message to send
	 * @return object received from the remote peer
	 * @throws IOException 
	 */
	public Object sendMessage(PeerAddress peerAddress, Object message) throws IOException{
		// TODO send direct
		FutureResponse fd = this.peer.sendDirect().setPeerAddress(peerAddress).setObject(message).start();
		fd.awaitUninterruptibly();
		
		logger.info("overlay.network: send message to: " + peerAddress.getID());
		
		if(fd.isSuccess()){
			logger.info("overlay.network: received response from: " + peerAddress.getID());
			return fd.getObject();
		}
		logger.error("overlay.network: no response received from: " + peerAddress.getID());
		
		return null;
	}
	
	
	public Object lookupAndSendMessage(String key, Object message) throws IOException{
		Number160 hash = Number160.createHash(key);
		
		// TODO send direct
		FutureDHT fd = this.peer.send(hash).setDirectReplication( false ).setRefreshSeconds( 0 ).setObject(message).start();
		fd.awaitUninterruptibly();
		
		//logger.info("overlay.network: send message to: " + peerAddress.getID());
		
		//if(fd.isSuccess()){
		return fd.getObject();
		//}
		//else {
		//	logger.warn("sending message failed: " + fd.getFailedReason());
		//}
		
		//return null;
	}
	
	/**
	 * Gets all known neighbors of the peer
	 * 
	 * @return all known neighbors
	 */
	public PeerMap getNeighbors(){
		 return this.peer.getPeerBean().getPeerMap();
	}

	/* (non-Javadoc)
	 * @see p2pct.controller.overlay.IPeer#remove(java.lang.String)
	 */
	@Override
	public boolean remove(String key) {
		FutureDHT dht = this.peer.remove(Number160.createHash(key)).start();
		try {
			dht.await(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("overlay.DHT: remove key: " + key);
		if(dht.isSuccess()){
			logger.info("overlay.DHT: key removed: " + key);
			return true;
		}
		logger.error("overlay.DHT: key not removed: " + key);
		
		return false;
	}
	
	
	/**
	 * Sets the message listener for the peer and enables messageing with other peers.
	 * Without a message listener file requests will end nowhere...
	 * 
	 * @param messageListener
	 */
	public void setMessageListener(MessageListener messageListener){
		// enable messaging
		this.peer.setObjectDataReply(messageListener);
	}

	/* (non-Javadoc)
	 * @see p2pct.controller.overlay.IPeer#shutdown()
	 */
	@Override
	public void shutdown() {
		this.peer.shutdown();
	}

	/**
	 * @return the peer
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer the peer to set
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	@Override
	public String getId() {
		return Overlay.shortenPeerId(this.peer.getPeerID());
	}

	public static String shortenPeerId(Number160 peerID) {
		return peerID.toString().substring(peerID.toString().length()-4);
	}
	

}
