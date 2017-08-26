package screach.titanium.gui.org.views;

import screach.titanium.core.server.WSPServer;

public class ServerView {
	private WSPServer server;
	
	public ServerView(WSPServer server) {
		this.server = server;
	}
	
	public String getName() {
		return server.getWSPServerName();
	}
	
	
	public String getAddress() {
		return server.getAddress() + ":" + server.getPort();
	}
	
	public WSPServer getServer() {
		return server;
	}
	
}
