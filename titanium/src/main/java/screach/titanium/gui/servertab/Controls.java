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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import screach.titanium.gui.dialogs.MapSelectorDialog;

public class Controls extends BorderPane {
	private ServerTab tab;
	private TextArea consoleLogs;
	private TextField manualCommandField;
	
	public Controls(ServerTab tab) {
		super();
		
		this.tab = tab;
		
		ToolBar toolbar = new ToolBar();
		
		Button disconnect = new Button("Disconnect");
		Button broadcast = new Button("Broadcast");
		Button enableKits = new Button("Allow all kits");
		Button disableKits = new Button("Disallow all kits");
		Button setNextMap = new Button("Set next map");
		Button changeMap = new Button("Change map");
		Button setSlomo = new Button("Set slomo");
		Button resetSlomo = new Button("Reset slomo");
		Button enableVClaim = new Button("Enable vehicule claiming");
		Button disableVClaim = new Button("Disable vehicule claiming");
		
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
		toolbar.getItems().add(getSeparator());
		toolbar.getItems().addAll(enableKits, disableKits);
		toolbar.getItems().add(getSeparator());
		toolbar.getItems().addAll(setSlomo, resetSlomo);
		toolbar.getItems().add(getSeparator());
		toolbar.getItems().addAll(enableVClaim, disableVClaim);

		consoleLogs = new TextArea();
		consoleLogs.setEditable(false);		
		
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
	
	
	private void disconnectAction(Event e) {
		tab.disconnect();
	}
	
	private void enableKitsAction(Event e) {
		tab.getServer().enableAllKits();
	}

	private void disableKitsAction(Event e) {
		tab.getServer().disableAllKits();
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
			tab.getServer().changeMap(result.get());
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
			}
		}
	
	}
	
	private void resetSlomoAction(Event e) {
		tab.getServer().resetSlomo();
	}
	
	private void sendManualCommandAction(Event e) {
		String cmd = manualCommandField.getText().trim();
		
		if (!cmd.isEmpty()) {
			tab.getServer().executeCommand(cmd, true);
			manualCommandField.setText("");
		}
		
	}
	
	private void broadcastAction(Event e) {
		TextInputDialog dial = new TextInputDialog();
		dial.setTitle("Broadcast");

		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
			tab.getServer().broadcast(result.get());
		}
		
	}
	
	private void enableVClaimAction(Event e) {
		tab.getServer().lockVehicules();
	}
	
	private void disableVClaimAction(Event e) {
		tab.getServer().unlockVehicules();
	}
	
	public void addLog(String log) {
		if (!log.trim().endsWith("\n"))
			log += "\n";
			
		consoleLogs.appendText(log);
	}
	
	private Separator  getSeparator() {
		Separator result = new Separator(Orientation.VERTICAL);
		result.setPadding(new Insets(0, 5, 0, 8));
		
		return result;
	}
}
