package screach.titanium.gui.servertab;


import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class PlayerListTable extends TableView<PlayerView>{
	private ObservableList<PlayerView> players;
	
	
	public PlayerListTable(ObservableList<PlayerView> players) {
		super();

		TableColumn<PlayerView, Integer> id = new TableColumn<>("ID");
		TableColumn<PlayerView, String> name = new TableColumn<>("Name");
		TableColumn<PlayerView, HBox> steamId = new TableColumn<>("Steam ID");
		TableColumn<PlayerView, String> vacBans = new TableColumn<>("VAC");
		TableColumn<PlayerView, HBox> actions = new TableColumn<>("Actions");

		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		steamId.setCellValueFactory(new PropertyValueFactory<>("steamPan"));
		vacBans.setCellValueFactory(new PropertyValueFactory<>("vacBans"));
		actions.setCellValueFactory(new PropertyValueFactory<>("actionPan"));
		
		id.setMaxWidth(25);
		id.setMinWidth(25);
		steamId.setMinWidth(220);
		steamId.setMaxWidth(220);
		vacBans.setMaxWidth(50);
		vacBans.setMinWidth(50);
		actions.setMinWidth(80);
		actions.setMaxWidth(80);
		
		id.setResizable(false);
		steamId.setResizable(false);
		actions.setResizable(false);
		vacBans.setResizable(false);
		
		this.getColumns().add(id);
		this.getColumns().add(name);
		this.getColumns().add(steamId);
		this.getColumns().add(vacBans);
		this.getColumns().add(actions);

		
		this.players = players;
		
		this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.setPrefHeight(1000000);

		this.setItems(this.players);

	}
	
}
