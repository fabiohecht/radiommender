/**
 * 
 */
package p2pct.model;

import java.io.Serializable;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * @author nicolas baer
 */
public class VotingMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	private String key;
	private SongTag[] tags;
	private Song song;
	private short vote;
	
	/**
	 * default constructor
	 */
	public VotingMessage(){
		
	}
	
	

	/**
	 * @param key
	 * @param tags
	 * @param song
	 * @param vote
	 */
	public VotingMessage(String key, SongTag[] tags, Song song, short vote) {
		super();
		this.key = key;
		this.tags = tags;
		this.song = song;
		this.vote = vote;
	}



	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the tags
	 */
	public SongTag[] getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(SongTag[] tags) {
		this.tags = tags;
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
	 * @return the vote
	 */
	public short getVote() {
		return vote;
	}

	/**
	 * @param vote the vote to set
	 */
	public void setVote(short vote) {
		this.vote = vote;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (SongTag tag : this.tags) {
			sb.append(tag.toString()).append(",");
		}
		return "VotingMessage: tags ("+sb.toString()+"), key ("+this.key+"), vote ("+this.vote+"), song ("+this.song+")";
	}
	
	
}
