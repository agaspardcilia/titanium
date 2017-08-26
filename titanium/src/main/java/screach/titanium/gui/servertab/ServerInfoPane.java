package screach.titanium.gui.servertab;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import screach.titanium.core.server.Server;

public class ServerInfoPane extends GridPane {
	private Label name, capacity, ping, address, crtMap, nextMap;
	private Server server;
	
	public ServerInfoPane(Server server) {
		super();
		
		this.server = server;
		
		name = new Label("-");
		capacity = new Label("-/-");
		ping = new Label("- ms");
		address = new Label("-:-");
		crtMap = new Label(server.getCurrentMap());
		nextMap = new Label("Next : " + server.getNextMap());
		
		
		this.add(name, 0, 0);
		this.add(capacity, 1, 0);
		
		this.add(address, 0, 1);
		this.add(ping, 1, 1);
		
		this.add(crtMap, 2, 0);
		this.add(nextMap, 3, 0);
		
		this.setHgap(25);
		this.setVgap(5);
		
		this.setPadding(new Insets(15, 0, 5, 0));
	
		refreshAll();
	}
	
	
	public void refreshAll() {
		name.setText(server.getName());
		address.setText(server.getAddress() + ":" + server.getPort());
		
		refreshPlayerCount();
		refreshPing();
	}
	
	public void refreshPlayerCount() {
		Platform.runLater(() -> {
			ping.setText(server.getPing() + " ms");
			capacity.setText(server.getConnectedPlayers().size() + "/" + server.getMaxCapacity());
		});
	}
	
	public void refreshMaps() {
		Platform.runLater(() -> {
			crtMap.setText(server.getCurrentMap());
			nextMap.setText("Next : " + server.getNextMap());
		});
	}
	
	public void refreshPing() {
		Platform.runLater(() -> {
			ping.setText(server.getPing() + " ms");
		});
	}
	
}
