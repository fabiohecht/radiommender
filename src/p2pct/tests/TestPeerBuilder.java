/**
 * 
 */
package p2pct.tests;

import java.io.File;
import java.io.IOException;

import net.tomp2p.p2p.Peer;
import p2pct.controller.P2POverlay;
import p2pct.controller.RecommenderSystem;
import p2pct.controller.SongHandler;

/**
 * @author nicolas baer
 */
public class TestPeerBuilder {
	
	public TestPeerBuilder(){
		
	}
	
	
	/**
	 * 
	 * 
	 * @param amount
	 * @param folder
	 * @throws IOException 
	 */
	public void buildTestPeers(int amount, File folder, Peer bootstrapPeer) throws IOException{
		for(int counter = 0; counter < amount; counter++){
			
			// init local song folder
			String folderName = new Integer(counter+1).toString();
			if(counter < 9){
				folderName = "0"+folderName;
			}
			File tempMusicFolder = new File(folder.getCanonicalPath()+System.getProperty("file.separator")+folderName);
		
			// initialize
			P2POverlay p2pOverlay = new P2POverlay();
			SongHandler songHandler = new SongHandler(p2pOverlay, tempMusicFolder);
			RecommenderSystem recommenderSystem = new RecommenderSystem(p2pOverlay, songHandler);
			
			// register dependencies
			songHandler.registerRecommenderSystem(recommenderSystem);
			
			// start doing stuff
			try {
				p2pOverlay.bootstrapToKnownPeer(bootstrapPeer);
				songHandler.registerMessageListener();
				songHandler.publishLocalMusic();
				
				//recommenderSystem.startRecommending();
						
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
