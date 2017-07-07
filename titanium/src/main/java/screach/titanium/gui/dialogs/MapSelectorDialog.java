package screach.titanium.gui.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import utils.MapListLoader;

public class MapSelectorDialog extends Dialog<String> {
	public MapSelectorDialog(String title) {
		super();
		// Create the custom dialog.
		this.setTitle(title);

		// Set the button types.
		ButtonType addButtonType = new ButtonType("Select map", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		VBox inputPane = new VBox();

		ComboBox<String> maps = new ComboBox<>();
		
		maps.getItems().addAll(MapListLoader.loadMapList());
		
		inputPane.getChildren().addAll(new Label("Select a map"), maps);


		this.getDialogPane().setContent(inputPane);

		this.setResultConverter(dialogButton -> {
			try {
				if (dialogButton == addButtonType) {
					return maps.getSelectionModel().getSelectedItem();
				}
			} catch (NumberFormatException e) {
				return null;
			}
			return null;
		});

	}
}
