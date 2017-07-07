package screach.titanium.core;

import screach.titanium.gui.servertab.PlayerView;

public class Player {
	private int id;
	private String steamId;
	private String name;
	
	public Player(int id, String steamId, String name) {
		this.id = id;
		this.steamId = steamId;
		this.name = name;
	}	
	
	public int getId() {
		return id;
	}
	
	public String getSteamId() {
		return steamId;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "Player : [id: " + id + ", steamId: " + steamId + ", name: " + name + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player p = (Player) obj;
			
			return p.getSteamId().equals(steamId);
			
		} else if (obj instanceof PlayerView) {
			PlayerView p = (PlayerView) obj;
			
			return p.getSteamId().equals(steamId);
		} else {
			return super.equals(obj);
		}
		
	}
}
