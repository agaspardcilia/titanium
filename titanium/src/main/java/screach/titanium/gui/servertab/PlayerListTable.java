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
		TableColumn<PlayerView, String> steamId = new TableColumn<>("Steam ID");
		TableColumn<PlayerView, HBox> actions = new TableColumn<>("Actions");

		id.setCellValueFactory(new PropertyValueFactory<PlayerView, Integer>("id"));
		name.setCellValueFactory(new PropertyValueFactory<PlayerView, String>("name"));
		steamId.setCellValueFactory(new PropertyValueFactory<PlayerView, String>("steamId"));
		actions.setCellValueFactory(new PropertyValueFactory<>("buttonPan"));
		
		id.setPrefWidth(25);
		name.setPrefWidth(100);
		steamId.setPrefWidth(150);
		
		
		actions.setResizable(false);
		actions.setPrefWidth(80);
		
		this.getColumns().add(id);
		this.getColumns().add(name);
		this.getColumns().add(steamId);
		this.getColumns().add(actions);

		
		this.players = players;
		

		this.setItems(this.players);

	}
	
}
