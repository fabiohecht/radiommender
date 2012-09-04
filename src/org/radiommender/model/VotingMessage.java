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

import java.io.Serializable;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * @author nicolas baer
 */
public class VotingMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	private String key;
	private SongTag[] tags;
	private Song song;
	private short vote;
	
	/**
	 * default constructor
	 */
	public VotingMessage(){
		
	}
	
	

	/**
	 * @param key
	 * @param tags
	 * @param song
	 * @param vote
	 */
	public VotingMessage(String key, SongTag[] tags, Song song, short vote) {
		super();
		this.key = key;
		this.tags = tags;
		this.song = song;
		this.vote = vote;
	}



	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the tags
	 */
	public SongTag[] getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(SongTag[] tags) {
		this.tags = tags;
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
	 * @return the vote
	 */
	public short getVote() {
		return vote;
	}

	/**
	 * @param vote the vote to set
	 */
	public void setVote(short vote) {
		this.vote = vote;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (SongTag tag : this.tags) {
			sb.append(tag.toString()).append(",");
		}
		return "VotingMessage: tags ("+sb.toString()+"), key ("+this.key+"), vote ("+this.vote+"), song ("+this.song+")";
	}
	
	
}
