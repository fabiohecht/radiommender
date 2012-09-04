package p2pct.model;

import java.io.Serializable;

/**
 * Song representation.
 * 
 * @author robert erdin
 * @mail robert.erdin@gmail.com
 * 
 */
@SuppressWarnings("rawtypes")
public class Song implements Cloneable, Serializable, Comparable {
	private static final long serialVersionUID = 1L;
	
	private String genre;
	private String artist;
	private String album;
	private String title;

	/**
	 * Full constructor.
	 * 
	 * @param genre
	 * @param artist
	 * @param album
	 * @param title
	 */
	public Song(String genre, String artist, String album, String title) {
		this.genre = genre;
		this.artist = artist;
		this.album = album;
		this.title = title;
	}

	/**
	 * default constructor without fields
	 */
	public Song() {
		
	}
	
	/**
	 * Key to identify the song uniquely.
	 * @return key
	 */
	public Integer getKey(){
		return this.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Song other = (Song) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	/**
	 * UNTESTED
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	


	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int compareTo(Object o) {
		
		return 0;
	}
	
	@Override
	public String toString() {
		return this.getArtist()+" - "+ this.getAlbum() +" - "+ this.getTitle() +" ("+this.getGenre()+")";
	}

}
