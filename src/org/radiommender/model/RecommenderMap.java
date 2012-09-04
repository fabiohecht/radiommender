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
import java.util.HashMap;

/**
 * Wraps java.util.HashMap. Used to store Key-->Value pairs of the recommender system.
 * @author robert erdin
 * @mail robert.erdin@gmail.com
 */
public class RecommenderMap<K, V> extends HashMap<K, V> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecommenderMap() {
		super();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		for(java.util.Map.Entry<K, V> current : super.entrySet()){
			sb.append(current.getKey().toString() + "\t" + current.getValue().toString() + "\n");
		}
		
		return sb.toString();
	}

}
