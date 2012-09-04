/**
ObservableValue.java Version 1.0 
 * 
 */
package p2pct.tests.gui;
import java.util.List;
import java.util.Observable;

/**
 * @author Marium Zeeshan
 *
 */
public class ObservableValue extends Observable{
    
    private String Song;
    
    /**
     * @param genre
     * @param Album
     * @param Artist
     */
    public ObservableValue(String Song)
    {
       this.Song=Song;
       //addObserver(new TextObserver);
       /*this.genre=genre;
       this.Album = Album;
       this.Artist=Artist;
       */
    }
    public String getSong(){
	return this.Song;
    }
    
    public void setSong(String Song) {
        this.Song = Song;
        //logger.debug("Set Song");
        setChanged();
        notifyObservers();
    }


  }
