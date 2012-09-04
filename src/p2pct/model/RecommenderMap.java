package p2pct.model;

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
