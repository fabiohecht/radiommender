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
 * The song tag is used to store references in the DHT.
 * @author nicolas baer
 */
public class SongTag implements Serializable {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((songTag == null) ? 0 : songTag.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongTag other = (SongTag) obj;
		if (songTag == null) {
			if (other.songTag != null)
				return false;
		} else if (!songTag.equals(other.songTag))
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String songTag;
	public SongTag(String songTag){
		this.songTag = songTag.toLowerCase();
	}
	public String getSongTag() {
		return songTag;
	}

	public void setSongTag(String songTag){
		this.songTag = songTag.toLowerCase();
	}
	
	@Override
	public String toString(){
		return "SongTag: " + this.songTag;
	}
}