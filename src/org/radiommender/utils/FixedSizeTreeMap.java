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
package org.radiommender.utils;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedSizeTreeMap<K, V> extends TreeMap<K, V> {
	
	Logger logger = LoggerFactory.getLogger(FixedSizeTreeMap.class);
	
	public FixedSizeTreeMap() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public V put(K key, V value) {
		int maxSize = new Integer(ConfigurationFactory.getProperty("recommender.affinitylist.length"));
		if (super.size() >= maxSize){
			logger.debug("cutting down FixedSizeMap" + super.pollFirstEntry()); // sorry nic :p
		}
		return super.put(key, value);
	}


}
