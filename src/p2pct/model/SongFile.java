/**
 * 
 */
package p2pct.model;

import java.io.File;

/**
 * This class represents a 1:1 relation between file and song.
 * 
 * @author nicolas baer
 */
public class SongFile {
	private File file;
	private Song song;
	
	/**
	 * default constructor
	 */
	public SongFile(){
		
	}

	/**
	 * default constructor with all fields
	 * 
	 * @param file song file
	 * @param song song information
	 */
	public SongFile(File file, Song song) {
		super();
		this.file = file;
		this.song = song;
	}



	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((song == null) ? 0 : song.hashCode());
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
		SongFile other = (SongFile) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		return true;
	}
	
	
}
