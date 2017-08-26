package screach.titanium.core.factories;

import screach.titanium.core.server.LocalServer;

public class ServerFactory {
	public static LocalServer newServer(String name, String address, int port, String password) {
		return new LocalServer(name, address, port, password);
	}
}
