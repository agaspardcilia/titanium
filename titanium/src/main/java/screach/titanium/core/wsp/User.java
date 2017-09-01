package screach.titanium.core.wsp;

import org.json.JSONObject;

public class User {
	private int id;
	private String username;
	
	public User(int id, String discordId, String username) {
		this.id = id;
		this.username = username;
	}
	
	public User(JSONObject jsonUser) {
		this.id = jsonUser.getInt(WebServiceProvider.API_ARG_ID);
		this.username = jsonUser.getString(WebServiceProvider.API_ARG_USERNAME);
	}
	
	public int getId() {
		return id;
	}
	
	
	public String getUsername() {
		return username;
	}
}
