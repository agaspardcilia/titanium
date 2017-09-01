package screach.titanium.gui.servertab;

import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import screach.titanium.core.server.RCONServerException;
import screach.titanium.gui.dialogs.MapSelectorDialog;
import utils.AssetsLoader;

public class Controls extends BorderPane {
	private final static int DEFAULT_ICON_SIZE = 25;
	
	private ServerTab tab;
	private TextArea consoleLogs;
	private TextField manualCommandField;
	
	public Controls(ServerTab tab) {
		super();
		
		this.tab = tab;
		
		ToolBar toolbar = new ToolBar();
		
		Button disconnect = new Button("", AssetsLoader.getAssetView("disconnect.png", DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE));
		Button broadcast = new Button("", AssetsLoader.getAssetView("broadcast.png", DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE));
		Button enableKits = new Button("Allow all kits");
		Button disableKits = new Button("Disallow all kits");
		Button setNextMap = new Button("", AssetsLoader.getAssetView("next_map.png", DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE));
		Button changeMap = new Button("", AssetsLoader.getAssetView("change_map.png", DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE));
		Button setSlomo = new Button("Set slomo");
		Button resetSlomo = new Button("Reset slomo");
		Button enableVClaim = new Button("Enable vehicule claiming");
		Button disableVClaim = new Button("Disable vehicule claiming");
		
		disconnect.setTooltip(new Tooltip("Disconnect from server."));
		broadcast.setTooltip(new Tooltip("Broadcast a message."));
		changeMap.setTooltip(new Tooltip("Change current map. Be careful, this command will immediatly switch to the desired map."));
		setNextMap.setTooltip(new Tooltip("Change the next played map."));
		
		disconnect.setOnAction(this::disconnectAction);
		enableKits.setOnAction(this::enableKitsAction);
		disableKits.setOnAction(this::disableKitsAction);
		setNextMap.setOnAction(this::setNextMapAction);
		setSlomo.setOnAction(this::setSlomoAction);
		resetSlomo.setOnAction(this::resetSlomoAction);
		broadcast.setOnAction(this::broadcastAction);
		enableVClaim.setOnAction(this::enableVClaimAction);
		disableVClaim.setOnAction(this::disableVClaimAction);
		changeMap.setOnAction(this::changeMapAction);
		
		toolbar.getItems().add(disconnect);
		toolbar.getItems().add(getSeparator());
		toolbar.getItems().add(broadcast);
		toolbar.getItems().add(getSeparator());
		toolbar.getItems().addAll(setNextMap, changeMap);
		
		// XXX not working atm, owi disabled them.
//		toolbar.getItems().add(getSeparator());
//		toolbar.getItems().addAll(enableKits, disableKits);
//		toolbar.getItems().add(getSeparator());
//		toolbar.getItems().addAll(setSlomo, resetSlomo);
//		toolbar.getItems().add(getSeparator());
//		toolbar.getItems().addAll(enableVClaim, disableVClaim);

		consoleLogs = new TextArea();
		consoleLogs.setEditable(false);		

		consoleLogs.textProperty().addListener((obs, oldV, newV) -> {
			consoleLogs.setScrollTop(Double.MAX_VALUE);
		});
		
		manualCommandField = new TextField();
		Button manualCommandButton = new Button("Send");
		
		HBox manualPane = new HBox();
		
		
		
		manualPane.getChildren().addAll(manualCommandField, manualCommandButton);
		
		manualCommandField.prefWidthProperty().bind(consoleLogs.widthProperty());
		
		manualCommandButton.setMaxWidth(70);
		manualCommandButton.setMinWidth(70);
		
		
		manualCommandButton.setOnAction(this::sendManualCommandAction);
		manualCommandField.setOnAction(this::sendManualCommandAction);
		
		this.setTop(toolbar);
		this.setCenter(consoleLogs);
		this.setBottom(manualPane);
	}
	
	
	public void disconnectAction(Event e) {
		tab.disconnect();
	}
	
	private void enableKitsAction(Event e) {
		try {
			tab.getServer().enableAllKits();
		} catch (RCONServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void disableKitsAction(Event e) {
		try {
			tab.getServer().disableAllKits();
		} catch (RCONServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void setNextMapAction(Event e) {
		MapSelectorDialog dial = new MapSelectorDialog("Next map selector");
		
		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
			tab.getServer().setNextMap(result.get());
		}
		
	}
	
	private void changeMapAction(Event e) {
		MapSelectorDialog dial = new MapSelectorDialog("Map selector");
		
		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
			try {
				tab.getServer().changeMap(result.get());
			} catch (RCONServerException e1) {
				logError(e1);
			}
		}
		
	}
	
	private void setSlomoAction(Event e) {
		TextInputDialog dial = new TextInputDialog(1+"");
		dial.setTitle("Slomo");
		dial.setContentText("Clock speed (2 = twice the normal server speed)");
		
		dial.getEditor().textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if (!newValue.matches("\\d*")) {
	            	dial.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
	            }
	        }
	    });
		
		
		Optional<String> result = dial.showAndWait();

		if (result.isPresent()) {
			try {
				tab.getServer().setSlomo(Integer.parseInt(result.get()));
			} catch (NumberFormatException e1) {
				new Alert(AlertType.ERROR, "Invalid clock speed value");
			} catch (RCONServerException e1) {
				logError(e1);
			}
		}
	
	}
	
	private void resetSlomoAction(Event e) {
		try {
			tab.getServer().resetSlomo();
		} catch (RCONServerException e1) {
			logError(e1);
		}
	}
	
	private void sendManualCommandAction(Event e) {
		String cmd = manualCommandField.getText().trim();
		
		if (!cmd.isEmpty()) {
			addLog("> " + cmd);
			try {
				tab.getServer().executeCommand(cmd, false);
			} catch (RCONServerException e1) {
				logError(e1);
			}
			manualCommandField.setText("");
		}
		
	}
	
	private void broadcastAction(Event e) {
		TextInputDialog dial = new TextInputDialog();
		dial.setTitle("Broadcast");

		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
			try {
				tab.getServer().broadcast(result.get());
			} catch (RCONServerException e1) {
				logError(e1);
			}
		}
		
	}
	
	private void enableVClaimAction(Event e) {
		try {
			tab.getServer().lockVehicules();
		} catch (RCONServerException e1) {
			logError(e1);
		}
	}
	
	private void disableVClaimAction(Event e) {
		try {
			tab.getServer().unlockVehicules();
		} catch (RCONServerException e1) {
			logError(e1);
		}
	}
	
	private void logError(RCONServerException e) {
		tab.getServer().logError(e);
	}
	
	public void addLog(String log) {
		if (!log.trim().endsWith("\n"))
			log += "\n";
			
		consoleLogs.appendText(log);
	}
	
	private Separator  getSeparator() {
		Separator result = new Separator(Orientation.VERTICAL);
		result.setPadding(new Insets(0, 0, 0, 3));
		
		return result;
	}
}
