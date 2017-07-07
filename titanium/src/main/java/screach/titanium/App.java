package screach.titanium;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import screach.titanium.core.Server;
import screach.titanium.gui.MainPane;
import utils.ServerListLoader;;

public class App extends Application {
	public final static int WIDTH_DFT = 750;
	public final static int HEIGTH_DFT = 820;
	public final static String APP_NAME = "Titanium";
	public final static String VERSION = "0.2";
	
	
	private List<Server> servers; // Not really useful. New tabs are not included in that list.
	
	private MainPane pane;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initModel();
		
		primaryStage.setTitle(APP_NAME + " - " + VERSION);
		primaryStage.setWidth(WIDTH_DFT);
		primaryStage.setHeight(HEIGTH_DFT);
		pane = new MainPane(primaryStage);
		Scene s = new Scene(pane);
		primaryStage.setScene(s);
		
		refreshTabs();
		
		primaryStage.show();
	}
	
	
	private void initModel() {
		servers = ServerListLoader.loadServers();
	}
	
	private void refreshTabs() {
		pane.refreshTabs(servers);
	}
}
