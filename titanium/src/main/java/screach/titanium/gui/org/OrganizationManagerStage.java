package screach.titanium.gui.org;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import screach.titanium.App;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.core.wsp.WebServiceProvider;
import screach.titanium.gui.org.edit.EditOrganizationStage;
import screach.titanium.gui.org.views.OrganizationView;
import utils.ErrorUtils;
import utils.webapi.HttpException;

public class OrganizationManagerStage extends Stage {
	private WebServiceProvider wsp;

	private ObservableList<OrganizationView> organizationList;

	private Scene mainScene;
	private Pane mainPane;

	private TableView<OrganizationView> orgas;

	private Button manageButton;
	private Button leaveButton;
	private Button removeButton;
	private Button newButton;


	public OrganizationManagerStage(WebServiceProvider wsp) {
		super();
		this.setTitle("Organization Manager");

		this.wsp = wsp;

		organizationList = FXCollections.observableArrayList();

		forceOrganizationListRefresh();
		

		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(15));

		mainPane = borderPane;

		mainScene = new Scene(mainPane);
		this.setScene(mainScene);

		orgas = createOrganizationTable(organizationList);
		orgas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


		setupButtons();
		GridPane buttonPane = new GridPane();
		buttonPane.setHgap(5);

		buttonPane.add(removeButton, 0, 0);
		buttonPane.add(leaveButton, 1, 0);
		buttonPane.add(manageButton, 2, 0);
		buttonPane.add(newButton, 3, 0);

		buttonPane.setPadding(new Insets(10, 0, 0, 0));

		Label title = new Label("Organization list :");
		title.setPadding(new Insets(0, 0, 10, 0));

		borderPane.setTop(title);
		borderPane.setCenter(orgas);
		borderPane.setBottom(buttonPane);

		// TODO : Change this. Awful way to do it.
		orgas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null) {
				manageButton.setDisable(true);
				removeButton.setDisable(true);
				leaveButton.setDisable(true);
			} else if (!newSelection.isOwned()) { // Not owned
				manageButton.setDisable(true);
				removeButton.setDisable(true);

				leaveButton.setDisable(false);
			} else {
				manageButton.setDisable(false);
				removeButton.setDisable(false);

				leaveButton.setDisable(true);
			}
		});
	}

	public void forceOrganizationListRefresh() {
		organizationList.clear();
		organizationList.addAll(wsp.getOrganizations().stream().map(e -> new OrganizationView(wsp, e)).collect(Collectors.toList()));
	}

	private TableView<OrganizationView> createOrganizationTable(ObservableList<OrganizationView> orgas) {
		TableView<OrganizationView> result = new TableView<>(orgas);

		TableColumn<OrganizationView, String> name = new TableColumn<>("Name");
		TableColumn<OrganizationView, String> owned = new TableColumn<>("Owned");
		TableColumn<OrganizationView, Integer> serverCount = new TableColumn<>("Servers");

		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		serverCount.setCellValueFactory(new PropertyValueFactory<>("serverCount"));
		owned.setCellValueFactory(new PropertyValueFactory<>("isOwned"));
		
		serverCount.setMinWidth(70);
		serverCount.setMaxWidth(70);
		owned.setMaxWidth(70);
		owned.setMinWidth(70);
		
		result.getColumns().add(name);
		result.getColumns().add(serverCount);
		result.getColumns().add(owned);

		return result;
	}

	private void setupButtons() {
		System.out.println("Button setup");
		removeButton = new Button("Delete selected");
		manageButton = new Button("Manage selected...");
		leaveButton = new Button("Leave selected");
		newButton = new Button("New...");

		manageButton.setDisable(true);
		leaveButton.setDisable(true);
		removeButton.setDisable(true);

		manageButton.setOnAction(this::manageAction);
		removeButton.setOnAction(this::removeAction);
		leaveButton.setOnAction(this::leaveAction);
		newButton.setOnAction(this::newAction);
	}

	private void manageAction(Event e) {
		new EditOrganizationStage(orgas.getSelectionModel().getSelectedItem().getOrganization(), this).show();
	}

	private void leaveAction(Event e) {
		// TODO
		//App.getCurrentInstance().refreshWSPTabs();
	}

	private void removeAction(Event e) {
		Alert conf = new Alert(AlertType.CONFIRMATION, "Do you really want to delete this organization ?\n"
				+ "Every server and member association will be lost.");

		Optional<ButtonType> result = conf.showAndWait();

		if (result.isPresent() && result.get().equals(ButtonType.OK)) {
			try {
				wsp.removeOrganization(orgas.selectionModelProperty().get().getSelectedItem().getOrganization());
				forceOrganizationListRefresh();
				App.getCurrentInstance().refreshWSPTabs();
			} catch(Exception e1) {
				ErrorUtils.getAlertFromException(e1).show();
			}
		}
	}

	private void newAction(Event e) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New organization");
		dialog.setHeaderText("Create a new organization");
		dialog.setContentText("Organization name");
		
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent() && result.get().length() > 0) {
			System.out.println("present");
			if (result.get().length() <= 3) {
				new Alert(AlertType.ERROR, "The name is too short.").show();;
			} else {
				try {
					wsp.newOrganization(result.get());
					wsp.fecthAndSetOrganization();
					forceOrganizationListRefresh();
					App.getCurrentInstance().refreshWSPTabs();
				} catch (JSONException | IOException | HttpException | WebApiException e1) {
					ErrorUtils.getAlertFromException(e1).show();
				}
			}
		} else {
			System.out.println("not present");
		}
		
		System.out.println(result);
		
	}

}





