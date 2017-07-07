package screach.titanium.gui.servertab;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import screach.titanium.core.Player;
import screach.titanium.core.Server;
import screach.titanium.gui.dialogs.BanDialog;
import utils.AssetsLoader;

public class PlayerView {
	private Player player;
	private Server server;
	
	private Button kickButton;
	private Button banButton;
	private HBox actionPane;

	private Label steamIDLabel;
	private Button copyButton;
	private Button steamProfileButton;
	private HBox steamPan;
	
	private Application app;
	
	public PlayerView(Player player, Server server, Application app) {
		this.player = player;
		this.server = server;
		this.app = app;
		
		ImageView copyImage = new ImageView(AssetsLoader.getAsset("copy.png"));
		copyImage.setFitWidth(16);
		copyImage.setFitHeight(16);
		
		ImageView steamImage = new ImageView(AssetsLoader.getAsset("steam.png"));
		steamImage.setFitWidth(16);
		steamImage.setFitHeight(16);
		
		ImageView banImage = new ImageView(AssetsLoader.getAsset("ban.png"));
		banImage.setFitWidth(16);
		banImage.setFitHeight(16);
		
		ImageView kickImage = new ImageView(AssetsLoader.getAsset("kick.png"));
		kickImage.setFitWidth(16);
		kickImage.setFitHeight(16);
		
		kickButton = new Button("", kickImage);
		banButton = new Button("", banImage);
		
		kickButton.setTooltip(new Tooltip("Kick this player"));
		banButton.setTooltip(new Tooltip("Ban this player"));
		
		actionPane = new HBox(kickButton, banButton);
		kickButton.setOnAction(this::kickPlayerAction);
		banButton.setOnAction(this::banPlayerAction);

	
		steamIDLabel = new Label(player.getSteamId());
		copyButton = new Button("", copyImage);
		steamProfileButton = new Button("", steamImage);
		
		copyButton.setOnAction(this::copySteamIDToClipboardAction);
		steamProfileButton.setOnAction(this::steamProfileAction);
		
		copyButton.setTooltip(new Tooltip("Copy steamID to clipboard"));
	
		steamPan = new HBox(steamIDLabel, copyButton, steamProfileButton);
		
		
	}
	
	public String getName() {
		return player.getName();
	}
	
	public int getId() {
		return player.getId();
	}
	
	public String getSteamId() {
		return player.getSteamId();
	}
	
	public HBox getSteamPan() {
		return steamPan;
	}
	
	public Button getKickButton() {
		return kickButton;
	}
	
	public Button getBanButton() {
		return banButton;
	}
	
	public HBox getActionPan() {
		return actionPane;
	}
	
	public Server getServer() {
		return server;
	}
	
	private void kickPlayerAction(ActionEvent e) {
		TextInputDialog dial = new TextInputDialog("No reason indicated");
		dial.setTitle("Kick player");
		dial.setHeaderText("Do you really want to kick " + player.getName() + " ?");
		dial.setContentText("Reason ");
		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
			server.kickPlayer(player, result.orElse("No reason indicated"));
		}
		

	
	}
	
	private void banPlayerAction(ActionEvent e) {
		BanDialog dial = new BanDialog(player.getName(), "No reason indicated", 0);
		dial.setTitle("Ban player");
		dial.setHeaderText("Do you really want to ban " + player.getName() + " ?");
		dial.setContentText("Reason ");
		Optional<Pair<String, Integer>> result = dial.showAndWait();
		
		if (result.isPresent()) {
			Pair<String, Integer> defaultValues = new Pair<String, Integer>("No reason indicated", 0);
			
			server.banPlayer(player, result.orElse(defaultValues).getValue() + "d", result.orElse(defaultValues).getKey());
		}
		
	}
	
	private void copySteamIDToClipboardAction(ActionEvent e) {
		ClipboardContent content = new ClipboardContent();
		content.putString(getSteamId());
		Clipboard.getSystemClipboard().setContent(content);
	
	}
	
	private void steamProfileAction(ActionEvent e) {
		app.getHostServices().showDocument("http://steamcommunity.com/profiles/" + getSteamId());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player p = (Player) obj;
			
			return p.getSteamId().equals(player.getSteamId());
		} else {
			return super.equals(obj);
		}
	}
	
}
