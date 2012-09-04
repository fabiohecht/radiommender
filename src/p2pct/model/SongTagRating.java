/**
 * 
 */
package p2pct.model;

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
