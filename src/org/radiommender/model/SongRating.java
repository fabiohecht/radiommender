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

/**
 * Helper class to get all songs with rating in a list and order it afterwards.
 * 
 * @author nicolas baer
 */
public class SongRating implements Comparable<SongRating> {

	private Song song;
	private Rating rating;
	private String searchTerm;
	
	/**
	 * @param song
	 * @param rating
	 */
	public SongRating(Song song, Rating rating) {
		super();
		this.song = song;
		this.rating = rating;
	}
	
	/**
	 * default constructor
	 */
	public SongRating(){
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SongRating o) {
		return this.rating.compareTo(o.getRating());
	}

	/**
	 * @return the song
	 */
	public Song getSong() {
		return song;
	}

	/**
	 * @param song the song to set
	 */
	public void setSong(Song song) {
		this.song = song;
	}

	/**
	 * @return the rating
	 */
	public Rating getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(Rating rating) {
		this.rating = rating;
	}

	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @param searchTerm the searchTerm to set
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	
}
