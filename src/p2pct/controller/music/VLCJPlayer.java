package p2pct.controller.music;

import java.util.Set;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;


public class VLCJPlayer 
{
	final private static Logger logger = LoggerFactory.getLogger(VLCJPlayer.class);

	private static final float MAX_VOLUME = 200;

	private final Object lock = new Object();
	private MediaPlayerFactory mpf;
	private EmbeddedMediaPlayer mediaPlayer;
	
	public VLCJPlayer(final Set<String> pathToVlcLibs)  {
		try {
			NativeDiscovery nativeDiscovery = new NativeDiscovery();
	
			if (!nativeDiscovery.discover() && pathToVlcLibs!=null) {
				for (String path : pathToVlcLibs) {
					NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
				}
			    Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
			}
			
			this.mpf = new MediaPlayerFactory();
		}
		catch (RuntimeException e) {
			//error initializing player
			String message = "Error initializing VLCJ Player";
			logger.error(message);
			throw new RuntimeException(e);
		}
	}
	
	public void open(String fileName)
	{
		synchronized (lock)
		{	
			this.mediaPlayer = mpf.newEmbeddedMediaPlayer();

			logger.debug("created mpf v." + this.mpf.version());
			
			this.mediaPlayer.prepareMedia(fileName);
		}
		
	}

	

	public void play()
	{
		synchronized (lock)
		{
			if (mediaPlayer != null){

				mediaPlayer.play();
				
				logger.debug("vlcj player - play");
			}
			else
				throw new RuntimeException("media player is not initialazed");
		}
	}
	public void shutdown()
	{
		synchronized (lock)
		{
			if (this.mediaPlayer != null)
			{
				this.mediaPlayer.stop();
				this.mediaPlayer.release();
			}
		}
	}

	public void pause()
	{
		synchronized (lock)
		{
			if (mediaPlayer != null){
				mediaPlayer.pause();

				logger.debug("vlcj player - pause");
			}
		}
	}


	public void stop()
	{
		synchronized (lock)
		{
			if (mediaPlayer != null){
				mediaPlayer.stop();

				logger.debug("vlcj player - stop");
			}
		}
	}
	
	public float getVolumePercent() {
		if (logger.isDebugEnabled()) {
			logger.debug("returning volume "+this.mediaPlayer.getVolume());
		}
		return this.mediaPlayer.getVolume()*100/MAX_VOLUME;
	}
	
	public void setVolumeUp(int amountPercent){
		int oldvolume = mediaPlayer.getVolume();
		mediaPlayer.setVolume((int) (oldvolume + amountPercent*MAX_VOLUME/100));
	}
	
	public void setVolumeDown(int amountPercent){
		int oldvolume = mediaPlayer.getVolume();
		mediaPlayer.setVolume((int) (oldvolume - amountPercent*MAX_VOLUME/100));
	}

	public void setVolumePercent(float volumePercent) {
		mediaPlayer.setVolume((int) (volumePercent*MAX_VOLUME/100));
	}

	public boolean isPlaying() {
		if (this.mediaPlayer==null) {
			return false;
		}
		return this.mediaPlayer.isPlaying();
	}
	
}
