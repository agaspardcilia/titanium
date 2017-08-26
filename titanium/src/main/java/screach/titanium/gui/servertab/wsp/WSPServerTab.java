package screach.titanium.gui.servertab.wsp;

import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import screach.titanium.core.server.WSPServer;
import screach.titanium.gui.ServerTabsPane;
import screach.titanium.gui.servertab.ServerTab;

public class WSPServerTab extends ServerTab {

	public WSPServerTab(WSPServer server, ServerTabsPane tabs) {
		super(server, tabs);
		System.out.println("Server tab created.");
	}

	@Override
	protected void editButtonAction(Event e) {
		new Alert(AlertType.INFORMATION, "Not implemented yet.");
	}

}
