package screach.titanium.core.cmdparser;

import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.ServerException;
import utils.rcon.RconAnswerReceiver;

public class DisconnectCmd extends CommandParser {
	private LocalServer localServer;
	
	public DisconnectCmd(LocalServer server, boolean log) {
		super(server, log);
		localServer = server;
	}

	@Override
	public boolean match(String answer) {
		
		return answer.startsWith(RconAnswerReceiver.DC_MESSAGE);
	}

	@Override
	public void parseCommand(String answer) throws ServerException {
		ServerException e = new ServerException(answer);
		localServer.setLastServerException(e);
		server.disconnectWithError();
		throw e;
	}

}
