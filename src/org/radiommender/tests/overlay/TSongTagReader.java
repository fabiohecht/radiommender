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
package org.radiommender.tests.overlay;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.radiommender.controller.songhandler.SongTagReader;
import org.radiommender.model.Song;


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
