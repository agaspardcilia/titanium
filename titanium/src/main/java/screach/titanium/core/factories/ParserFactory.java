package screach.titanium.core.factories;

import screach.titanium.core.AnswerParser;
import screach.titanium.core.Server;
import screach.titanium.core.cmdparser.ListPlayerCmd;
import screach.titanium.core.cmdparser.MapInfoCmd;
import utils.rcon.RconAnswerReceiver;

public class ParserFactory {
	public static AnswerParser newAnswerParser(RconAnswerReceiver receiver, Server server) {
		AnswerParser result = new AnswerParser(receiver, server);
		
		result.getHandledCommands().add(new ListPlayerCmd(server));
		result.getHandledCommands().add(new MapInfoCmd(server));
		
		return result;
	}
}
