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

public class SongVoteEntry {
	private Song song;
	private String searchTerm;
	private boolean like;
	
	public SongVoteEntry(Song song, String searchTerm, boolean like) {
		this.song = song;
		this.searchTerm = searchTerm;
		this.like = like;
	}
	
	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
	}
	
	public boolean isLike() {
		return like;
	}
	public void setLike(boolean like) {
		this.like = like;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}

}
