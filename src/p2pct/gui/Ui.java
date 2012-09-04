package p2pct.gui;

import java.util.List;

import p2pct.model.AffinityEntry;
import p2pct.model.PlayListEntry;
import p2pct.model.SearchTermRankingEntry;
import p2pct.model.Song;
import p2pct.model.SongVoteEntry;

public interface Ui {
	
	public void updatePeerId(String peerId);
	
	public void updateCurrentSong(Song song, String origin);
	public void updatePlayList(List<PlayListEntry> songs);
	
	public void updateVotes(List<SongVoteEntry> songVotes);
	public void updateSearchTermRanking(List<SearchTermRankingEntry> ratings);
	
	public void updateActualAffinity(List<AffinityEntry> affinityEntries);
	public void updateEstimatedAffinity(List<AffinityEntry> affinityEntries);
	
}
