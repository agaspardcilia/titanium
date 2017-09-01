package screach.titanium.gui.servertab.local;

import java.util.Optional;
import javafx.event.Event;
import screach.titanium.core.server.LocalServer;
import screach.titanium.gui.ServerTabsPane;
import screach.titanium.gui.dialogs.EditServerDialog;
import screach.titanium.gui.servertab.ServerInfoPane;
import screach.titanium.gui.servertab.ServerTab;

public class LocalServerTab extends ServerTab {
	public final static int CONNECTION_ATTEMPS = 5;
	
	private LocalServer server;

	private ServerTabsPane tabs;

	private ServerInfoPane serverInfo;

	
	
	public LocalServerTab(LocalServer server, ServerTabsPane tabs) {
		super(server, tabs);
		this.server = server;
		this.setGraphic(getTitleNodde());
	}


	
	@Override
	protected void editButtonAction(Event e) {
		EditServerDialog dial = new EditServerDialog(server);
		
		Optional<LocalServer> result = dial.showAndWait();
		
		if (result.isPresent()) {
			server.changeInformations(result.get());
			serverInfo.refreshAll();
			this.setText(server.getName());
			tabs.writeServerList();
		}
		
	}
	
	
}
