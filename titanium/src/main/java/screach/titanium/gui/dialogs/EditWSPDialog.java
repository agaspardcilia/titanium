package screach.titanium.gui.dialogs;

import java.net.MalformedURLException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.dialogs.listeners.RequieredListener;
import utils.webapi.WebApi;

public class EditWSPDialog extends Dialog<WebServiceProvider>{
	private ObservableList<String> availableProtocols = FXCollections.observableArrayList("http", "https");

	public EditWSPDialog(WebServiceProvider wsp) {
		super();
		// Create the custom dialog.
		this.setTitle("Edit Web Service Provider");
		this.setHeaderText("WSP informations");


		// Set the button types.
		ButtonType addButtonType = new ButtonType("Edit WSP", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField wspName = new TextField();
		ComboBox<String> protocol = new ComboBox<>(availableProtocols);
		TextField host = new TextField();
		TextField basePath = new TextField();
		TextField port = new TextField();
		
		if (wsp != null) {
			wspName.setText(wsp.getName());
			host.setText(wsp.getApi().getHost());
			basePath.setText(wsp.getApi().getBasePath());
			port.setText(wsp.getApi().getPort() + "");
			protocol.getSelectionModel().select(wsp.getApi().getProtocol());
		} else {
			protocol.getSelectionModel().select("http");
		}
		
		grid.add(new Label("WSP name"), 0, 0);
		grid.add(wspName, 1, 0);
		grid.add(new Label("Protocol"), 0, 1);
		grid.add(protocol, 1, 1);
		grid.add(new Label("Host"), 0, 2);
		grid.add(host, 1, 2);
		grid.add(new Label("Port"), 0, 3);
		grid.add(port, 1, 3);
		grid.add(new Label("Api path"), 0, 4);
		grid.add(basePath, 1, 4);

		port.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					port.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		Node addButton = this.getDialogPane().lookupButton(addButtonType);



		// Verify required inputs
		// TODO not working
		wspName.textProperty().addListener(new RequieredListener(addButton));
		host.textProperty().addListener(new RequieredListener(addButton));
		port.textProperty().addListener(new RequieredListener(addButton));


		this.getDialogPane().setContent(grid);

		this.setResultConverter(dialogButton -> {
			try {
				if (dialogButton == addButtonType) {
					try {
						WebApi webApi = new WebApi(protocol.getValue(), host.getText(), Integer.parseInt(port.getText()), basePath.getText());
						
						WebServiceProvider result = new WebServiceProvider(webApi, wspName.getText());
						
						return result;
					} catch (MalformedURLException e) {
						return null;
					}
				}
			} catch (NumberFormatException e) {
				return null;
			}
			return null;
		});

	}


}
