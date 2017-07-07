package screach.titanium.core.cmdparser;

import screach.titanium.core.Server;

public class MapInfoCmd extends CommandParser {
	private static String CMD_BEGINNING = "Current map is";
	private static String NEXTMAP_BEGINNING = "Next map is";
	
	public MapInfoCmd(Server server) {
		super(server, false);
	}

	@Override
	public boolean match(String answer) {
		return answer.startsWith(CMD_BEGINNING);
	}

	@Override
	public void parseCommand(String answer) {
		String[] split = answer.split(", ");
		
		String crtRaw = split[0];
		String nextRaw = split[1];
		
		
		
		server.setCurrentMap(crtRaw.substring(CMD_BEGINNING.length()).trim());
		server.setNextMap(nextRaw.substring(NEXTMAP_BEGINNING.length()).trim());
		
	}
	
	
}
