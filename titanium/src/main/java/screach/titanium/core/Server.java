package screach.titanium.core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import screach.titanium.core.factories.ParserFactory;
import utils.rcon.RconAnswerReceiver;
import utils.rcon.RconClient;


// TODO notify observers
public class Server extends Observable {
	private String name;
	private String address;
	private int port;
	private String password;

	// Network
	private RconClient rcon;
	private Thread receiverThread;
	private RconAnswerReceiver receiver;
	private Thread parserThread;
	private AnswerParser parser;
	private Thread refresherThread;
	private ServerInformationRefresher refresher;

	private List<Player> connectedPlayers;
	private List<Player> recentlyDCPlayers;

	private ArrayList<String> consoleLogs;

	// Stats
	private int maxCapacity;
	private long ping;
	
	private String currentMap;
	private String nextMap;
	
	public Server(String name, String address, int port, String password) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.password = password;

		ping = -1;
		maxCapacity = -1;
		
		currentMap = "no data";
		nextMap = "no data";
		
		consoleLogs = new ArrayList<>();

		connectedPlayers = new ArrayList<>();
		recentlyDCPlayers = new ArrayList<>();
	}




	public void connect() throws IllegalStateException, IOException, ConnectionFailureException, UnknownHostException {
		rcon = new RconClient(address, port);
		try {
			rcon.connect();
			rcon.authenticate(password);
		} catch (NegativeArraySizeException e) { // Happens when the host is unreachable.
			throw new ConnectionFailureException();
		}

		if (rcon.connected()) {
			System.out.println("Connection to " + address + ":" + port + " : OK");
		} else {
			System.out.println("Can't connect to " + address);
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

	public void executeCommand(String command) {
		System.out.println("executing : " + command);

		try {
			rcon.executeCommand(command);
		} catch (IOException e) {
			System.out.println("Execution failed : " + e.getMessage());
		}
	}
	
	public void executeCommand(String command, boolean log) {
		executeCommand(command);
		
		if (log)
			log(command);
	}


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

	public void refreshRecentlyDCPlayers(ArrayList<Player> players) {
		// TODO make it better

		recentlyDCPlayers.clear();
		recentlyDCPlayers.addAll(players);
		setChanged();
		notifyObservers(NotifyEventType.PLAYER_LIST);
	}


	public void playerList() {
		executeCommand("ListPLayers");
	}

	// AdminKick "<NameOrSteamId>" <KickReason> (Kicks a player from the server)
	public void kickPlayer(Player p, String reason) {
		executeCommand("AdminKick \"" + p.getSteamId() + "\" " + reason, true);
	}

	// AdminBan "<NameOrSteamId>" "<BanLength>" <BanReason> (Bans a player from the server for a length of time. 0 = Perm, 1d = 1 Day, 1m = 1 Month, etc)
	public void banPlayer(Player p, String duration, String reason) {
		executeCommand("AdminKick \"" + p.getSteamId() + "\" \"" + duration + "\" " + reason, true);
	}

	public void enableAllKits() {
		executeCommand("AdminAllKitsAvailable true", true);
	}

	public void disableAllKits() {
		executeCommand("AdminAllKitsAvailable false", true);
	}

	public void unlockVehicules() {
		executeCommand("AdminDisableVehicleClaiming true", true);
	}
	
	public void lockVehicules() {
		executeCommand("AdminDisableVehicleClaiming false", true);
	}
	
	public void killServer() {
		// TODO
	}

	public void pauseMatch() {
		// TODO
	}

	public void restardMatch() {
		// TODO
	}

	public void setNextMapCmd(String map) {
		executeCommand("AdminSetNextMap \"" + map + "\"", true);
	}

	public void setSlomo(int clockSpeed) {
		executeCommand("AdminSlomo " + clockSpeed, true);
	}

	public void resetSlomo() {
		setSlomo(1);
	}

	public void stats() {
		// TODO - looks like it's not implemented yet or has been abandoned.
	}

	public void endMatch() {
		// TODO
	}
	
	public void showNextMap() {
		executeCommand("ShowNextMap");
	}
	
	public void broadcast(String content) {
		executeCommand("AdminBroadcast " + content);
	}



	public String getAddress() {
		return address;
	}


	public int getPort() {
		return port;
	}


	public String getPassword() {
		return password;
	}


	public RconClient getRcon() {
		return rcon;
	}

	public String getName() {
		return name;
	}

	public List<Player> getConnectedPlayers() {
		return connectedPlayers;
	}

	public List<Player> getRecentlyDCPlayers() {
		return recentlyDCPlayers;
	}

	public boolean isConnected() {
		return rcon != null && rcon.connected();
	}

	public long getPing() {
		return ping;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void log(String log) {
		consoleLogs.add(log);
		setChanged();
		notifyObservers(NotifyEventType.CONSOLE_LOG);
	}

	public ArrayList<String>getConsoleLog() {
		return consoleLogs;
	}
	
	public String getLastLog() {
		return consoleLogs.get(consoleLogs.size()-1);
	}

	public synchronized String getCurrentMap() {
		return currentMap;
	}
	
	public synchronized String getNextMap() {
		return nextMap;
	}
	
	public synchronized void setNextMap(String nextMap) {
		if (!this.nextMap.equals(nextMap)) {
			this.nextMap = nextMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	public synchronized void setCurrentMap(String currentMap) {
		if (!this.currentMap.equals(currentMap)) {
			this.currentMap = currentMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	public void setPing(long ping) {
		this.ping = ping;
		setChanged();
		notifyObservers(NotifyEventType.PING);
	}
	
	@Override
	public String toString() {
		return name + " " + address + ":" + port;
	}
}
