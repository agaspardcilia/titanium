package screach.titanium.gui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.dialogs.listeners.RequieredListener;

public class ConnectToWSPDialog extends Dialog<ButtonType> {
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

		TextField username = new TextField();
		PasswordField password = new PasswordField();
		CheckBox rememberUsername = new CheckBox("Remember username");
		CheckBox rememberPassword = new CheckBox("Remember password");

		rememberUsername.setIndeterminate(false);
		rememberPassword.setIndeterminate(false);


		if (wsp != null) {
			username.setText(wsp.getUsername());
			password.setText(wsp.getPassword());
			rememberUsername.setSelected(wsp.rememberUsername());
			rememberPassword.setSelected(wsp.rememberPassword());
		}

		grid.addRow(0, new Label("Username"), username);
		grid.addRow(1, new Label("Password"), password);
		grid.addRow(2, rememberUsername);
		grid.addRow(3, rememberPassword);


		Node okButton = this.getDialogPane().lookupButton(addButtonType);



		// Verify required inputs
		// TODO not working
		username.textProperty().addListener(new RequieredListener(okButton));
		password.textProperty().addListener(new RequieredListener(okButton));


		this.getDialogPane().setContent(grid);

		this.setResultConverter(dialogButton -> {

			if (dialogButton.getButtonData().equals(ButtonData.OK_DONE)) {
				wsp.setUsername(username.getText());
				wsp.setPassword(password.getText());
				wsp.setRememberUsername(rememberUsername.isSelected());
				wsp.setRememberPassword(rememberPassword.isSelected());
			}

			return dialogButton;
		});

	}
}
