package screach.titanium.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoadingStage extends Stage {
	private final static int WIDTH = 500;
	private final static int HEIGHT = 100;
	
	private ProgressBar progressBar;
	private Label notice;
	
	public LoadingStage(String title, String  noticeText) {
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		this.setTitle(title);
		progressBar = new ProgressBar();
		this.notice = new Label(noticeText);
		BorderPane mainPane = new BorderPane();
		Scene s = new Scene(mainPane);
		
		progressBar.setPrefWidth(WIDTH - 25);
		
		mainPane.setTop(notice);
		mainPane.setCenter(progressBar);
		
		mainPane.setPadding(new Insets(15));
		
		this.setScene(s);
	}
	
	public void setNotice(String notice) {
		this.notice.setText(notice);
	}
	
	public void setProgress(double progress) {
		progressBar.setProgress(progress);
	}
}
