package p2pct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.P2POverlay;
import p2pct.controller.Player;
import p2pct.controller.RecommenderSystem;
import p2pct.controller.SongHandler;
import p2pct.gui.P2PGui2;
import p2pct.tests.TestPeerBuilder;
import p2pct.utils.ConfigurationFactory;

/**
 * This class is the main entry point of the software.
 * It will initialize all modules, register the dependencies and finally start the modules.
 * 
 * @author nicolas baer
 */
public class P2PChallengeTask {

	final private static Logger logger = LoggerFactory.getLogger(P2PChallengeTask.class);

	private static final int BOOTSTRAP_RETRIES = 3;

	private RecommenderSystem recommenderSystem;
	private Player player;
	private P2POverlay p2pOverlay;
	
	final static public Random random = new Random();

	static {
		try {
			LogManager.getLogManager().readConfiguration(P2PChallengeTask.class.getResourceAsStream("/jdklog.properties"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static int voteUp = 1;
	
	/**
	 * Main method. Use  this to start the program.
	 * @param args bootstrapPeer, musicPath, port
	 */
	public static void main(String[] args) {
		
		String bootstrapIp = null, musicPath = null;
		int port, bootstrapPort=-1;
		
		if(args.length > 0){
			String bootstrapPeer = args[0];
			if (bootstrapPeer.equals("first")) {
				bootstrapIp = "";
			}
			else {
				String[] bootstrap = bootstrapPeer.split(":");
				if(bootstrap.length > 0){
					bootstrapIp = bootstrap[0];
					if(bootstrap.length > 1){
						bootstrapPort = Integer.parseInt(bootstrap[1]);
					}
				}
			}
		}
		else {
			bootstrapIp = ConfigurationFactory.getProperty("overlay.network.bootstrap.ip");
			bootstrapPort = new Integer(ConfigurationFactory.getProperty("overlay.network.bootstrap.port"));
		}
			
		if(args.length > 1){
			ConfigurationFactory.setProperty("file.store.path", musicPath = args[1]);
		}
		else {
			musicPath = ConfigurationFactory.getProperty("file.store.path");
		}
		
		if(args.length > 2){
			port = Integer.parseInt(args[2]);
		}		
		else {
			port = new Integer(ConfigurationFactory.getProperty("overlay.network.port"));
		}
		
		if(args.length > 3){
            voteUp = Integer.parseInt(args[3]);
        }
		
		new P2PChallengeTask(bootstrapIp, bootstrapPort, musicPath, port, port%10);
	}
	
	public P2PChallengeTask(String bootstrapIp, int bootstrapPort, String musicPath, int port, int position) {
		// read arguments
		boolean askBootstrap = bootstrapIp==null;  //bootstrapIp.isEmpty() == first peer in the network, null == ask
		boolean askMusicPath = musicPath==null;

		// initialize
		p2pOverlay = new P2POverlay();
		SongHandler songHandler;
		try {
			// initialize modules
			songHandler = new SongHandler(p2pOverlay, musicPath);
			recommenderSystem = new RecommenderSystem(p2pOverlay, songHandler);
			player = new Player();
			P2PGui2 window = new P2PGui2(this, askBootstrap, askMusicPath, position);
			
			// register dependencies
			songHandler.registerRecommenderSystem(recommenderSystem);
			player.registerSongHandler(songHandler);
			//window.registerPlayerActionListener(player);
			songHandler.registerUi(window);
			recommenderSystem.registerUi(window);
			//player.registerGUI(window);
			player.registerRecommenderSystem(recommenderSystem);
			
			// start modules
			try {
				// bootstrap peer
				int i=0;
				boolean bootstrapped = false;
				while (!bootstrapped && i < BOOTSTRAP_RETRIES) {
					bootstrapped = p2pOverlay.bootstrap(bootstrapIp, bootstrapPort, port);
					if (!bootstrapped) {
						i++;
						logger.warn("bootstrap failed ("+i+"/"+BOOTSTRAP_RETRIES+"), waiting 2 seconds");
						Thread.sleep(2000);
					}
				}				
				if (!bootstrapped) {
					logger.warn("bootstrap failed ("+i+"/"+BOOTSTRAP_RETRIES+"), giving up");
					System.exit(4);
				}

				songHandler.registerMessageListener();
				window.updatePeerId(p2pOverlay.getId());

				// build test peers (virtually)
				// to enable: make sure to set the property "file.testdata", within this directory should be directories for the test
				// peers to provide music ( e.g. 01, 02, 03 etc). Start buildTestPeers with the amount of test peers. This must match
				// the directory structure.
				/*
				TestPeerBuilder testPeerBuilder = new TestPeerBuilder();
				testPeerBuilder.buildTestPeers(5, new File(ConfigurationFactory.getProperty("file.testdata")), p2pOverlay.getPeer());
				*/
				
				// publish local music
				songHandler.publishLocalMusic();
				
				// start recommending
				recommenderSystem.startRecommending(player);
				
				player.addObserver(recommenderSystem.getRecommenderFeeder());
				
				// player start!
				//window.getFrmPpInternetRadio().setVisible(true);
				window.showFrame();
				player.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
	}

	public void play(String searchTerm) {
		this.player.play(searchTerm);
	}

	public void stop() {
		this.player.stop();
	}

	public void skip() {
		this.player.skip();		
	}

	public void shutdownApplication() {
		this.p2pOverlay.shutdown();
	}

}
