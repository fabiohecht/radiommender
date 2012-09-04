/**
 * 
 */
package p2pct.tests.music;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * @author nicolas baer
 *
 */
public class TestJLGui implements BasicPlayerListener
{
	  private PrintStream out = null;

	  /**
	   * Entry point.
	   * @param args filename to play.
	   */
	  public static void main(String[] args)
	  {
	    TestJLGui test = new TestJLGui();
	    test.play("/Users/nicolasbar/Desktop/music/Mad Season/Above/01 - Wake Up.mpc"); 
	    
	    
	    
	  }

	    /**
	     * Contructor.
	     */
	  public TestJLGui()
	     {
	      out = System.out;
	     }

	  public void play(String filename)
	     {
	       // Instantiate BasicPlayer.
	      BasicPlayer player = new BasicPlayer();
	      // BasicPlayer is a BasicController.
	      BasicController control = (BasicController) player;
	      // Register BasicPlayerTest to BasicPlayerListener events.
	      // It means that this object will be notified on BasicPlayer
	      // events such as : opened(...), progress(...), stateUpdated(...)
	      player.addBasicPlayerListener(this);
	     

	  try
	     { 
	      // Open file, or URL or Stream (shoutcast, icecast) to play.
	      control.open(new File(filename));

	      // control.open(new URL("http://yourshoutcastserver.com:8000"));

	      // Start playback in a thread.
	      control.play();
	      
	     

	      // If you want to pause/resume/pause the played file then
	      // write a Swing player and just call control.pause(),
	      // control.resume() or control.stop(). 
	      // Use control.seek(bytesToSkip) to seek file
	      // (i.e. fast forward and rewind). seek feature will
	      // work only if underlying JavaSound SPI implements
	      // skip(...). True for MP3SPI and SUN SPI's
	      // (WAVE, AU, AIFF).

	      // Set Volume (0 to 1.0).
	      control.setGain(0.85);
	      // Set Pan (-1.0 to 1.0).
	      control.setPan(0.0);
	    }
	    catch (BasicPlayerException e)
	    {
	      e.printStackTrace();
	    }
	  }

	  /**
	   * Open callback, stream is ready to play.
	   *
	   * properties map includes audio format dependant features such as
	   * bitrate, duration, frequency, channels, number of frames, vbr flag, ... 
	   *
	   * @param stream could be File, URL or InputStream
	   * @param properties audio stream properties.
	   */
	  public void opened(Object stream, Map properties)
	  {
	    // Pay attention to properties. It's useful to get duration, 
	    // bitrate, channels, even tag such as ID3v2.
	    display("opened : "+properties.toString()); 
	  }

	  /**
	   * Progress callback while playing.
	   * 
	   * This method is called severals time per seconds while playing.
	   * properties map includes audio format features such as
	   * instant bitrate, microseconds position, current frame number, ... 
	   * 
	   * @param bytesread from encoded stream.
	   * @param microseconds elapsed (<b>reseted after a seek !</b>).
	   * @param pcmdata PCM samples.
	   * @param properties audio stream parameters.
	  */
	  public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
	  {
	    // Pay attention to properties. It depends on underlying JavaSound SPI
	    // MP3SPI provides mp3.equalizer.
	    display("progress : "+properties.toString());
	  }

	  /**
	   * Notification callback for basicplayer events such as opened, eom ...
	   * 
	   * @param event
	   */
	  public void stateUpdated(BasicPlayerEvent event)
	  {
	    // Notification of BasicPlayer states (opened, playing, end of media, ...)
	    display("stateUpdated : "+event.toString());
	  }

	  /**
	   * A handle to the BasicPlayer, plugins may control the player through
	   * the controller (play, stop, ...)
	   * @param controller : a handle to the player
	   */ 
	  public void setController(BasicController controller)
	  {
	    display("setController : "+controller);
	  }

	  public void display(String msg)
	  {
	    if (out != null) out.println(msg);
	  }
	}