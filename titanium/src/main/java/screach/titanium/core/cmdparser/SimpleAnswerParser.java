package screach.titanium.core.cmdparser;

import java.util.ArrayList;

import screach.titanium.core.server.Server;

public class SimpleAnswerParser {
	protected ArrayList<CommandParser> handledCommands;
	protected Server server;
	
	
	public SimpleAnswerParser(Server server) {
		this.server = server;
		handledCommands = new ArrayList<>();
	}
	
	public void parseAnswer(String answer) {
		for (CommandParser cmdp : handledCommands) {
			if (cmdp.match(answer)) {
				cmdp.parseCommand(answer);
				return;
			}
		}
		
		server.log(answer);
	}
	
	public ArrayList<CommandParser> getHandledCommands() {
		return handledCommands;
	}
}
