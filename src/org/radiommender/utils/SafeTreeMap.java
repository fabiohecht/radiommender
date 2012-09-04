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

public class SafeTreeMap<Float, V> extends TreeMap<Float, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public V safePut(float key, V value){
		float id = (float) (0.000001 + (Math.random() * (0.0001 - 0.000001)));
		float newKey = key + id;
		Float test = (Float) new java.lang.Float(newKey);
		return super.put(test, value);
		
	}

}

