package screach.titanium.gui.servertab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import screach.titanium.core.ConnectionFailureException;
import screach.titanium.core.NotifyEventType;
import screach.titanium.core.Player;
import screach.titanium.core.Server;
import screach.titanium.gui.MainPane;
import screach.titanium.gui.ServerTabsPane;
import screach.titanium.gui.dialogs.EditServerDialog;

public class ServerTab extends Tab implements Observer {
	private Server server;

	private ServerTabsPane tabs;
	
	private Pane connectedPane;
	private Pane notConnectedPane;


	private PlayerListTable connectedPlayerTable;
	private PlayerListTable dcPlayerTable;

	private ObservableList<PlayerView> connectedPlayersList;
	private ObservableList<PlayerView> dcPlayersList;

	private ServerInfoPane serverInfo;
	private Controls controlsPane;

	
	
	public ServerTab(Server server, ServerTabsPane tabs) {
		super(server.getName());
		this.server = server;
		this.tabs = tabs;
		
		this.setClosable(false);

		server.addObserver(this);

		connectedPlayersList = FXCollections.observableArrayList();
		connectedPlayersList.addAll(server.getConnectedPlayers().stream().map(p -> new PlayerView(p, server, tabs.getApplication())).collect(Collectors.toList()));

		dcPlayersList = FXCollections.observableArrayList();
		dcPlayersList.addAll(server.getRecentlyDCPlayers().stream().map(p -> new PlayerView(p, server, tabs.getApplication())).collect(Collectors.toList()));


		setupConnectedPane();
		setupNotConnectedPane();

		this.setContent(notConnectedPane);

	}

	public void setupConnectedPane() {
		connectedPlayerTable = new PlayerListTable(connectedPlayersList);
		dcPlayerTable = new PlayerListTable(dcPlayersList);

		connectedPane = new VBox();

		GridPane playersPane = new GridPane();
		playersPane.setPadding(new Insets(10, 0, 10, 0));

		VBox connectePlayersdPane = new VBox();
		VBox dcPlayersPane = new VBox();

		connectePlayersdPane.setPadding(new Insets(0, 5, 0, 0));
		dcPlayersPane.setPadding(new Insets(0, 0, 0, 5));

		Label connectedPlayerLabel = new Label("Connected players");
		connectedPlayerLabel.setPadding(new Insets(0, 0, 5, 0));
		Label dcPlayerLabel = new Label("Disconnected players");
		dcPlayerLabel.setPadding(new Insets(0, 0, 5, 0));



		connectePlayersdPane.getChildren().addAll(connectedPlayerLabel, connectedPlayerTable);
		dcPlayersPane.getChildren().addAll(dcPlayerLabel, dcPlayerTable);

		// Col and row length
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);
		RowConstraints row = new RowConstraints();
		row.setPercentHeight(100);

		playersPane.getColumnConstraints().addAll(column1, column2);
		playersPane.getRowConstraints().add(row);

		playersPane.add(connectePlayersdPane, 0, 0);
		playersPane.add(dcPlayersPane, 1, 0);

		serverInfo = new ServerInfoPane(server);
		controlsPane = new Controls(this);

		connectedPane.setPadding(new Insets(0, 10, 0, 10));

		connectedPane.getChildren().add(serverInfo);
		connectedPane.getChildren().add(new Separator(Orientation.HORIZONTAL));
		connectedPane.getChildren().add(playersPane);
		connectedPane.getChildren().add(new Separator(Orientation.HORIZONTAL));
		connectedPane.getChildren().addAll(controlsPane);


	}

	public void setupNotConnectedPane() {
		notConnectedPane = new VBox(15);
		notConnectedPane.setPadding(new Insets(15, 15, 15, 15));

		Label l = new Label("Not connected");
		Button connect = new Button("Connect");
		Button edit = new Button("Edit server informations...");

		connect.setOnAction(this::connectButtonAction);
		edit.setOnAction(this::editButtonAction);
		
		notConnectedPane.getChildren().addAll(l, connect, edit);
	}

	public void switchToDisconnected() {
		this.setContent(notConnectedPane);
	}

	public void switchToConnected() {
		this.setContent(connectedPane);
	}

	private void connectButtonAction(Event e) {
		try {
			connect();
		} catch (IllegalStateException | IOException | ConnectionFailureException e1) {
			switchToDisconnected();
			new Alert(AlertType.ERROR, "Connection to \"" + server + "\" has failed (" + e1.getMessage() + ")").show();
			e1.printStackTrace();
		}
	}
	
	private void editButtonAction(Event e) {
		EditServerDialog dial = new EditServerDialog(server);
		
		Optional<Server> result = dial.showAndWait();
		
		if (result.isPresent()) {
			server.changeInformations(result.get());
			serverInfo.refreshAll();
			this.setText(server.getName());
			tabs.writeServerList();
		}
		
	}

	public void connect() throws IllegalStateException, IOException, ConnectionFailureException {
		server.connect();
		switchToConnected();
	}

	public void disconnect() {
		server.disconnect();
		switchToDisconnected();
	}

	public Server getServer() {
		return server;
	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof NotifyEventType) {
			NotifyEventType eventType = (NotifyEventType) arg;

			switch (eventType) {
			case CONSOLE_LOG:
				updateConsoleLog(server.getLastLog());
				break;
			case PLAYER_LIST:
				updateConnected(server.getConnectedPlayers());
				updateNotConnected(server.getRecentlyDCPlayers());
				break;
			case MAP_CHANGED:
				updateMaps();
				break;
			case PING:
				updatePing();
				break;
			}


		}
		
	}


	private void updateConnected(List<Player> players) {
		List<PlayerView> toRemove = new ArrayList<PlayerView>();

		connectedPlayersList.forEach(p -> {
			if (!players.contains(p)) 
				toRemove.add(p);
		});

		connectedPlayersList.removeAll(toRemove);

		players.forEach(p -> {
			if (!connectedPlayersList.contains(p))
				connectedPlayersList.add(new PlayerView(p, server, tabs.getApplication()));
		});
		
		serverInfo.refreshPlayerCount();

	}

	private void updateNotConnected(List<Player> players) {
		players.forEach(p -> {
			if (!dcPlayersList.contains(p))
				dcPlayersList.add(new PlayerView(p, server, tabs.getApplication()));
		});
	}
	
	private void updateConsoleLog(String log) {
		controlsPane.addLog(log);
	}
	
	private void updateMaps() {
		serverInfo.refreshMaps();
	}
	
	private void updatePing() {
		serverInfo.refreshPing();
	}
}
