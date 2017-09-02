package utils;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

public class ControlAvailabilityManager {
	private List<Button> connectionRequiredButtons;
	private List<MenuItem> connectionRequiredMenuItems;
	
	private List<Button> notConnectedRequiredButtons;
	private List<MenuItem> notConnectedRequiredMenuItems;
	
	private List<Button> wspConnectionRequiredButtons;
	private List<MenuItem> wspConnectionRequiredMenuItems;
	
	private List<Button> wspNotConnectedRequiredButtons;
	private List<MenuItem> wspNotConnectedRequiredMenuItems;
	
	public ControlAvailabilityManager() {
		connectionRequiredButtons = new ArrayList<>();
		connectionRequiredMenuItems = new ArrayList<>();
	
		notConnectedRequiredButtons = new ArrayList<>();
		notConnectedRequiredMenuItems = new ArrayList<>();
		
		wspConnectionRequiredButtons = new ArrayList<>();
		wspConnectionRequiredMenuItems = new ArrayList<>();
		
		wspNotConnectedRequiredButtons = new ArrayList<>();
		wspNotConnectedRequiredMenuItems = new ArrayList<>();
		
	}

	public void connectedServer() {
		enableButtonsAndItems(connectionRequiredButtons, connectionRequiredMenuItems);
		disableButtonsAndItems(notConnectedRequiredButtons, notConnectedRequiredMenuItems);
	}
	
	public void disconnectedServer() {
		disableButtonsAndItems(connectionRequiredButtons, connectionRequiredMenuItems);
		enableButtonsAndItems(notConnectedRequiredButtons, notConnectedRequiredMenuItems);
	}
	
	public void connectedWSP() {
		enableButtonsAndItems(wspConnectionRequiredButtons, wspConnectionRequiredMenuItems);
		disableButtonsAndItems(wspNotConnectedRequiredButtons, wspNotConnectedRequiredMenuItems);
	}
	
	public void disconnectedWSP() {
		disableButtonsAndItems(wspConnectionRequiredButtons, wspConnectionRequiredMenuItems);
		enableButtonsAndItems(wspNotConnectedRequiredButtons, wspNotConnectedRequiredMenuItems);
	}
	
	public List<Button> getConnectionRequiredButtons() {
		return connectionRequiredButtons;
	}
	
	public List<MenuItem> getConnectionRequiredMenuItems() {
		return connectionRequiredMenuItems;
	}
	
	public List<Button> getWspConnectionRequiredButtons() {
		return wspConnectionRequiredButtons;
	}
	
	public List<MenuItem> getWspConnectionRequiredMenuItems() {
		return wspConnectionRequiredMenuItems;
	}
	
	public List<Button> getWspNotConnectedRequiredButtons() {
		return wspNotConnectedRequiredButtons;
	}
	
	public List<MenuItem> getWspNotConnectedRequiredMenuItems() {
		return wspNotConnectedRequiredMenuItems;
	}
	
	public List<Button> getNotConnectedRequiredButtons() {
		return notConnectedRequiredButtons;
	}
	
	public List<MenuItem> getNotConnectedRequiredMenuItems() {
		return notConnectedRequiredMenuItems;
	}
	
	private void disableButtonsAndItems(List<Button> lb, List<MenuItem> li) {
		lb.forEach(b -> b.setDisable(true));
		li.forEach(i -> i.setDisable(true));
	}
	
	private void enableButtonsAndItems(List<Button> lb, List<MenuItem> li) {
		lb.forEach(b -> b.setDisable(false));
		li.forEach(i -> i.setDisable(false));
	}
}
