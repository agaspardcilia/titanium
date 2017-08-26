package screach.titanium.gui.org.edit;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import screach.titanium.App;
import screach.titanium.core.server.LocalServer;
import screach.titanium.core.wsp.Organization;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.gui.dialogs.AddServerDialog;
import screach.titanium.gui.dialogs.EditServerDialog;
import screach.titanium.gui.org.OrganizationManagerStage;
import screach.titanium.gui.org.views.ServerView;
import utils.ErrorUtils;
import utils.webapi.HttpException;

public class ServerPane extends BorderPane {
	private TableView<ServerView> serverTable;
	
	
	private GridPane buttonPane;
	private Button addButton;
	private Button editButton;
	private Button removeButton;
	
	private Organization organization;
	
	private OrganizationManagerStage oms;
	
	private ObservableList<ServerView> servers;
	
	public ServerPane(Organization organization, OrganizationManagerStage oms) {
		super();
		this.organization = organization;
		this.oms = oms;
		
		servers = FXCollections.observableArrayList();

		forceUpdateServerList();
		
		serverTable = new TableView<>(servers);
		serverTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<ServerView, String> nameCol = new TableColumn<>("Name");
		TableColumn<ServerView, String> addressCol = new TableColumn<>("Address");

		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

		serverTable.getColumns().add(nameCol);
		serverTable.getColumns().add(addressCol);
		
		buttonPane = new GridPane();
		buttonPane.setPadding(new Insets(10, 0, 0, 0));
		buttonPane.setHgap(5);
		
		addButton = new Button("Add Server...");
		editButton = new Button("Edit selected...");
		removeButton = new Button("Remove selected");
		
		addButton.setOnAction(this::addServerAction);
		editButton.setOnAction(this::editServerAction);
		removeButton.setOnAction(this::removeServerAction);
		
		editButton.setDisable(true);
		removeButton.setDisable(true);
		
		buttonPane.addRow(0, addButton, editButton, removeButton);
		
		Label title = new Label("Servers :");
		title.setPadding(new Insets(0, 0, 10, 0));
		
		setTop(title);
		setCenter(serverTable);
		setBottom(buttonPane);
		
		serverTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null) {
				editButton.setDisable(true);
				removeButton.setDisable(true);
			} else {
				editButton.setDisable(false);
				removeButton.setDisable(false);
			}
		});
		
	}
	
	public TableView<ServerView> getServerTable() {
		return serverTable;
	}
	
	private void forceUpdateServerList() {
		servers.clear();
		servers.addAll(organization.getServers().stream().map(s -> new ServerView(s)).collect(Collectors.toList()));
	}
	
	private void addServerAction(Event e) {
		AddServerDialog dialog = new AddServerDialog();
		Optional<LocalServer> server = dialog.showAndWait();
		
		if (server.isPresent()) {
			try {
				organization.addServer(server.get());
				oms.forceOrganizationListRefresh();
				forceUpdateServerList();
				App.getCurrentInstance().refreshWSPTabs();
				new Alert(AlertType.INFORMATION, "The server has been added to your organization.").show();
			} catch (JSONException e1) {
				new Alert(AlertType.ERROR, "Error while parsing server answer.").show();
				e1.printStackTrace();
			} catch (WebApiException e1) {
				new Alert(AlertType.ERROR, "The server answered an error. (" + e1.getCode() + ":" + e1.getErrorMessage() + ")").show();
				e1.printStackTrace();
			} catch (IOException | HttpException e1) {
				new Alert(AlertType.ERROR, "Error while sending request to server.").show();
				e1.printStackTrace();
			}
		}
	}
	
	private void removeServerAction(Event e) {
		ServerView selectedServer = serverTable.selectionModelProperty().get().getSelectedItem();
			
		if (selectedServer == null) {
			new Alert(AlertType.ERROR, "No selected server.").show();
			return;
		}
		
		Optional<ButtonType> answer = new Alert(AlertType.CONFIRMATION, "Do you really want to remove this server ?").showAndWait();
		
		if (answer.isPresent() && answer.get().equals(ButtonType.OK)) {
			try {
				organization.removeServer(selectedServer.getServer());
				oms.forceOrganizationListRefresh();
				forceUpdateServerList();
				App.getCurrentInstance().refreshWSPTabs();
				new Alert(AlertType.INFORMATION, "The server has been removed from your organization.").show();
			}  catch(Exception e1) {
				ErrorUtils.getAlertFromException(e1).show();
			}
		}
		
	}
	
	private void editServerAction(Event e) {
		ServerView selectedServer = serverTable.selectionModelProperty().get().getSelectedItem();
		
		if (selectedServer == null) {
			new Alert(AlertType.ERROR, "No selected server.").show();
			return;
		}
		
		EditServerDialog dialog = new EditServerDialog(new LocalServer(selectedServer.getName(), selectedServer.getServer().getAddress(),
				selectedServer.getServer().getPort(), ""));
		
		Optional<LocalServer> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			try {
				organization.editServer(selectedServer.getServer().getServerId(), result.get());
				oms.forceOrganizationListRefresh();
				forceUpdateServerList();
				App.getCurrentInstance().refreshWSPTabs();
				serverTable.refresh();
			} catch(Exception e1) {
				ErrorUtils.getAlertFromException(e1).show();
			}
		}
		
		
		
	}
}
