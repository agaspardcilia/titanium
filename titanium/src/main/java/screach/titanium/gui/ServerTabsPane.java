package screach.titanium.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import screach.titanium.core.ConnectionFailureException;
import screach.titanium.core.Server;
import screach.titanium.gui.servertab.ServerTab;

public class ServerTabsPane extends TabPane {
	private MainPane mainPane;
	
	public ServerTabsPane(MainPane mainPane) {
		super();
		this.mainPane = mainPane;
	}

	public void refreshTabs(List<Server> servers) {
		ArrayList<ServerTab> toRemove = new ArrayList<>();
		ArrayList<Server> present = new ArrayList<>();
		
		this.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {
				ServerTab st = (ServerTab) tab;
				
				if (!servers.contains(st.getServer()))
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
	}
	
	public void addTab(Server server) {
		this.getTabs().add(new ServerTab(server, this));
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
			if (tab instanceof ServerTab) {
				ServerTab st = (ServerTab) tab;
				
				try {
					st.connect();
				} catch (IllegalStateException | IOException | ConnectionFailureException e) {
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
