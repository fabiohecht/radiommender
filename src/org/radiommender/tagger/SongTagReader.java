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
package org.radiommender.tagger;

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
import org.radiommender.model.Song;


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
		song.setGenre(tag.getFirst(FieldKey.GENRE));
		
		
		return song;
	}
}
