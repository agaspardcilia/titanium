package screach.titanium.gui.dialogs;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class BanDialog extends Dialog<Pair<String, Integer>> {
	public BanDialog(String playerName, String defaultReason, int defaultDuration) {
		super();
		// Create the custom dialog.
		this.setTitle("Ban dialog");
		this.setHeaderText("Do you really want to ban " + playerName + " ?");


		// Set the button types.
		ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField reason = new TextField();
		reason.setText(defaultReason);
		TextField duration = new TextField();
		duration.setText(defaultDuration+"");

		duration.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if (!newValue.matches("\\d*")) {
	                duration.setText(newValue.replaceAll("[^\\d]", ""));
	            }
	        }
	    });
		
		
		grid.add(new Label("Reason"), 0, 0);
		grid.add(reason, 1, 0);
		grid.add(new Label("Duration, in days (0 = perm)"), 0, 1);
		grid.add(duration, 1, 1);

		Node loginButton = this.getDialogPane().lookupButton(loginButtonType);

		reason.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		this.getDialogPane().setContent(grid);

		this.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(reason.getText(), Integer.parseInt(duration.getText()));
		    }
		    return null;
		});

	}
}
