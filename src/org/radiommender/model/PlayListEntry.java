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
package org.radiommender.model;

public class PlayListEntry {
	private Song song;
	private SongFile songFile;
	private String origin;
	private float rating;

	public PlayListEntry(Song song, String origin, float rating) {
		this.song = song;
		this.origin = origin;
		this.rating = rating;
	}

	public void setSongFile(SongFile songFile) {
		this.songFile = songFile;
	}
	public SongFile getSongFile() {
		return this.songFile;
	}
	public Song getSong() {
		return this.song;
	}
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "song:" + song + " origin:"+ origin+ " rating:"+rating;
	}
}
