package screach.titanium.gui.servertab;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
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
	private HBox buttonPane;
	
	
	
	public PlayerView(Player player, Server server) {
		this.player = player;
		this.server = server;
		
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
		
		buttonPane = new HBox(kickButton, banButton);
		kickButton.setOnAction(this::kickPlayer);
		banButton.setOnAction(this::banPlayer);
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
	
	public Button getKickButton() {
		return kickButton;
	}
	
	public Button getBanButton() {
		return banButton;
	}
	
	public HBox getButtonPan() {
		return buttonPane;
	}
	
	public Server getServer() {
		return server;
	}
	
	public void kickPlayer(ActionEvent e) {
		TextInputDialog dial = new TextInputDialog("No reason indicated");
		dial.setTitle("Kick player");
		dial.setHeaderText("Do you really want to kick " + player.getName() + " ?");
		dial.setContentText("Reason ");
		Optional<String> result = dial.showAndWait();
		
		if (result.isPresent()) {
//			server.kickPlayer(player, result.orElse("No reason indicated"));
			System.out.println("reason : " + result.orElse("No reason indicated"));
		}
		

	
	}
	
	public void banPlayer(ActionEvent e) {
		BanDialog dial = new BanDialog(player.getName(), "No reason indicated", 0);
		dial.setTitle("Ban player");
		dial.setHeaderText("Do you really want to ban " + player.getName() + " ?");
		dial.setContentText("Reason ");
		Optional<Pair<String, Integer>> result = dial.showAndWait();
		
		if (result.isPresent()) {
			Pair<String, Integer> defaultValues = new Pair<String, Integer>("No reason indicated", 0);
			
			server.banPlayer(player, result.orElse(defaultValues).getValue() + "d", result.orElse(defaultValues).getKey());
			// TODO execute command on server. Don't forget to add the 'd' after the duration
			System.out.println("reason : " + result.orElse(new Pair<String, Integer>("No reason indicated", 0)));
		}
		
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
