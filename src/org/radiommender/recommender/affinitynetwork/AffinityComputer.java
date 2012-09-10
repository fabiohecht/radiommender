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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.radiommender.model.AffinityEntry;
import org.radiommender.model.Song;
import org.radiommender.overlay.Overlay;
import org.radiommender.recommender.RecommenderSystem;
import org.radiommender.ui.Ui;
import org.radiommender.utils.CountingBloomFilter;
import org.radiommender.utils.SafeTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

/**
 * Compute affinity of local peer and a remote peer on the basis of the shared
 * .mp3 files.
 * 
 * @author robert erdin
 * @mail robert.erdin@gmail.com
 * 
 */
public class AffinityComputer implements Runnable {
	// logger
	Logger logger = LoggerFactory.getLogger(AffinityComputer.class);

	private final HashSet<Song> localSongs;
	private final HashSet<PeerAddress> processedPeers;
	private final HashSet<PeerAddress> affinityRequested;
	private final RecommenderSystem recommenderSystem;
	private final Ui ui;
	private volatile SafeTreeMap<Float, PeerAddress> affinityList; // convenience
	private final ArrayList<AffinityEntry> actualListUI;
	private final Overlay overlay;
	private boolean activated;
	private final PeerAddress selfAddr;

	public AffinityComputer(Set<Song> localSongs, RecommenderSystem recommenderSystem, Overlay overlay, Ui ui) {
		this.localSongs = (HashSet<Song>) localSongs;
		this.recommenderSystem = recommenderSystem;
		this.activated = true;
		this.affinityList = this.recommenderSystem.getAffinityList();
		this.actualListUI = new ArrayList<AffinityEntry>();
		this.processedPeers = new HashSet<PeerAddress>();
		this.affinityRequested = new HashSet<PeerAddress>();
		this.overlay = overlay;
		this.ui = ui;
		// add own address to prevent loops
		this.selfAddr = this.recommenderSystem.getOverlay().getPeer().getPeerAddress();
		this.processedPeers.add(this.selfAddr); 
		this.affinityRequested.add(this.selfAddr);
	}

	/**
	 * Try to stop thread.
	 */
	public void stopThread() {
		this.activated = false;
	}

	/**
	 * <b>UNUSED, check if working with CountingBloomFilters if needed!</b> Use
	 * of computeBordaAffinity instead of computeSimpleAffinity recommended.
	 * Basic affinity computing function.
	 * 
	 * @param remoteSongs
	 *            - List with songs of remote peer
	 * @return affinity value (size of intersection of local and remote songs)
	 */
	@SuppressWarnings("unused")
	private int computeSimpleAffinity(Set<Song> remoteSongs) {

		@SuppressWarnings("unchecked")
		HashSet<Song> tmpLocalSongs = (HashSet<Song>) this.localSongs.clone();

		System.out.println("remote song list siz:" + remoteSongs.size());

		tmpLocalSongs.retainAll(remoteSongs);

		return tmpLocalSongs.size();
	}

	/**
	 * Affinity computing according to the Borda rule.
	 * 
	 * @param remoteSongs
	 *            as a CountingBloomFilter
	 * @return
	 */
	private float computeBordaAffinity(CountingBloomFilter<Song> remoteSongs) {

		float affinity = 0;

		if (remoteSongs == null || remoteSongs.isEmpty()) {
			return 0;
		}

		// compute borda affinity
		for (Song current : this.localSongs) {

			// create partial Songs for convenience reasons:
			Song matchAlbum = new Song(current.getGenre(), current.getArtist(), current.getAlbum(), null);
			Song matchArtist = new Song(current.getGenre(), current.getArtist(), null, null);
			Song matchGenre = new Song(current.getGenre(), null, null, null);

			// complete match --> weight: 4
			if (remoteSongs.contains(current)) {
				affinity = affinity + 4;

				// remove song from CountingBloomFilter
				remoteSongs.reduceCount(current);
				remoteSongs.reduceCount(matchAlbum);
				remoteSongs.reduceCount(matchArtist);
				remoteSongs.reduceCount(matchGenre);
			}
			// matching album --> weight: 3
			else if (remoteSongs.contains(matchAlbum)) {
				affinity = affinity + 3;

				// remove song from CountingBloomFilter
				remoteSongs.reduceCount(matchAlbum);
				remoteSongs.reduceCount(matchArtist);
				remoteSongs.reduceCount(matchGenre);
			}
			// matching artist --> weight: 2
			else if (remoteSongs.contains(matchArtist)) {
				affinity = affinity + 2;

				// remove song from CountingBloomFilter
				remoteSongs.reduceCount(matchArtist);
				remoteSongs.reduceCount(matchGenre);
			}
			// matching genre --> weight: 1
			else if (remoteSongs.contains(matchGenre)) {
				affinity = affinity + 1;

				// remove song from CountingBloomFilter
				remoteSongs.reduceCount(matchGenre);
			}

		}

		// collection size normalization
		affinity = affinity / (localSongs.size() * 4);

		return affinity;
	}

	/**
	 * compute affinity from the initial collection of neighbour peers. BLOCKING
	 * IF THERE ARE NO NEIGHBOURS!!!
	 */
	private void computeFromNeighbours() {
		logger.info("AffinityComputer: Start computeFromNeighbour");

		while (this.recommenderSystem.getNeighbours().getAll().size() < 1) {
			try {
				Thread.sleep(5000);
				logger.debug("No neighbours... waiting 5s...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// iterate over all neighbour peers
		for (PeerAddress peer : this.recommenderSystem.getNeighbours().getAll()) {
			logger.debug("current neighbour:" + peer.toString());

			// affinity to current peer
			float affinity = this.computeBordaAffinity(this.recommenderSystem.getSongHandler().fetchRemoteSongList(peer));

			// add affinity to affinityList
			this.recommenderSystem.addAffinity(affinity, peer);
			
			
			// notify UI of new Peer
			// add to exact list
			String peerID = peer.getID().toString();
			this.addUIAffinity(new AffinityEntry(peerID.substring(peerID.length()-4), affinity));
			ui.updateActualAffinity(this.actualListUI);

			// add peer to already processed peers
			this.processedPeers.add(peer);
		}
		logger.info("AffinityComputer: End computeFromNeighbour");
	}

	/**
	 * compute affinity for each peer of a remote affinity list.
	 * 
	 * @param tmpAffinityList
	 */
	private void computeFromAffinityList(TreeMap<Float, PeerAddress> tmpAffinityList) {
		logger.debug("AffinityComputer: Start computeFromAffinityList");

		for (Entry<Float, PeerAddress> current : tmpAffinityList.entrySet()) {

			if (!this.processedPeers.contains(current.getValue())) {

				// affinity to current peer
				float affinity = this.computeBordaAffinity(this.recommenderSystem.getSongHandler().fetchRemoteSongList(current.getValue()));

				// add affinity to affinityList
				this.recommenderSystem.addAffinity(affinity, current.getValue());

				// add peer to already processed peers
				this.processedPeers.add(current.getValue());

			}

		}
		logger.debug("AffinityComputer: End computeFromAffinityList");
	}

	@Override
	public void run() {
		long before;
		
		// Ordered map containing all peers received from neighbors.
		//ordered by approximate affinity value based on transitivity.
		SafeTreeMap<Float, PeerAddress> frontierList = new SafeTreeMap<Float, PeerAddress>();
		ArrayList<AffinityEntry> approxListUI = new ArrayList<AffinityEntry>();
		
		// initially compute affinities to neighbours
		// BLOCKING IF NO NEIGHBOURS
		this.computeFromNeighbours();

		while (this.activated) {

			boolean stateChanged = false;
			// just in case there are new neighbours
			if(!this.processedPeers.containsAll(this.recommenderSystem.getNeighbours().getAll())){
				this.computeFromNeighbours();	
				stateChanged = true;
			}
			
			//enter initial affinityList into DHT
			if(stateChanged){
				storeAffinityToDHT();
				stateChanged = false;
			}
			
			
			// TODO Limit size in order to scale well!
			frontierList.clear();
			
			//clear ui list and notify GUI
			approxListUI.clear();
			ui.updateEstimatedAffinity(approxListUI);
			//this.processedPeers.clear();

			
			// process all peers in affinity list in descending order to get approximate affinities...
			int terminator = 0;
			for (Float direct : this.affinityList.descendingKeySet()){
				// make sure only 5 approximate peers get inserted into frontier in one run to keep things going.
				if(terminator == 5){
					break;
				}
				if(!this.affinityRequested.contains(this.affinityList.get(direct)) && this.activated){
					// ...get affinityList from that peer (blocking)
					before = System.currentTimeMillis();
					SafeTreeMap<Float, PeerAddress> remotePeerAffinityList = this.recommenderSystem.getSongHandler().fetchRemoteAffinityList(this.affinityList.get(direct));
					logger.debug("Time to fetch remote affinityList: " + (System.currentTimeMillis() - before) + "ms");
					
					// put all peers of the remote affinity list into the frontier.
					Float approxAffinity;
					if(remotePeerAffinityList != null && remotePeerAffinityList.size()>0 && this.activated){
						for (Float indirect : remotePeerAffinityList.descendingKeySet()) {
							if(terminator == 5){
								break;
							}
							// calculate approximate affinity and put it into frontier
							approxAffinity = direct * indirect;
							frontierList.safePut(approxAffinity, remotePeerAffinityList.get(indirect));
							
							//notify GUI
							String peerID = remotePeerAffinityList.get(indirect).getID().toString();
							approxListUI.add(new AffinityEntry(peerID.substring(peerID.length()-4), approxAffinity));
							terminator++;
						}
					}
					else{
						logger.error("Either an empty affinityList recieved or not recieved at all");
					}
					
					this.affinityRequested.add(this.affinityList.get(direct));
					
				}
			}
			

			// Calculate actual affinities for a fixed amount of peers in the
			// frontier
			int i = 0;
			for (Float current : frontierList.descendingKeySet()) {
				if (i == 5) {
					break;
				}
				if(!this.processedPeers.contains(frontierList.get(current))){	
					
					PeerAddress toBeAdded = frontierList.get(current);
					
					Float affinity = this.computeBordaAffinity(this.recommenderSystem.getSongHandler().fetchRemoteSongList(toBeAdded));
					this.recommenderSystem.addAffinity(affinity, toBeAdded);
					stateChanged = true;
					
					// add to processed peers to make sure it does not get added multiple times
					this.processedPeers.add(toBeAdded);
					
					//
					// notify GUI
					//
					
					// add to exact list
					String peerID = toBeAdded.getID().toString();
					this.addUIAffinity(new AffinityEntry(peerID.substring(peerID.length()-4), affinity));
					ui.updateActualAffinity(this.actualListUI);
					
					// remove from approx list
					for(AffinityEntry approx : approxListUI){
						if(approx.getAffinity() == current){
							approxListUI.remove(approx);
							break;
						}
					}
					ui.updateEstimatedAffinity(approxListUI);
					i++;
				}

			}

			//this.computeFromAffinityList(frontierList);
			
			//store affinity List to DHT
			if(stateChanged){
				storeAffinityToDHT();
			}
			

			System.out.println(this.affinityList);

			logger.info("AffinityComputer: complete affinity graph crawled, will pause for 1min!");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Will restart building the affinity list");
		}
	}
	
	private void addUIAffinity(AffinityEntry entry){
		boolean existing = false;
		for(AffinityEntry current : this.actualListUI){
			if(current.equals(entry)){
				existing = true;
			}
		}
		if(!existing){
			this.actualListUI.add(entry);
		}
	
	}

	private void storeAffinityToDHT() {
		Data tmpAffList = null;
		try {
			tmpAffList = new Data(this.affinityList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.overlay.put(AffinityFetcher.AFFINITY_PREFIX + this.overlay.getPeer().getPeerAddress().getID().toString(), tmpAffList);
	}
}