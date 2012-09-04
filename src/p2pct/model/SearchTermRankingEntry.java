package p2pct.model;

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
