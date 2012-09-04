/**
 * 
 */
package p2pct.tests.overlay;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import p2pct.controller.songhandler.SongTagReader;
import p2pct.model.Song;

/**
 * @author nicolas baer
 *
 */
public class TSongTagReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Song song = new SongTagReader(new File("/Users/nicolasbar/Desktop/music/Waldeck/Fallen Angel.mp3")).getSongTags();
			
			System.out.println(song.getArtist() + " - " + song.getTitle() + " - " + song.getAlbum() + " - " + song.getGenre());
			System.out.println(song.hashCode());
			
			Song song1 = new SongTagReader(new File("/Users/nicolasbar/Desktop/music/Waldeck/Fallen Angel.mp3")).getSongTags();
			System.out.println(song1.hashCode());
			
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
