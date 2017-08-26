package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.factories.ServerFactory;
import screach.titanium.core.server.LocalServer;

public class ServerListLoader {
	public final static String serverListPath = "./servers.json";


	public static List<LocalServer> loadServers() {
		ArrayList<LocalServer> result = new ArrayList<>();

		
		
		File f = getServerListFile();

		
		if (f.exists()) {
			try {
				JSONObject root = new JSONObject(FileUtils.readFile(f, Charset.defaultCharset()));
				JSONObject keyNode;
				
				for (String key : root.keySet()) {
					keyNode = root.getJSONObject(key);
					result.add(ServerFactory.newServer(key, keyNode.getString("host"), keyNode.getInt("port"), keyNode.getString("password")));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Can't load server list, " + serverListPath + " can't be found.");
		}

		return result;
	}


	
	
	public static JSONObject serverListToJSONObject(List<LocalServer> servers) {
		JSONObject root = new JSONObject();
		JSONObject crtNode;
		for (LocalServer s : servers) {
			crtNode = new JSONObject();
			crtNode.put("host", s.getAddress());
			crtNode.put("port", s.getPort());
			crtNode.put("password", s.getPassword());
			
			root.put(s.getName(), crtNode);
		}
		
		return root;
	}
	
	public static void writeServerList(List<LocalServer> servers) {
		JSONObject root = serverListToJSONObject(servers);
		
		File f = getServerListFile();
		try {
			PrintWriter writer = new PrintWriter(f);
			
			writer.print(root.toString());
			
			
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static File getServerListFile() {
		return new File(serverListPath);
	
	}
	
}













