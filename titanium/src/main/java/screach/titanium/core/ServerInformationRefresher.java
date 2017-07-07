package screach.titanium.core;

import java.io.IOException;
import java.net.InetAddress;

public class ServerInformationRefresher implements Runnable {
	private long INTERVAL = 1000;
	
	private Server server;
	
	
	public ServerInformationRefresher(Server server) {
		this.server = server;
	}
	
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				break;
			}
			
			server.playerList();
			server.showNextMap();
			updatePing();
		}
		
	}
	
	public void updatePing() {
		server.setPing(pingHost(server.getAddress()));
	}
	
	
	public long pingHost(String host){
	    Long start = System.currentTimeMillis();
	    try {
			if (!InetAddress.getByName(host).isReachable(2000)) 
				return -1;
		} catch (IOException e) {
			System.out.println("Ping failed");
			return -1;
		}
	    return System.currentTimeMillis()-start;
	}

}
