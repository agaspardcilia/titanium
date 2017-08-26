package screach.titanium.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import screach.titanium.core.server.Server;
import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.WSPServer;
import screach.titanium.gui.servertab.ServerTab;
import screach.titanium.gui.servertab.local.LocalServerTab;
import screach.titanium.gui.servertab.wsp.WSPServerTab;

public class ServerTabsPane extends TabPane {
	private MainPane mainPane;

	public ServerTabsPane(MainPane mainPane) {
		super();
		this.mainPane = mainPane;
	}

	public void refreshTabs(List<LocalServer> servers, List<WSPServer> wspServers) {
		ArrayList<ServerTab> toRemove = new ArrayList<>();
		ArrayList<Server> present = new ArrayList<>();

		this.getTabs().forEach(tab -> {
			if (tab instanceof LocalServerTab) {
				LocalServerTab st = (LocalServerTab) tab;

				if (!servers.contains(st.getServer()))
					toRemove.add(st);
				else
					present.add(st.getServer());
			} else if (tab instanceof WSPServerTab) {
				WSPServerTab st = (WSPServerTab) tab;

				if (!wspServers.contains(st.getServer()))
					toRemove.add(st);
				else
					present.add(st.getServer());
			}
		});

		this.getTabs().removeAll(toRemove);

		servers.forEach(server -> {
			if (!present.contains(server))
				addTab(server);
		});
		
		wspServers.forEach(server -> {
			if (!present.contains(server))
				addTab(server);
		});
	}

	public void addTab(LocalServer server) {
		this.getTabs().add(new LocalServerTab(server, this));
	}

	public void addTab(WSPServer server) {
		this.getTabs().add(new WSPServerTab(server, this));
	}
	
	public void addTab(Tab tab, boolean setSelected) {
		this.getTabs().add(tab);

		if (setSelected)
			this.getSelectionModel().select(tab);
	}

	public Server removeSelectedServer() {
		Tab selected = this.getSelectionModel().getSelectedItem();

		if (selected instanceof ServerTab) {
			Server result = ((ServerTab)selected).getServer();

			this.getTabs().remove(selected);

			return result;
		} else {
			return null;
		}
	}

	public void connectToAll() {
		this.getTabs().forEach(tab -> {
			if (tab instanceof LocalServerTab) {
				LocalServerTab st = (LocalServerTab) tab;

				try {
					st.connect();
				} catch (Exception e) {
					new Alert(AlertType.ERROR, "Connection to \"" + st.getServer() + "\" has failed (" + e.getMessage() + ")").show();
					e.printStackTrace();
				}
			}
		});
	}

	public void disconnectFromAll() {
		this.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {
				ServerTab st = (ServerTab) tab;

				if (st.getServer().isConnected()) {
					st.getServer().disconnect();
					st.switchToDisconnected();
				}
			}
		});
	}

	public Application getApplication() {
		return mainPane.getApp();
	}

	public void writeServerList() {
		mainPane.writeServerList();
	}
}
