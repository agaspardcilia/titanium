package screach.titanium.gui.dialogs.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public class RequieredListener implements ChangeListener<String>{
	private Node submit;
	
	public RequieredListener(Node submit) {
		this.submit = submit;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		submit.setDisable(newValue.trim().isEmpty());
	}
}
