package p2pct.model;

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
