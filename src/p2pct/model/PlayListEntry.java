package p2pct.model;

public class PlayListEntry {
	private Song song;
	private SongFile songFile;
	private String origin;
	private float rating;

	public PlayListEntry(Song song, String origin, float rating) {
		this.song = song;
		this.origin = origin;
		this.rating = rating;
	}

	public void setSongFile(SongFile songFile) {
		this.songFile = songFile;
	}
	public SongFile getSongFile() {
		return this.songFile;
	}
	public Song getSong() {
		return this.song;
	}
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "song:" + song + " origin:"+ origin+ " rating:"+rating;
	}
}
