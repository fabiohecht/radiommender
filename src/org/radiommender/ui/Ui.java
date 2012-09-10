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
package org.radiommender.ui;

import java.util.List;

import org.radiommender.model.AffinityEntry;
import org.radiommender.model.PlayListEntry;
import org.radiommender.model.SearchTermRankingEntry;
import org.radiommender.model.Song;
import org.radiommender.model.SongVoteEntry;


public interface Ui {
	
	public void updatePeerId(String peerId);
	
	public void updateCurrentSong(Song song, String origin);
	public void updatePlayList(List<PlayListEntry> songs);
	
	public void updateVotes(List<SongVoteEntry> songVotes);
	public void updateSearchTermRanking(List<SearchTermRankingEntry> ratings);
	
	public void updateActualAffinity(List<AffinityEntry> affinityEntries);
	public void updateEstimatedAffinity(List<AffinityEntry> affinityEntries);
	
}
