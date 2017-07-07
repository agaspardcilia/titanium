package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapListLoader {
	private final static String MAP_LIST_PATH = "maps.ini";
	
	private static List<String> mapList;
	
	public static List<String> loadMapList() {
		ArrayList<String> result = new ArrayList<>();
		
		File f = new File(MAP_LIST_PATH);
		try {
			Scanner sc = new Scanner(f);
		
			while (sc.hasNextLine()) {
				result.add(sc.nextLine());
			}
			
			sc.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		mapList = new ArrayList<>(result);
		
		return result;
	}
	
	public List<String> getMapList() {
		if (mapList == null)
			loadMapList();
		
		return mapList;
	}
	
	
	
}
