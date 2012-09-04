package p2pct.utils;

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
