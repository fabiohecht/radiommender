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
package org.radiommender.recommender.searchterm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.radiommender.model.Rating;
import org.radiommender.model.RecommenderMap;
import org.radiommender.model.Song;
import org.radiommender.model.SongTag;
import org.radiommender.utils.CountingBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Provides functionality to handle incoming requests for SearchTerm Maps.
 * Prunes the map such that there are only the top n entries in it when sent back to save bandwidth.
 * 
 * @author Robert Erdin
 * @mail robert.erdin@gmail.com
 *
 */
public class SearchTermProvider {
	
	private final static int MIN_AMOUNT_OF_SONGS = 3;
	
	private int pruningSize;
	Logger logger = LoggerFactory.getLogger(SearchTermProvider.class);
	
	/**
	 * Custom pruning size.
	 * @param pruningSize Maximum size of the RecommenderMap
	 */
	public SearchTermProvider(int pruningSize){
		this.pruningSize = pruningSize;
	}
	
	/**
	 * Default pruning size.
	 */
	public SearchTermProvider(){
		this.pruningSize = 5;
	}
	
	/**
	 * 
	 * @param blockedSongs Songs which the requesting peer does not want in the result.
	 * @return pruned RecommenderMap<Object, Rating>
	 */
	public RecommenderMap<Object, Rating> pruneSearchTermMap(RecommenderMap<Object, Rating> recommenderMap, CountingBloomFilter<Object> filter){
		
		RecommenderMap<Object, Rating> result = new RecommenderMap<Object, Rating>();
		
		// create ArrayList for easy sorting
		ArrayList<Entry<Object, Rating>> sortedEntntries = new ArrayList<Map.Entry<Object,Rating>>();
		
		// add all Map entries to the ArrayList
		sortedEntntries.addAll(recommenderMap.entrySet());
		
		// sort...
		Collections.sort(sortedEntntries, new Comparator<Entry<Object, Rating>>() {
			@Override
			public int compare(Entry<Object, Rating> o1, Entry<Object, Rating> o2) {
				return o1.getValue().compareToGlobal(o2.getValue());
			}
			
		});
		
		Collections.reverse(sortedEntntries); // make it descending...		
		
		//check if a Set(Bloom Filter) was prvided, if not create empty dummy HashSet
		if(filter == null){
			filter = new CountingBloomFilter<Object>(1, new int[10]);
		}
		
		
		boolean songFlag = false; // used to track whether there is a song in the map.
		
		// fill the result with the according SongTags and Songs.
		int songCounter = 0;
		for(int i = 0; i<this.pruningSize && !sortedEntntries.isEmpty();){
			if (!sortedEntntries.isEmpty()) {
				if(sortedEntntries.get(0).getKey() instanceof SongTag  && !filter.contains(sortedEntntries.get(0).getKey()) && ((Rating)sortedEntntries.get(0).getValue()).getRating() > 0){
					result.put(sortedEntntries.get(0).getKey(), sortedEntntries.get(0).getValue());
					sortedEntntries.remove(0);
					i++;
				}
				else if(sortedEntntries.get(0).getKey() instanceof Song && !filter.contains((Song)sortedEntntries.get(0).getKey()) && ((Rating)sortedEntntries.get(0).getValue()).getRating() > 0){
					result.put(sortedEntntries.get(0).getKey(), sortedEntntries.get(0).getValue());
					sortedEntntries.remove(0);
					i++;
					songFlag = true;
					songCounter++;
				}
				else{
					sortedEntntries.remove(0);
				}
			}
		}
		
		// make sure at least one Song object is contained in the map.
		if (!songFlag && sortedEntntries.size()>0 && songCounter < MIN_AMOUNT_OF_SONGS){
			for(int counter = 0; counter < sortedEntntries.size() || songCounter< MIN_AMOUNT_OF_SONGS; counter++){
				if(sortedEntntries.get(counter).getKey() instanceof Song){
					result.put(sortedEntntries.get(0).getKey(), sortedEntntries.get(0).getValue());
					songFlag = true;
					songCounter++;
				}
			}
		}
		
		return result;
	}
	

}
