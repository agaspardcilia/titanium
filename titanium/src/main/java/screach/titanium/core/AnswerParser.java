package screach.titanium.core;

import java.util.ArrayList;

import screach.titanium.core.cmdparser.CommandParser;
import utils.rcon.RconAnswerReceiver;

public class AnswerParser implements Runnable {
	private RconAnswerReceiver receiver;
	private ArrayList<CommandParser> handledCommands;
	private Server server;
	
	
	public AnswerParser(RconAnswerReceiver receiver, Server server) {
		this.receiver = receiver;
		this.server = server;
		handledCommands = new ArrayList<>();
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
	
	
	private void parseAnswer(String answer) {
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
