package screach.titanium.gui.org.edit;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import screach.titanium.core.wsp.Organization;
import screach.titanium.gui.org.OrganizationManagerStage;

public class EditOrganizationStage extends Stage {
	private Pane mainPane;

	private ServerPane serverPane;
	private MemberPane memberPane;

	
	public EditOrganizationStage(Organization organization, OrganizationManagerStage oms) {
		super();
		setTitle("Manage " + organization.getName());
		
		serverPane = new ServerPane(organization, oms);

		memberPane = new MemberPane(organization, this);

		GridPane mainGP = new GridPane();
		mainGP.setPadding(new Insets(15));
		mainGP.addRow(0, serverPane, memberPane);
		mainGP.setHgap(15);
		
		mainPane = mainGP;
		
		
		
		Scene scene = new Scene(mainPane);
		this.setScene(scene);
		
	}



}







