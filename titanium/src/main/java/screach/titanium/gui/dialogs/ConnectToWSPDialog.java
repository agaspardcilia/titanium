package screach.titanium.gui.dialogs;

import java.net.MalformedURLException;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import screach.titanium.App;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.dialogs.listeners.RequieredListener;
import utils.ErrorUtils;

public class ConnectToWSPDialog extends Dialog<String> {
	public ConnectToWSPDialog(WebServiceProvider wsp) {
		super();
		// Create the custom dialog.
		this.setTitle("Sign in");
		this.setHeaderText("WSP informations");


		// Set the button types.
		ButtonType addButtonType = new ButtonType("Sign in", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		Button openDiscordUrlButton = new Button("Sign in via Discord");
		TextField codeField = new TextField();
		
		openDiscordUrlButton.setOnAction((event) -> {
			try {
				App.getCurrentInstance().getHostServices().showDocument(wsp.getDiscordAuthURL());
			} catch (MalformedURLException e) {
				Alert a = ErrorUtils.getAlertFromException(e);
				a.show();
				
				e.printStackTrace();
			}
		});

		grid.addRow(0, openDiscordUrlButton);
		grid.addRow(1, new Label("Authorization code"), codeField);


		Node okButton = this.getDialogPane().lookupButton(addButtonType);



		// Verify required inputs
		// TODO not working
		codeField.textProperty().addListener(new RequieredListener(okButton));


		this.getDialogPane().setContent(grid);

		this.setResultConverter(dialogButton -> {

			if (dialogButton.getButtonData().equals(ButtonData.OK_DONE)) {
				return codeField.getText();
			}

			return null;
		});

	}
}
