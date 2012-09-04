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
 * @author nicolas baer
 *
 */
public class SongTagRating implements Comparable<SongTagRating>{
	private SongTag songTag;
	private Rating rating;
	private float multiplicator;
	private String searchTermHistory = "";
	
	/**
	 * default constructor
	 */
	public SongTagRating(){
		
	}

	/**
	 * @param songTag
	 * @param rating
	 */
	public SongTagRating(SongTag songTag, Rating rating) {
		super();
		this.songTag = songTag;
		this.rating = rating;
	}
	
	

	/**
	 * @return the songTag
	 */
	public SongTag getSongTag() {
		return songTag;
	}

	/**
	 * @param songTag the songTag to set
	 */
	public void setSongTag(SongTag songTag) {
		this.songTag = songTag;
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SongTagRating o) {
		return this.getRating().compareTo(o.getRating());
	}

	/**
	 * @return the multiplicator
	 */
	public float getMultiplicator() {
		return multiplicator;
	}

	/**
	 * @param multiplicator the multiplicator to set
	 */
	public void setMultiplicator(float multiplicator) {
		this.multiplicator = multiplicator;
	}

	/**
	 * @return the searchTermHistory
	 */
	public String getSearchTermHistory() {
		return searchTermHistory;
	}

	/**
	 * @param searchTermHistory the searchTermHistory to set
	 */
	public void setSearchTermHistory(String searchTermHistory) {
		this.searchTermHistory = searchTermHistory;
	}
	
	
	
}
