package screach.titanium.core.cmdparser;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import screach.titanium.core.Player;
import screach.titanium.core.server.Server;

public class ListPlayerCmd extends CommandParser {
	private final static String TRIGGER_REGEX = "----- Active Players -----";
//	private final static String DC_REGEX = "----- Recently Disconnected Players [Max of 15] -----";
	
	

	public ListPlayerCmd(Server server) {
		super(server, false);
	}

	@Override
	public boolean match(String answer) {
		try {
			return answer.split("\n")[0].matches(TRIGGER_REGEX);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	@Override
	public void parseCommand(String answer) {
		ArrayList<String> rawConnectedPlayers = new ArrayList<>();
		ArrayList<String> rawDCPlayers = new ArrayList<>();
		boolean connectedPlayerFlag = true;
		
		String[] splittedAnswer = answer.split("\n");
		for (int i = 1; i < splittedAnswer.length; i++) {
			if (!splittedAnswer[i].startsWith("ID:")) {
				connectedPlayerFlag = false;
			} else {
				if (connectedPlayerFlag)
					rawConnectedPlayers.add(splittedAnswer[i]);
				else
					rawDCPlayers.add(splittedAnswer[i]);
			}
		}
		
		ArrayList<Player> connectedPlayers = new ArrayList<>();
		ArrayList<Player> dcPlayers = new ArrayList<>();
		
		rawConnectedPlayers.forEach(p -> {
			connectedPlayers.add(parsePlayerFromListLine(p));
		});
		
		rawDCPlayers.forEach(p -> {
			dcPlayers.add(parsePlayerFromListLine(p));
		});
		
		server.refreshConnectedPlayersList(connectedPlayers);
		server.refreshRecentlyDCPlayers(dcPlayers);
		
	}

	private Player parsePlayerFromListLine(String line) {
		int id = -1;
		String steamId = null, name = null;

		String[] splittedLine = line.split(" \\| ");

		for (String s : splittedLine) {
			// Not the best way to do it but you now, it works™
			if (s.startsWith("ID:")) {
				id = Integer.parseInt(s.substring(4));
			} else if (s.startsWith("SteamID:")) {
				steamId = s.substring(9);
			} else if (s.startsWith("Name:")) {
				name = s.substring(6);
			}
		}


		return new Player(id, steamId, name);
	}
	
}
