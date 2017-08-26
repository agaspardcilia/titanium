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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import screach.titanium.core.wsp.Member;
import screach.titanium.core.wsp.Organization;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.gui.dialogs.AddMemberDialog;
import screach.titanium.gui.org.views.MemberView;
import utils.ErrorUtils;
import utils.webapi.HttpException;

public class MemberPane extends BorderPane {
	private TableView<MemberView> memberTable;

	private GridPane buttonPane;
	private Button addMember;
	private Button removeMember;
	private Button transfertOwnership;


	private ObservableList<MemberView> members;

	private Organization organization;
	private EditOrganizationStage eos;

	public MemberPane(Organization organization, EditOrganizationStage oms) {
		super();
		this.organization = organization;
		this.eos = oms;
		
		members = FXCollections.observableArrayList();
		forceUpdateMemberList();

		memberTable = new TableView<>(members);

		TableColumn<MemberView, String> nameCol = new TableColumn<>("Name");

		nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(100);
		
		memberTable.getColumns().add(nameCol);
		
		memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		buttonPane = new GridPane();
		buttonPane.setPadding(new Insets(10, 0, 0, 0));
		buttonPane.setHgap(5);

		addMember = new Button("Add Member...");
		removeMember = new Button("Remove selected");
		transfertOwnership = new Button("Transfert Ownership");

		buttonPane.addRow(0, addMember, removeMember, transfertOwnership);

		addMember.setOnAction(this::addMemberAction);
		removeMember.setOnAction(this::removeMemberAction);
		transfertOwnership.setOnAction(this::transferOwnershipAction);

		removeMember.setDisable(true);
		transfertOwnership.setDisable(true);
		
		Label title = new Label("Members :");
		title.setPadding(new Insets(0, 0, 10, 0));
		
		setTop(title);
		setCenter(memberTable);
		setBottom(buttonPane);

		memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null || newSelection.isYou()) {
				removeMember.setDisable(true);
				transfertOwnership.setDisable(true);
			} else {
				removeMember.setDisable(false);
				transfertOwnership.setDisable(false);
			}
		});
		
	}

	private void forceUpdateMemberList() {
		members.clear();
		try {
			members.addAll(organization.getMemberList().stream().map(m -> new MemberView(m)).collect(Collectors.toList()));
		} catch (JSONException | WebApiException | IOException | HttpException e) {
			new Alert(AlertType.ERROR, "Error while fetching member list. " + e.getClass() + " : " + e.getMessage()).show();
			e.printStackTrace();
		}
	}


	public TableView<MemberView> getMemberTable() {
		return memberTable;
	}

	private void removeMemberAction(Event e) {
		MemberView selected = memberTable.selectionModelProperty().get().getSelectedItem();

		if (selected != null) { 
			Alert conf = new Alert(AlertType.CONFIRMATION, "Do you really want to transfert " + organization.getName() + "'s ownership to "+ selected.getUsername() + " ?\n");
			Optional<ButtonType> result = conf.showAndWait();

			if (result.isPresent() && result.get().equals(ButtonType.OK))  {
				try {
					organization.removeMember(selected.getId());
				} catch (JSONException | WebApiException | IOException | HttpException e1) {
					ErrorUtils.getAlertFromException(e1).show();
					forceUpdateMemberList();
					e1.printStackTrace();
				}
			}
		}
	}

	private void transferOwnershipAction(Event e) {
		MemberView selected = memberTable.selectionModelProperty().get().getSelectedItem();

		if (selected != null) {  
			Alert conf = ErrorUtils.newAlert(AlertType.CONFIRMATION, "Transfer ownership confirmation",
					"Do you really want to transfer " + organization.getName() + "'s ownership to "+ selected.getUsername() + " ?",
					"This action is definitive, be careful.");
			Optional<ButtonType> result = conf.showAndWait();

			if (result.isPresent() && result.get().equals(ButtonType.OK))  {
				try {
					organization.transfertOwnership(selected.getId());
					eos.close();
				} catch (JSONException | WebApiException | IOException | HttpException e1) {
					ErrorUtils.getAlertFromException(e1).show();
					e1.printStackTrace();
				}
			}
		}



	}

	private void addMemberAction(Event e) {
		AddMemberDialog dialog = new AddMemberDialog(organization);

		Optional<Member> result = dialog.showAndWait();

		if (result.isPresent()) {
			try {
				organization.addMember(result.get().getId());
				forceUpdateMemberList();
			} catch (JSONException | WebApiException | IOException | HttpException e1) {
				ErrorUtils.getAlertFromException(e1).show();
				e1.printStackTrace();
			}
		}


	}

}
