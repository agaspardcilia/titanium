package screach.titanium.core.factories;

import screach.titanium.core.Server;

public class ServerFactory {
	public static Server newServer(String name, String address, int port, String password) {
		return new Server(name, address, port, password);
	}
}
