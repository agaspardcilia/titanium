package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class AssetsLoader {
	private final static String ASSETS_DIRECTORY_PATH = "assets/";
	
	private static Map<String, Image> assets;
	
	public static void loadAssets() {
		assets = new HashMap<>();
		
		File dir = new File(ASSETS_DIRECTORY_PATH);
		
		if (!dir.exists() || !dir.isDirectory()) {
			System.out.println("Cannot find or access assets directory");
			return;
		}
		
		InputStream crtFileStream;
		
		for (File f : dir.listFiles()) {
			try {
				crtFileStream = new FileInputStream(f);
				
				assets.put(f.getName(), new Image(crtFileStream));
				
				crtFileStream.close();	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	public static Image getAsset(String fileName) {
		if (assets == null)
			loadAssets();
		
		return assets.get(fileName);
	}
	
	
}
