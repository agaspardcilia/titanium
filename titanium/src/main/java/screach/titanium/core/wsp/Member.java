package screach.titanium.core.wsp;

public class Member {
	private int id;
	private String username;
	private boolean isYou;
	
	public Member(int id, String username, boolean isYou) {
		this.id = id;
		this.username = username;
		this.isYou = isYou;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isYou() {
		return isYou;
	}
	
	@Override
	public String toString() {
		return "Member[" + id + ":" + username + " " + isYou + "]" ;
	}
	
}
