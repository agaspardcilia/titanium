package screach.titanium.core.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import screach.titanium.core.NotifyEventType;
import screach.titanium.core.Player;
import utils.PlayerUtils;

public abstract class Server extends Observable {
	private final static int POOL_SIZE = 4;
	private final static int VAC_TRIES = 5;
	
	private String name;
	private String address;
	private int port;
	
	protected List<Player> connectedPlayers;
	protected List<Player> recentlyDCPlayers;

	protected ArrayList<String> consoleLogs;

	// Stats
	protected int maxCapacity;
	protected long ping;

	protected String currentMap;
	protected String nextMap;

	private ExecutorService pool; 
	
	public Server(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
		
		ping = -1;
		maxCapacity = -1;

		currentMap = "no data";
		nextMap = "no data";

		consoleLogs = new ArrayList<>();

		connectedPlayers = new ArrayList<>();
		recentlyDCPlayers = new ArrayList<>();
		
		pool = Executors.newFixedThreadPool(POOL_SIZE);
	}
	
	public Server() {
		this("no data", "no data", -1);
	}

	public abstract void connect() throws Exception; 
	
	public abstract void disconnect();

	public abstract boolean isConnected();
	
	public abstract void executeCommand(String command) throws RCONServerException;

	public void executeCommand(String command, boolean log) throws RCONServerException {
		executeCommand(command);

		if (log)
			log(command);
	}


	public void refreshConnectedPlayersList(ArrayList<Player> players) {
		// Add new players
		players.forEach(p -> {
			if (!connectedPlayers.contains(p)) {
				connectedPlayers.add(p);
				pool.submit(() -> {
					PlayerUtils.updateVACBanStatus(p, VAC_TRIES);
					setChanged();
					notifyObservers();
				});
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
	
	
	public void playerList() throws RCONServerException {
		executeCommand("ListPLayers");
	}

	// AdminKick "<NameOrSteamId>" <KickReason> (Kicks a player from the server)
	public void kickPlayer(Player p, String reason) throws RCONServerException {
		executeCommand("AdminKick \"" + p.getSteamId() + "\" " + reason, true);
	}

	// AdminBan "<NameOrSteamId>" "<BanLength>" <BanReason> (Bans a player from the server for a length of time. 0 = Perm, 1d = 1 Day, 1m = 1 Month, etc)
	public void banPlayer(Player p, String duration, String reason) throws RCONServerException {
		executeCommand("AdminBan \"" + p.getSteamId() + "\" \"" + duration + "\" " + reason, true);
	}

	public void enableAllKits() throws RCONServerException {
		executeCommand("AdminAllKitsAvailable true", true);
	}

	public void disableAllKits() throws RCONServerException {
		executeCommand("AdminAllKitsAvailable false", true);
	}

	public void unlockVehicules() throws RCONServerException {
		executeCommand("AdminDisableVehicleClaiming true", true);
	}

	public void lockVehicules() throws RCONServerException {
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

	public void setNextMapCmd(String map) throws RCONServerException {
		executeCommand("AdminSetNextMap " + map, true);
	}

	public void changeMap(String map) throws RCONServerException {
		executeCommand("AdminChangeMap " + map, true);
	}

	public void setSlomo(int clockSpeed) throws RCONServerException {
		executeCommand("AdminSlomo " + clockSpeed, true);
	}

	public void resetSlomo() throws RCONServerException {
		setSlomo(1);
	}

	public void stats() {
		// TODO - looks like it's not implemented yet or has been abandoned.
	}

	public void endMatch() {
		// TODO
	}

	public void showNextMap() throws RCONServerException {
		executeCommand("ShowNextMap");
	}

	public void broadcast(String content) throws RCONServerException {
		executeCommand("AdminBroadcast " + content);
	}

	public List<Player> getConnectedPlayers() {
		return connectedPlayers;
	}

	public List<Player> getRecentlyDCPlayers() {
		return recentlyDCPlayers;
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
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getName() {
		return name;
	}
	
	public void changeInformations(Server newInformations) {
		name = newInformations.getName();
		address = newInformations.getAddress();
		port = newInformations.getPort();
	}
	
	public void logError(RCONServerException e) {
		log("Error : " + e.getMessage());
	}
	
}

