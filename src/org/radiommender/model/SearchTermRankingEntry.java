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

public class SearchTermRankingEntry {
	private String searchTerm;
	private Song song;
	private float rating;
	
	public SearchTermRankingEntry(String searchTerm, Song song, float rating) {
		this.searchTerm = searchTerm;
		this.song = song;
		this.rating = rating;
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
	}
	
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	
}
