/**
 * 
 */
package p2pct.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.P2PChallengeTask;
import p2pct.controller.overlay.MessageListener;
import p2pct.controller.recommender.RecommenderFeeder;
import p2pct.controller.songhandler.AffinityFetcher;
import p2pct.controller.songhandler.MusicStorageWatcher;
import p2pct.controller.songhandler.SongFetcher;
import p2pct.controller.songhandler.SongListWorker;
import p2pct.controller.songhandler.SongTagger;
import p2pct.controller.songhandler.SonglistFetcher;
import p2pct.gui.Ui;
import p2pct.model.PlayListEntry;
import p2pct.model.Song;
import p2pct.model.SongFile;
import p2pct.model.SongTag;
import p2pct.model.VotingMessage;
import p2pct.utils.ConfigurationFactory;
import p2pct.utils.CountingBloomFilter;
import p2pct.utils.ExecutorPool;
import p2pct.utils.SafeTreeMap;

/**
 * SongHandler Module Master
 * 
 * All inter-module functionality is accessible through this class. 
 * 
 * The SongHandler has the following responsibilities:
 * - Watch local library and share it with other peers
 * - Fetch and provide songlists
 * - Fetch and provide affinity lists
 * 
 * @author nicolas baer
 */
public class SongHandler {
	// logger
	Logger logger = LoggerFactory.getLogger(SongHandler.class);
		
	// external module
	private P2POverlay overlay;
	private RecommenderSystem recommenderSystem;
	
	// module internal
	private MusicStorageWatcher musicStorageWatcher;
	private SongFetcher songFetcher;
	private SonglistFetcher songListFetcher;
	private AffinityFetcher affinityFetcher;
	private SongListWorker songListWorker = null;
	private PlayListQueue<PlayListEntry> rdySongs;
	private SongTagger songTagger;

	private Ui ui;
	
	/**
	 * default constructor
	 * @param musicPath 
	 * @throws FileNotFoundException 
	 */
	public SongHandler(P2POverlay overlay, String musicPath) throws FileNotFoundException{
		
		this.overlay = overlay;
		
		File musicPathFile = new File(musicPath);
		if (!musicPathFile.exists()) {
			throw new RuntimeException("Path does not exist: " + musicPath);
		}
		
		// init module internal stuff
		this.affinityFetcher = new AffinityFetcher(this.overlay);
		this.musicStorageWatcher = new MusicStorageWatcher(musicPathFile);
		this.musicStorageWatcher.initSongMapping();
		this.songFetcher = new SongFetcher(overlay, this.musicStorageWatcher);
		this.songListFetcher = new SonglistFetcher(overlay);
		this.songTagger = new SongTagger(overlay);
		ExecutorPool.getGeneralExecutorService().execute(this.songTagger);
		
		//create queue, length will determine how many songs are being fetched in advance. 
		this.rdySongs = new PlayListQueue<PlayListEntry>(new Integer(ConfigurationFactory.getProperty("songhandler.downloadlist.length")));
	}
	
	/**
	 * Constructor to overwrite the music library.
	 * Mainly used to create test peers.
	 * @throws FileNotFoundException 
	 */
	public SongHandler(P2POverlay overlay, File musicLibrary) throws FileNotFoundException{
		this.overlay = overlay;
		
		// init module internal stuff
		this.musicStorageWatcher = new MusicStorageWatcher(musicLibrary);
		this.musicStorageWatcher.initSongMapping();
		this.songFetcher = new SongFetcher(overlay, this.musicStorageWatcher);
		this.songListFetcher = new SonglistFetcher(overlay);
		this.songTagger = new SongTagger(overlay);
	}
	
	/**
	 * Fetches the next song, which is downloaded.
	 * @return
	 */
	public PlayListEntry getRdySong(){
		PlayListEntry playListEntry = this.rdySongs.poll();
		if (playListEntry==null) {
			return null;
		}
		else {
			this.updatePlayList(new ArrayList<PlayListEntry>(this.rdySongs));
			return playListEntry;
		}
	}

	/**
	 * Starts the songlist worker. This will poll the recommender system for new songs and
	 * fetches the songs from other peers.
	 */
	public void startSongListWorker(){
		if(this.songListWorker == null){
			this.songListWorker = new SongListWorker(this, this.recommenderSystem, this.songFetcher);
			this.songListWorker.setActive(true);
			ExecutorPool.getGeneralExecutorService().execute(this.songListWorker);
		}
		else {
			//just reactivates the thread, which will keep on running
			this.songListWorker.setActive(true);
		}
	}
	
	public void stopSongListWorker(){
		if(this.songListWorker != null){
			//just deactivates the thread, which will keep on running
			this.songListWorker.setActive(false);
		}
	}
	
	/**
	 * registers a message listener for direct inter-peer messaging.
	 */
	public void registerMessageListener(){
		MessageListener messageListener = new MessageListener(this.musicStorageWatcher, this.songTagger, this.overlay);
		this.overlay.setMessageListener(messageListener);
	}
	
	/**
	 * Clears all cached songs.
	 */
	public void clearRdySongs(){
		this.rdySongs.clear();
		this.updatePlayList(new ArrayList<PlayListEntry>());
	}
	
	/**
	 * Publishes the local songs and song lists to the DHT.
	 */
	public void publishLocalMusic(){
		logger.info("Starting to publish local music do DHT");
		this.storeLocalMusicInDHT();
		this.storeLocalSonglist();
		logger.info("Done publishing local music to DHT");
	}
	
	
	/**
	 * Fetches the song list of a remote peer. It will try to get it from the DHT, if this doesn't work,
	 * the module will communicate with the peer itself.
	 * 
	 * @param peerAddress peer to fetch song list of
	 * @return song list of remote peer. Returns null if nothing is found. 
	 */
	public CountingBloomFilter<Song> fetchRemoteSongList(PeerAddress peerAddress){
		try {
			CountingBloomFilter<Song> songList = this.songListFetcher.fetchRemoteSongList(peerAddress);
			return songList;
		} catch (IOException e) {
			logger.error("songhandler: could not fetch song list from peer: " + peerAddress.getID().toString());
		}
		
		return null;
	}
	
	/**
	 * Fetches the song list of the local music library.
	 * 
	 * @return local song list
	 */
	public Set<Song> fetchLocalSongList(){
		return this.musicStorageWatcher.getMusicLibrary();
	}
	
	
	/**
	 * Fetches the affinity list of a remote peer.
	 * 
	 * @param peerAddress address of remote peer
	 * @return affinity list - returns null if nothing is found
	 */
	public SafeTreeMap<Float, PeerAddress> fetchRemoteAffinityList(PeerAddress peerAddress){
		System.out.println(peerAddress);
		try {
			SafeTreeMap<Float, PeerAddress> affinityList = this.affinityFetcher.fetchRemoteAffinity(peerAddress);
			return affinityList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Stores the local music library into the DTH.
	 * The key of the song will be the key in the DHT. The value is this peers address.
	 */
	private void storeLocalMusicInDHT(){
		// fetch local song list
		HashSet<Song> songList = (HashSet<Song>) this.musicStorageWatcher.getMusicLibrary();
		
		if (songList==null) {
			return;
		}
		for(Song song : songList){
			// store song reference
			boolean success = this.overlay.addToTracker(song.getKey().toString());	
		 
			if(!success){
				logger.error("songhandler: couldn't add song to tracker: " + song.getKey());
			} else{
				logger.info("songhandler: added song to tracker: " + song.getKey());
			}
			
			// store song tags
			if(song != null){
			    //TODO: remove the static hack!!
			    for(int i=0;i<P2PChallengeTask.voteUp;i++) {
			        try{
			            this.overlay.lookupAndSendMessage(song.getArtist(), new VotingMessage(song.getArtist(), new SongTag[]{new SongTag(song.getGenre())}, song, SongTagger.UPVOTE));
			            this.overlay.lookupAndSendMessage(song.getGenre(), new VotingMessage(song.getGenre(), new SongTag[]{new SongTag(song.getArtist())}, song, SongTagger.UPVOTE));
			        } catch (Exception e) {
			            // nothing to do here
			        }
			    }
			}
		}
	}
	
	/**
	 * Stores the local song list into the DHT.
	 */
	private void storeLocalSonglist(){
		// fetch local song list
		Set<Song> songList = this.musicStorageWatcher.getMusicLibraryAsBloomFilter();
		
		try {
			overlay.put(overlay.getPeer().getPeerAddress().getID().toString(), new Data(songList));
			logger.info("songhandler: stored songlist in dht");
		} catch (IOException e) {
			logger.error("songhandler: could not store songlist in dht local peer");
		}
	}
	
	/**
	 * Stores the local affinity list into the DHT
	 */
	public void storeLocalAffinityList(){
		// fetch affinity list from recommender
		TreeMap<Float, PeerAddress> affinityList = this.recommenderSystem.getAffinityList();
		
		try {
			overlay.put(AffinityFetcher.AFFINITY_PREFIX + overlay.getPeer().getPeerAddress().getID(), new Data(affinityList));
		} catch (IOException e) {
			logger.error("songhandler: could not store local affinity list");
		}
	}
	
	
	/**
	 * Registers the recommender system to share the affinity list.
	 * 
	 * @param recommenderSystem 
	 */
	public void registerRecommenderSystem(RecommenderSystem recommenderSystem){
		this.recommenderSystem = recommenderSystem;
	}

	public void addSongToPlayList(PlayListEntry playListEntry) throws InterruptedException {
		logger.info("Added to playlist: "+playListEntry.toString());
		this.rdySongs.offer(playListEntry);
		this.updatePlayList(new ArrayList<PlayListEntry>(this.rdySongs));
	}

	public void registerUi(Ui ui) {
		this.ui = ui;
	}

	void updateCurrentSong(Song song, String origin) {
		if (this.ui!=null) {
			this.ui.updateCurrentSong(song, origin);
		}
	}

	private void updatePlayList(List<PlayListEntry> songs) {
		if (this.ui!=null) {
			this.ui.updatePlayList(songs);
		}
	}
}
