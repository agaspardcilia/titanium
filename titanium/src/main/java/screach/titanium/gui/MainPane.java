package screach.titanium.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;

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
import screach.titanium.App;
import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.WSPServer;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.dialogs.AddServerDialog;
import screach.titanium.gui.dialogs.ConnectToWSPDialog;
import screach.titanium.gui.dialogs.EditWSPDialog;
import screach.titanium.gui.org.OrganizationManagerStage;
import utils.AssetsLoader;
import utils.ErrorUtils;
import utils.ServerListLoader;
import utils.WapiLoader;
import utils.webapi.HttpException;

public class MainPane extends BorderPane {
	private Stage primaryStage;
	private MenuBar menu;
	private ServerTabsPane content;

	private MenuItem orgManagerItem;
	
	private List<LocalServer> servers;
	private WebServiceProvider wsp;

	private App app;

	public MainPane(Stage primaryStage, App app, WebServiceProvider wsp) {
		super();
		this.app = app;
		this.primaryStage = primaryStage;
		this.wsp = wsp;

		setupPane();
		servers = new ArrayList<>();
		primaryStage.setOnCloseRequest(this::quitAction);
		Platform.runLater(() -> {
			initWSP();
		});
	}
	
	private void initWSP() {
		if (wsp != null) {
			LoadingStage ls = null;
			try {
				wsp.fetchConfiguration();
				if (!wsp.getKey().isEmpty()) {
					ls = new LoadingStage("WSP sign in", "");
					ls.initOwner(primaryStage);
					ls.show();
					ls.requestFocus();
					
					loadUser(ls);
					wsp.setConnected(true);
					orgManagerItem.setDisable(false);
				}
			} catch (WebApiException e) {
				Alert a;
				if (e.getCode() == WebServiceProvider.API_ERROR_INVALID_KEY) {
					a = ErrorUtils.newErrorAlert("Web Service Provider Error", "You token is invalid. Sign in again please.", e.getCode() + " : " + e.getMessage());
					wsp.setKey("");
					writeWSP(wsp);
				} else {
					a = ErrorUtils.newErrorAlert("Web Service Provider Error", "Cannot retreive wsp configuration", e.getClass() + "\n" + e.getMessage());
				}
				a.initOwner(primaryStage);
				a.show();
				e.printStackTrace();
			} catch (JSONException | IOException | HttpException e) {
				
				Alert a = ErrorUtils.newErrorAlert("Web Service Provider Error", "Cannot retreive wsp configuration", e.getClass() + "\n" + e.getMessage());
				a.initOwner(primaryStage);
				a.show();
				e.printStackTrace();
			} finally {
				if (ls != null)
					ls.close();
			}
		}
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
		MenuItem addServerItem = new MenuItem("Add a server...", AssetsLoader.getIcon("new_icon.png"));
		MenuItem removeCrtServerItem = new MenuItem("Remove current server", AssetsLoader.getIcon("delete_icon.png"));
		MenuItem connectToAll = new MenuItem("Connect to all servers", AssetsLoader.getIcon("connect.png"));
		MenuItem disconnectAllItem = new MenuItem("Disconnect from all servers", AssetsLoader.getIcon("disconnect.png"));
		MenuItem quitItem = new MenuItem("Quit", AssetsLoader.getIcon("quit_icon.png"));

		
		
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

		MenuItem editWSPItem = new MenuItem("Edit WSP...", AssetsLoader.getIcon("edit.png"));
		MenuItem connectItem = new MenuItem("Sign in...", AssetsLoader.getIcon("discord_icon.png"));
		orgManagerItem = new MenuItem("Organization manager...", AssetsLoader.getIcon("wsp_icon.png"));

		orgManagerItem.setDisable(true);

		connectItem.setOnAction(this::connectToWSPAction);
		editWSPItem.setOnAction(this::editWSPAction);
		orgManagerItem.setOnAction(this::organizationManagerAction);
		
		result.getItems().add(connectItem);
		result.getItems().add(orgManagerItem);
		result.getItems().add(editWSPItem);

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

	private void editWSPAction(Event e) {
		EditWSPDialog dialog = new EditWSPDialog(wsp);
		Optional<WebServiceProvider> result = dialog.showAndWait();

		if (result.isPresent()) {
			try {
				wsp = result.get();
				wsp.fetchConfiguration();
				writeWSP(wsp);
			} catch (JSONException | WebApiException | IOException | HttpException e1) {
				Alert a = ErrorUtils.getAlertFromException(e1);
				a.show();
				e1.printStackTrace();
			}
			
		} else {
			new Alert(AlertType.ERROR, "Wrong WSP information");
		}
	}

	private void organizationManagerAction(Event e) {
		OrganizationManagerStage oms = new OrganizationManagerStage(wsp);

		oms.show();
	}

	private void connectToWSPAction(Event e) {
		if (wsp == null) {
			ErrorUtils.newErrorAlert("WSP Error", "There is no loaded Web Service Provider", 
					"You need to define a Web Service Provider first.\n"
					+ "WSP->Edit WSP...").show();
		} else {
			
			ConnectToWSPDialog dial = new ConnectToWSPDialog(wsp);
			
			Optional<String> result = dial.showAndWait();
			
			System.out.println(result);
			if (result.isPresent()) {
				writeWSP(wsp);
				LoadingStage ls = new LoadingStage("WSP sign in", "");
				ls.initOwner(primaryStage);
				ls.show();
				ls.requestFocus();
				try {
					ls.setNotice("Signing in...");
					wsp.connect(result.get());
					ls.setProgress(0.25);
					orgManagerItem.setDisable(false);
					
					loadUser(ls);
					
					writeWSP(wsp);
				} catch (IOException | HttpException e1) {
					ErrorUtils.getAlertFromException(e1).show();
					e1.printStackTrace();
				} catch(WebApiException e1){
					if (e1.getCode() == WebServiceProvider.API_ERROR_WRONG_SIGNIN_INFO) {
						ErrorUtils.newErrorAlert("Sign in error", "Authentication failed.", "Wrong username or password.").show();
					} else {
						ErrorUtils.getAlertFromException(e1).show();
					}
					e1.printStackTrace();
				} finally {
					ls.close();
				}
			
			}
		}
	}
	
	public void loadUser(LoadingStage ls) throws WebApiException, IOException, HttpException {
		ls.setNotice("Fetching user information...");
		wsp.fetchUserInfo();
		ls.setProgress(0.25);
		
		ls.setNotice("Fetching organizations...");
		wsp.fecthAndSetOrganization();
		ls.setProgress(0.5);
		
		ls.setNotice("Updating server list...");
		app.refreshWSPTabs();
		ls.setProgress(1);
		
		ls.close();
		
		
		refreshWindowTitle();
	}
	
	public List<LocalServer> getServerList() {
		return servers;
	}

	public Application getApp() {
		return app;
	}
	
	public void refreshWindowTitle() {
		app.setMainWindowsName(wsp.getInfo());
	}

	public void writeServerList() {
		ServerListLoader.writeServerList(servers);
	}
	
	private void writeWSP(WebServiceProvider wsp) {
		List<WebServiceProvider> l = new ArrayList<>();
		l.add(wsp);
		try {
			WapiLoader.writeWapi(l);
		} catch (IOException e1) {
			e1.printStackTrace();
			ErrorUtils.newErrorAlert("WSP saving error", "Error while saving Web Service Provider",
					e1.getClass() + " : " + e1.getMessage()).show();;
		}
	}

}
