package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.wsp.WebServiceProvider;
import utils.webapi.WebApi;

public class WapiLoader {
	public final static String WAPI_PATH = "wapi.json";
	
	
	public static List<WebServiceProvider> loadWapi() throws JSONException {
		ArrayList<WebServiceProvider> result = new ArrayList<>();
		
		try {
			String raw = FileUtils.readFile(new File(WAPI_PATH), true);
			
			if (raw.length() == 0) 
				return result;
			
			JSONObject root = new JSONObject(raw);
			
			JSONArray wapis = root.getJSONArray("wapis");
			
			wapis.forEach((e) -> {
				if (e instanceof JSONObject) {
					JSONObject elem = (JSONObject) e;
					try {
						WebApi crt = new WebApi(elem.getString("protocol"), elem.getString("host"),
								elem.getInt("port"), elem.getString("path"));
						
						WebServiceProvider wsp = new WebServiceProvider(crt, elem.getString("username"),
								elem.getString("password"), elem.getString("name"), 
								elem.getBoolean("remember-username"), elem.getBoolean("remember-password"));
						
						result.add(wsp);
					} catch (MalformedURLException | JSONException e1) {
						System.out.println("Can't load wapi");
						e1.printStackTrace();
					}
				}
			});
			
		
		
		
		} catch (JSONException | IOException e) {
			System.out.println("Error while loading Web Service Provider list.");
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	public static void writeWapi(List<WebServiceProvider> wapis) throws IOException {
		JSONObject root = new JSONObject();
		
		wapis.forEach(wapi -> {
			JSONObject crt = new JSONObject();
			
			crt.put("name", wapi.getName());
			crt.put("protocol", wapi.getApi().getProtocol());
			crt.put("host", wapi.getApi().getHost());
			crt.put("path", wapi.getApi().getBasePath());
			crt.put("port", wapi.getApi().getPort());
			crt.put("remember-username", wapi.rememberUsername());
			crt.put("remember-password", wapi.rememberPassword());
			crt.put("username", (wapi.rememberUsername()) ? wapi.getUsername() : "");
			crt.put("password", (wapi.rememberPassword()) ? wapi.getPassword() : "");

			
			root.append("wapis", crt);
		});
		

		File f = new File(WAPI_PATH);
		try {
			FileUtils.WriteFile(f, root.toString());
		} catch (FileNotFoundException e) {
			f.createNewFile();
			FileUtils.WriteFile(f, root.toString());
		}
	}
	
	
}
