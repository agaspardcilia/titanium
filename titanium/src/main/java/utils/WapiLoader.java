package utils;

import java.io.File;
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
	
	
	public static List<WebServiceProvider> loadWapi() {
		ArrayList<WebServiceProvider> result = new ArrayList<>();
		
		try {
			JSONObject root = new JSONObject(FileUtils.readFile(new File(WAPI_PATH)));
			
			JSONArray wapis = root.getJSONArray("wapis");
			
			wapis.forEach((e) -> {
				if (e instanceof JSONObject) {
					JSONObject elem = (JSONObject) e;
					try {
						WebApi crt = new WebApi(elem.getString("protocol"), elem.getString("host"),
								elem.getInt("port"), elem.getString("path"));
						
						WebServiceProvider wsp = new WebServiceProvider(crt, elem.getString("username"),
								elem.getString("password"), elem.getString("name"));
						
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
	
}
