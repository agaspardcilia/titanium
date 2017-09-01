package screach.titanium.gui.servertab.wsp;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import screach.titanium.core.server.WSPServer;
import screach.titanium.gui.ServerTabsPane;
import screach.titanium.gui.servertab.ServerTab;
import utils.AssetsLoader;

public class WSPServerTab extends ServerTab {
	private WSPServer wspServer;
	
	public WSPServerTab(WSPServer server, ServerTabsPane tabs) {
		super(server, tabs);
		this.wspServer = server;
		this.setGraphic(getTitleNodde());
		System.out.println("Server tab created.");
	}

	@Override
	protected void editButtonAction(Event e) {
		new Alert(AlertType.INFORMATION, "Not implemented yet.").show();;
	}

	@Override
	protected Node getTitleNodde() {
		GridPane result = new GridPane();
		
		result.setHgap(5);
		
		result.addRow(0, AssetsLoader.getIcon("wsp_icon.png"), getConnectionStatusIcon(),
				new Label(wspServer.getWSPServerName() + " : " + wspServer.getName()));
		
		return result;
	}
	
	

}
