package screach.titanium.core.factories;

import screach.titanium.core.cmdparser.AnswerParser;
import screach.titanium.core.cmdparser.DisconnectCmd;
import screach.titanium.core.cmdparser.ListPlayerCmd;
import screach.titanium.core.cmdparser.MapInfoCmd;
import screach.titanium.core.cmdparser.SimpleAnswerParser;
import screach.titanium.core.server.Server;
import screach.titanium.core.server.LocalServer;
import utils.rcon.RconAnswerReceiver;

public class ParserFactory {
	public static AnswerParser newAnswerParser(RconAnswerReceiver receiver, LocalServer server) {
		AnswerParser result = new AnswerParser(receiver, server);
		
		result.getHandledCommands().add(new ListPlayerCmd(server));
		result.getHandledCommands().add(new MapInfoCmd(server));
		result.getHandledCommands().add(new DisconnectCmd(server, false));
		
		return result;
	}
	
	public static SimpleAnswerParser newSimpleAnswerParser(Server server) {
		SimpleAnswerParser result = new SimpleAnswerParser(server);
		
		result.getHandledCommands().add(new ListPlayerCmd(server));
		result.getHandledCommands().add(new MapInfoCmd(server));
		
		return result;
	}
}
