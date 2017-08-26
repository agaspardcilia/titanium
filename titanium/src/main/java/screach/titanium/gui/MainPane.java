package screach.titanium.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import screach.titanium.core.server.Server;
import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.WSPServer;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.dialogs.AddServerDialog;
import screach.titanium.gui.dialogs.EditWSPDialog;
import screach.titanium.gui.org.OrganizationManagerStage;
import utils.ServerListLoader;

public class MainPane extends BorderPane {
	private Stage primaryStage;
	private MenuBar menu;
	private ServerTabsPane content;

	private List<LocalServer> servers;
	private WebServiceProvider wsp;

	private Application app;

	public MainPane(Stage primaryStage, Application app, WebServiceProvider wsp) {
		super();
		this.app = app;
		this.primaryStage = primaryStage;
		this.wsp = wsp;

		setupPane();
		servers = new ArrayList<>();
		primaryStage.setOnCloseRequest(this::quitAction);
	}

	private void setupPane() {
		menu = getMenuBar();

		this.setTop(menu);

		content = new ServerTabsPane(this);

		this.setCenter(content);

	}

	private MenuBar getMenuBar() {
		MenuBar result = new MenuBar();

		result.getMenus().addAll(getServerMenu(), getWSMenu(), getHelpMenu());

		return result;
	}

	private Menu getServerMenu() {
		Menu result = new Menu("Servers");
		MenuItem addServerItem = new MenuItem("Add a server...");
		MenuItem removeCrtServerItem = new MenuItem("Remove current server");
		MenuItem connectToAll = new MenuItem("Connect to all servers");
		MenuItem disconnectAllItem = new MenuItem("Disconnect from all servers");
		MenuItem quitItem = new MenuItem("Quit");

		addServerItem.setOnAction(this::addServerAction);
		removeCrtServerItem.setOnAction(this::removeServerAction);
		connectToAll.setOnAction(this::connectToAllAction);
		disconnectAllItem.setOnAction(this::disconnectFromAll);
		quitItem.setOnAction(this::quitAction);

		result.getItems().addAll(addServerItem, removeCrtServerItem);
		result.getItems().add(new SeparatorMenuItem());
		result.getItems().addAll(connectToAll, disconnectAllItem);
		result.getItems().add(new SeparatorMenuItem());
		result.getItems().addAll(quitItem);

		return result;
	}

	private Menu getHelpMenu() {
		Menu result = new Menu("Help");
		MenuItem aboutItem = new MenuItem("About Titanium");

		result.getItems().addAll(aboutItem);

		return result;
	}

	private Menu getWSMenu() {
		Menu result = new Menu("WSP");

		MenuItem editWSPItem = new MenuItem("Edit WSP...");
		MenuItem orgManagerItem = new MenuItem("Orgnaization manager...");


		editWSPItem.setOnAction(this::editWSPAction);
		orgManagerItem.setOnAction(this::organizationManagerAction);

		result.getItems().add(editWSPItem);
		result.getItems().add(orgManagerItem);

		return result;
	}

	private void addServerAction(Event e) {
		AddServerDialog dial = new AddServerDialog();

		Optional<LocalServer> s = dial.showAndWait();

		if (s.isPresent()) {
			addServer(s.get());
			ServerListLoader.writeServerList(servers);
		}

	}

	private void removeServerAction(Event e) {
		Alert conf = new Alert(AlertType.CONFIRMATION, "Do you really want to remove this server ?");

		Optional<ButtonType> result = conf.showAndWait();

		if (result.isPresent() && result.get().equals(ButtonType.OK)) {
			Server s = content.removeSelectedServer();
			if (s != null) {
				servers.remove(s);
				ServerListLoader.writeServerList(servers);
			} else
				new Alert(AlertType.ERROR, "No selected server");

		}
	}

	private void connectToAllAction(Event e) {
		content.connectToAll();
	}

	private void disconnectFromAll(Event e) {
		content.disconnectFromAll();
	}

	private void quitAction(Event e) {
		content.disconnectFromAll();

		primaryStage.close();
		
		Platform.exit();
	}

	public void refreshTabs(List<LocalServer> servers, List<WSPServer> wspServers) {
		content.refreshTabs(servers, wspServers);
		this.servers = servers;
	}

	public void addServer(LocalServer server) {
		content.addTab(server);
		servers.add(server);
	}

	public void editWSPAction(Event e) {
		EditWSPDialog dialog = new EditWSPDialog(wsp);
		Optional<WebServiceProvider> result = dialog.showAndWait();

		if (result.isPresent()) {
			wsp = result.get();
		} else {
			new Alert(AlertType.ERROR, "Wrong WSP information");
		}
	}

	public void organizationManagerAction(Event e) {
		OrganizationManagerStage oms = new OrganizationManagerStage(wsp);

		oms.show();
	}

	public List<LocalServer> getServerList() {
		return servers;
	}

	public Application getApp() {
		return app;
	}

	public void writeServerList() {
		ServerListLoader.writeServerList(servers);
	}

}
