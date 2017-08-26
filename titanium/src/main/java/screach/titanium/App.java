package screach.titanium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.WSPServer;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.MainPane;
import utils.AssetsLoader;
import utils.ServerListLoader;
import utils.WapiLoader;
import utils.webapi.HttpException;;

public class App extends Application {
	private static App currentInstance;
	
	public final static int WIDTH_DFT = 885;
	public final static int HEIGTH_DFT = 820;
	public final static String APP_NAME = "Titanium";
	public final static String VERSION = "0.5";


	private List<LocalServer> servers; // Not really useful. New tabs are not included in that list.
	private List<WSPServer> wspServers;

	private WebServiceProvider wsp = null;


	private MainPane pane;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		currentInstance = this;
		initModel();
		
		primaryStage.setTitle(APP_NAME + " - " + VERSION);
		primaryStage.setWidth(WIDTH_DFT);
		primaryStage.setHeight(HEIGTH_DFT);
		primaryStage.setResizable(true);
		pane = new MainPane(primaryStage, this, wsp);
		Scene s = new Scene(pane);
		primaryStage.setScene(s);
		primaryStage.getIcons().add(AssetsLoader.getAsset("titanium_icon.png"));

		refreshTabs();
			
		primaryStage.show();
	}


	private void initModel() {
		servers = ServerListLoader.loadServers();
		List<WebServiceProvider> wsps = WapiLoader.loadWapi();

		wspServers = new ArrayList<>();
		
		if (!wsps.isEmpty()) {
			wsp = wsps.get(0); // XXX only works with one wsp atm.
			try {
				wsp.connect();
				
				wsp.fecthAndSetOrganization();
				wsp.updateServerList();
				wspServers = wsp.getAllAvailableServers();
			} catch (WebApiException | IOException | HttpException e) {
				e.printStackTrace();
			}
		} 




	}

	private void refreshTabs() {
		pane.refreshTabs(servers, wspServers);
	}
	public void refreshWSPTabs() {
		wsp.updateServerList();
		wspServers = wsp.getAllAvailableServers();
		refreshTabs();
	}

	public static App getCurrentInstance() {
		return currentInstance;
	}
}
