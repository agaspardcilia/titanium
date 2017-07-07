package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.Server;
import screach.titanium.core.factories.ServerFactory;

public class ServerListLoader {
	public final static String serverListPath = "./servers.json";


	public static List<Server> loadServers() {
		ArrayList<Server> result = new ArrayList<>();

		File f = new File(serverListPath);
		
		if (f.exists()) {
			try {
				JSONObject root = new JSONObject(readFile(serverListPath, Charset.defaultCharset()));
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


	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static JSONObject serverListToJSONObject(List<Server> servers) {
		JSONObject root = new JSONObject();
		JSONObject crtNode;
		for (Server s : servers) {
			crtNode = new JSONObject();
			crtNode.put("host", s.getAddress());
			crtNode.put("port", s.getPort());
			crtNode.put("password", s.getPassword());
			
			root.put(s.getName(), crtNode);
		}
		
		return root;
	}
	
	public static void writeServerList(List<Server> servers) {
		JSONObject root = serverListToJSONObject(servers);
		
		File f = new File(serverListPath);
		try {
			PrintWriter writer = new PrintWriter(f);
			
			writer.print(root.toString());
			
			
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		List<Server> servers = loadServers();
		
		System.out.println(serverListToJSONObject(servers));
		
	}
}
