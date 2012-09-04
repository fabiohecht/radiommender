package p2pct.model;

public class AffinityEntry {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(affinity);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		AffinityEntry other = (AffinityEntry) obj;
		if (Float.floatToIntBits(affinity) != Float.floatToIntBits(other.affinity))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	private String user;
	private float affinity;
	
	public AffinityEntry(String user, Float affinity) {
		this.user = user;
		this.affinity = affinity;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	public float getAffinity() {
		return affinity;
	}
	public void setAffinity(float affinity) {
		this.affinity = affinity;
	}
	public String toString(){
		return affinity +  " --> " + user;
	}
}
