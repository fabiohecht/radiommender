/**
 * 
 */
package p2pct.model;

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
