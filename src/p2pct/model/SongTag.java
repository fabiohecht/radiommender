package p2pct.model;

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