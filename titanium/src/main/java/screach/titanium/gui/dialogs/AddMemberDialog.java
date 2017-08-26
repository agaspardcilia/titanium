package screach.titanium.gui.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import screach.titanium.core.wsp.Member;
import screach.titanium.core.wsp.Organization;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.gui.org.views.MemberView;
import utils.webapi.HttpException;

public class AddMemberDialog extends Dialog<Member> {
	private final static int RESULT_SIZE = 25;
	
	private ObservableList<MemberView> searchList;
	private TextField queryField;
	private Label resultCount;
	
	private Organization organization;
	
	public AddMemberDialog(Organization organization) {
		super();
		this.organization = organization;
		
		this.setTitle("Add a member");
		this.setHeaderText("Search user");

		ButtonType addButtonType = new ButtonType("Add selected", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		BorderPane mainPane = new BorderPane();
		GridPane queryPane = new GridPane();
		
		queryField = new TextField();
		Button searchButton = new Button("Search");
		searchButton.setOnAction(this::searchAction);
		queryField.setOnAction(this::searchAction);
		searchList = FXCollections.observableArrayList();
		
		TableView<MemberView> searchResult = new TableView<>(searchList);
		searchResult.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<MemberView, String> nameCol = new TableColumn<>("Name");

		nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

		searchResult.getColumns().add(nameCol);
		
		queryPane.addRow(0, queryField, searchButton);
		queryPane.setHgap(5);
		
		resultCount = new Label();
		
		queryPane.setPadding(new Insets(0, 0, 10, 0));
		resultCount.setPadding(new Insets(10, 0, 0, 0));
		
		mainPane.setTop(queryPane);
		mainPane.setCenter(searchResult);
		mainPane.setBottom(resultCount);
		
		this.getDialogPane().setContent(mainPane);

		this.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				Member result = searchResult.selectionModelProperty().get().getSelectedItem().getMember();
				Alert conf = new Alert(AlertType.CONFIRMATION, "Do you really want to add " + result.getUsername() + " to your organization ?");
				Optional<ButtonType> answer = conf.showAndWait();
				if (answer.isPresent() && answer.get() == ButtonType.OK)
					return result;
				else 
					return null;
			}
			return null;
		});
		
	}
	
	private void searchAction(Event e) {
		String query = queryField.getText();
		
		searchList.clear(); 
		try {
			List<Member> orgaMemberList = organization.getMemberList();
			ArrayList<MemberView> result = new ArrayList<>(organization.getWsp().searchMembers(query, 0, RESULT_SIZE).stream().map(m -> new MemberView(m)).collect(Collectors.toList()));
			result.forEach(mv -> {
				if (!orgaMemberList.stream().anyMatch(m -> m.getId() == mv.getId())) {
					searchList.add(mv);
				}
			});
			
			resultCount.setText(getResultCountText(searchList.size()));
			
		} catch (JSONException | IOException | HttpException | WebApiException e1) {
			new Alert(AlertType.ERROR, "Error while fetching member list. " + e1.getClass() + " : " + e1.getMessage()).show();
			e1.printStackTrace();
		}
		
	}
	
	private String getResultCountText(int size) {
		switch (size) {
		case 0:
			return "No result.";
		case 1:
			return "1 result.";
		default:
			return size + " results";
		}
	}
}
