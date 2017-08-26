package screach.titanium.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import screach.titanium.core.ConnectionFailureException;
import screach.titanium.core.NotifyEventType;
import screach.titanium.core.Player;
import screach.titanium.core.ServerInformationRefresher;
import screach.titanium.core.cmdparser.AnswerParser;
import screach.titanium.core.factories.ParserFactory;
import utils.rcon.RconAnswerReceiver;
import utils.rcon.RconClient;


// TODO notify observers
public class LocalServer extends Server {
	private String password;

	// Network
	private RconClient rcon;
	private Thread receiverThread;
	private RconAnswerReceiver receiver;
	private Thread parserThread;
	private AnswerParser parser;
	private Thread refresherThread;
	private ServerInformationRefresher refresher;


	public LocalServer(String name, String address, int port, String password) {
		super(name, address, port);
		this.password = password;
	}

	@Override
	public void connect() throws Exception {
		rcon = new RconClient(getAddress(), getPort());
		try {
			rcon.connect();
			rcon.authenticate(password);
		} catch (NegativeArraySizeException e) { // Happens when the host is unreachable.
			throw new ConnectionFailureException();
		}

		if (rcon.connected()) {
			System.out.println("Connection to " + getAddress() + ":" + getPort()+ " : OK");
		} else {
			System.out.println("Can't connect to " + getAddress());
		}

		receiver = new RconAnswerReceiver(rcon);
		receiverThread = new Thread(receiver);

		parser = ParserFactory.newAnswerParser(receiver, this);
		parserThread = new Thread(parser);

		refresher = new ServerInformationRefresher(this);
		refresherThread = new Thread(refresher);

		receiverThread.start();
		parserThread.start();
		refresherThread.start();

	}

	@Override
	public void disconnect() {
		if (rcon.connected()) {
			try {
				rcon.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				receiverThread.interrupt();
				parserThread.interrupt();
				refresherThread.interrupt();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void executeCommand(String command) {
		System.out.println("executing : " + command);

		try {
			rcon.executeCommand(command);
		} catch (IOException e) {
			System.out.println("Execution failed : " + e.getMessage());
		}
	}
	
	@Override
	public void refreshConnectedPlayersList(ArrayList<Player> players) {
		// Add new players
		players.forEach(p -> {
			if (!connectedPlayers.contains(p)) {
				connectedPlayers.add(p);
				setChanged();
			} 
		});

		// Remove disconnected players
		connectedPlayers.retainAll(players);

		notifyObservers(NotifyEventType.PLAYER_LIST);
	}

	@Override
	public void refreshRecentlyDCPlayers(ArrayList<Player> players) {
		// TODO make it better

		recentlyDCPlayers.clear();
		recentlyDCPlayers.addAll(players);
		setChanged();
		notifyObservers(NotifyEventType.PLAYER_LIST);
	}







	public String getPassword() {
		return password;
	}


	public RconClient getRcon() {
		return rcon;
	}

	@Override
	public List<Player> getConnectedPlayers() {
		return connectedPlayers;
	}

	@Override
	public List<Player> getRecentlyDCPlayers() {
		return recentlyDCPlayers;
	}

	@Override
	public boolean isConnected() {
		return rcon != null && rcon.connected();
	}

	@Override
	public long getPing() {
		return ping;
	}

	@Override
	public int getMaxCapacity() {
		return maxCapacity;
	}

	@Override
	public void log(String log) {
		consoleLogs.add(log);
		setChanged();
		notifyObservers(NotifyEventType.CONSOLE_LOG);
	}

	@Override
	public ArrayList<String>getConsoleLog() {
		return consoleLogs;
	}
	
	@Override
	public String getLastLog() {
		return consoleLogs.get(consoleLogs.size()-1);
	}

	@Override
	public synchronized String getCurrentMap() {
		return currentMap;
	}
	
	@Override
	public synchronized String getNextMap() {
		return nextMap;
	}
	
	@Override
	public synchronized void setNextMap(String nextMap) {
		if (!this.nextMap.equals(nextMap)) {
			this.nextMap = nextMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	@Override
	public synchronized void setCurrentMap(String currentMap) {
		if (!this.currentMap.equals(currentMap)) {
			this.currentMap = currentMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	@Override
	public void setPing(long ping) {
		this.ping = ping;
		setChanged();
		notifyObservers(NotifyEventType.PING);
	}
	@Override
	public void changeInformations(Server newInformations) {
		changeInformations(newInformations);
		
		if (newInformations instanceof LocalServer)
			password = ((LocalServer)newInformations).getPassword();
	}
	
	@Override
	public String toString() {
		return getName() + " " + getAddress() + ":" + getPort();
	}
	
		
}
