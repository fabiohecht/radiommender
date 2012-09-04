/**
 * 
 */
package p2pct.controller.songhandler;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import p2pct.model.Song;

/**
 * Reads the tags of a song. It currently uses the JAudioTagger library.
 * 
 * @author nicolas baer
 */
public class SongTagReader {

	private File songFile;
	
	/**
	 * default constructor
	 * 
	 * @param songFile
	 */
	public SongTagReader(File songFile){
		this.songFile = songFile;
	}
	
	/**
	 * Fetches all the tags from the local music file:
	 * - Artist
	 * - Album
	 * - Title
	 * - Genre
	 * @return Song filled with tags
	 * @throws InvalidAudioFrameException 
	 * @throws ReadOnlyFileException 
	 * @throws CannotReadException 
	 * @throws TagException 
	 * @throws IOException 
	 */
	public Song getSongTags() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException{
		// create song
		Song song = new Song();

		// instantiate audio file
		AudioFile audio = AudioFileIO.read(this.songFile);
		
		Tag tag = audio.getTag();
		song.setArtist(tag.getFirst(FieldKey.ARTIST));
		song.setAlbum(tag.getFirst(FieldKey.ALBUM));
		song.setTitle(tag.getFirst(FieldKey.TITLE));
		
		// FIXME
		String genre = tag.getFirst(FieldKey.GENRE);
		if(genre.equals("17")){
			song.setGenre("rock");
		} else{
			song.setGenre(tag.getFirst(FieldKey.GENRE));
		}
		
		return song;
	}
}
