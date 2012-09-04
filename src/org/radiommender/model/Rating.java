/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.model;


import java.io.Serializable;

import org.radiommender.utils.ConfigurationFactory;


/**
 * Helper class to provide the rating of either a song (p2pct.model.Song) or a
 * search tag (p2pct.model.SongTag). This leads to the information being stored
 * persistent within the DHT but the actual rating is computed individually for
 * each peer based on the peers preferences regarding the weighting of a song
 * being played and a song being skipped.
 * 
 * Rating is between -1 and 1.
 * 
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 * 
 */
public class Rating implements Comparable<Rating>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int plays;
	private int skips;
	private float localRating;
	
	float playWeight;
    float skipWeight;
    int ratingThreshold;

	/**
	 * Default constructor, will be initialized without any votes.
	 */
	public Rating() {
		this.plays = 0;
		this.skips = 0;
		
		
        try {
            // get the weights from the configuration
            //playWeight = new Float(ConfigurationFactory.getProperty("recommender.rating.playvalue"));
            //skipWeight = new Float(ConfigurationFactory.getProperty("recommender.rating.skipvalue"));
            playWeight = 1;
            skipWeight = 1;
            ratingThreshold = new Integer(ConfigurationFactory.getProperty("recommender.rating.ratingthreshold"));
        } catch (Error e) {
            // initialize with default value
            e.printStackTrace();
            playWeight = 1;
            skipWeight = 2;
            ratingThreshold = 5;
        }
	}

	/**
	 * Get rating based on information from DHT and local information on how to
	 * weight it.
	 * 
	 * @return rating
	 */
	public float getRating() {

		

		// calculate the rating:
		// to prevent the rating from being 1 after one positive vote the full
		// range will be enabled after
		// certain amount of votes.
		float rating;
		rating = (playWeight * this.plays) / (playWeight * this.plays + skipWeight * this.skips);

		if ((this.plays + this.skips) < ratingThreshold) {
			rating = (float) Math.min(rating, 0.75);
		}

		return rating;
	}

	/**
	 * Vote up!
	 */
	public void voteUp() {
		this.plays++;
	}

	/**
	 * Vote down!
	 */
	public void voteDown() {
		this.skips++;
	}
	
	/**
	 * Set local rating
	 */
	public void setLocalRating(float rating){
		this.localRating = rating;
	}
	
	/**
	 * Get local rating
	 */
	public float getLocalRating(){
		return this.localRating;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Rating o) {
		if(this.localRating > o.getLocalRating()){
			return 1;
		} else{
			if(this.localRating < o.getLocalRating()){
				return -1;
			} else{
				return 0;
			}
		}
	}
	
	public int compareToGlobal(Rating o){
		if(this.getRating() > o.getRating()){
			return 1;
		} else{
			if(this.getRating() < o.getRating()){
				return -1;
			} else{
				return 0;
			}
		}
	}
	
	public String toString(){
		return "DHT Rating: " + this.getRating() + ". Local Rating: " + this.getLocalRating(); 
	}

}
