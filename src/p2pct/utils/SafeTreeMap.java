package p2pct.utils;

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

