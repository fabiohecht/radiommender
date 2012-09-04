/**
 * 
 */
package p2pct.controller.music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p2pct.controller.Player;
import p2pct.controller.SongHandler;
import p2pct.controller.songhandler.MusicStorageWatcher;
import p2pct.model.PlayListEntry;
import p2pct.model.Song;
import p2pct.model.SongFile;

/**
 * The SongPlayer loads the next song from the SongHandler and 
 * starts playing it. The BasicPlayer from javazoom is used to play songs.
 * It will update the player with the current song playing.
 * 
 * @author nicolas baer
 */
public class SongPlayer implements Runnable{
	// logger
	Logger logger = LoggerFactory.getLogger(MusicStorageWatcher.class);
	
	// module controller
	private Player player; 
	private SongHandler songHandler;
	
	// thread stopper
	private boolean active = true;
	
	private final VLCJPlayer vlcjPlayer;

    // currently playing song
    private SongFile songFile;
    
    private boolean songSkipped; 
    private boolean songStopped;
   
	
	/**
	 * default constructor
	 * @param player
	 */
	public SongPlayer(Player player){
		this.player = player;
		this.songFile = null;
		this.songSkipped = false;
		
		this.vlcjPlayer = new VLCJPlayer(null);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean start = true;
		while(this.active){
			try {
				// wait until song has finished
				int round = 0;
				while (this.vlcjPlayer.isPlaying() && !start && !this.songStopped) {
					Thread.sleep(1000);
					round++;
					if(round == 25){
						this.vlcjPlayer.pause();
						this.vlcjPlayer.stop();
					}
				}
				
				// check if song was skipped
				if(!start && !this.songSkipped && !this.songStopped){
					player.songPlayed(this.songFile.getSong());
				}
				
				while(this.songStopped){
					Thread.sleep(1000);
				}
				
				start = false;
				
				// fetch next song
				this.songSkipped = false;
				this.songStopped = false;
				PlayListEntry playlistEntry = null;
				while(active && (playlistEntry = songHandler.getRdySong()) == null){
					Thread.sleep(50);
				}
				if (playlistEntry!=null) {
					songFile = playlistEntry.getSongFile();
					
					// open song
					this.vlcjPlayer.open(songFile.getFile().getCanonicalPath());
					
					// play song
					this.vlcjPlayer.play();
					this.player.songPlaying(songFile.getSong(), playlistEntry.getOrigin());				
	
					int counter = 0;
					while((!this.vlcjPlayer.isPlaying() && counter < 20) && !this.songSkipped && !this.songStopped){
						counter++;
						Thread.sleep(1000);
					}
				}				
			} catch (Exception e) {
				logger.error("couldnt play song, message: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Stops the current song and forces to play the next one in the queue.
	 * @return the current song playing
	 * @throws BasicPlayerException couldn't stop the current track
	 */
	public Song skipSong() {
		this.songSkipped = true;
		this.songStopped = false;
		this.vlcjPlayer.stop();
		
		if (this.songFile!=null) {
			return this.songFile.getSong();
		}
		else {
			return null;
		}
	}
		
	/**
	 * Stops the current song. This will automatically force the player to play the next song.
	 * @throws BasicPlayerException
	 */
	public void stop() {
		this.vlcjPlayer.stop();
		this.songStopped = true;
	}
	/**
	 * Starts the player.
	 */
	public void play(){
		this.songStopped = false;
	}
	
	/**
	 * Register the SongHandler. This needs to be done in order to update the gui.
	 * @param songHandler the songHandler to set
	 */
	public void setSongHandler(SongHandler songHandler) {
		this.songHandler = songHandler;
	}


	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the songFile
	 */
	public SongFile getSongFile() {
		return songFile;
	}


	/**
	 * @param songFile the songFile to set
	 */
	public void setSongFile(SongFile songFile) {
		this.songFile = songFile;
	}

}
