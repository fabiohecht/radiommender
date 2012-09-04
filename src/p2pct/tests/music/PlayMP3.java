package p2pct.tests.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map.Entry;

import p2pct.model.Song;
import p2pct.model.SongFile;

public class PlayMP3 implements Runnable {
	Boolean activated = true;
	
	
	@Override
	public void run() {
		p2pct.controller.songhandler.MusicStorageWatcher lib;
				
		try {
				lib = new p2pct.controller.songhandler.MusicStorageWatcher(new File("/home/bkey/BlackViolin")).initSongMapping();
				
				for(Entry<Integer, SongFile> entry : lib.getSongMapping().entrySet()){
					SongFile songFile = entry.getValue();
					File f = songFile.getFile();
					Song s = songFile.getSong();
					
					while(activated) {
					System.out.println(s.getArtist()+s.getAlbum()+s.getTitle()+s.getGenre());
					System.out.println();
					
						try {
							FileInputStream stream = new FileInputStream(f);
							javazoom.jl.player.Player testPlayer = new javazoom.jl.player.Player(stream);
							
							testPlayer.play();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
					
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void deactivate() {
		activated = false;
	}
	
}