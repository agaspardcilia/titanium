package screach.titanium.core.cmdparser;

import java.util.ArrayList;

import screach.titanium.core.server.LocalServer;
import utils.rcon.RconAnswerReceiver;

public class AnswerParser extends SimpleAnswerParser implements Runnable {
	private RconAnswerReceiver receiver;
	
	
	public AnswerParser(RconAnswerReceiver receiver, LocalServer server) {
		super(server);
		this.receiver = receiver;
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				parseAnswer(receiver.getNextAnswer());
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		System.out.println("Answer parser has been closed.");
	}
	
	
	@Override
	public void parseAnswer(String answer) {
		for (CommandParser cmdp : handledCommands) {
			if (cmdp.match(answer)) {
				cmdp.parseCommand(answer);
				return;
			}
		}
		
		server.log(answer);
	}
	
	@Override
	public ArrayList<CommandParser> getHandledCommands() {
		return handledCommands;
	}

}
