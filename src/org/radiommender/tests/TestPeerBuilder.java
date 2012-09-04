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
package org.radiommender.tests;

import java.io.File;
import java.io.IOException;

import org.radiommender.controller.P2POverlay;
import org.radiommender.controller.RecommenderSystem;
import org.radiommender.controller.SongHandler;

import net.tomp2p.p2p.Peer;

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
