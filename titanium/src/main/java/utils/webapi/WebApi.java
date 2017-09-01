package utils.webapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;


public class WebApi {
	private String host;
	private String basePath;
	private int port;
	private String protocol;

	/**
	 * Create a webApi wrapper to send web api request.
	 * @param protocol Used protocol. (http | https)
	 * @param host Web api provider host.
	 * @param port Web api provider host.
	 * @param basePath Api base path. "/" can be a base path. 
	 * @throws MalformedURLException
	 */
	public WebApi(String protocol, String host, int port, String basePath) throws MalformedURLException {
		this.protocol = protocol;
		this.host = host;
		this.basePath = basePath;
		this.port = port;
		
		// Keeps attributes coherence.
		if (host.endsWith("/"))
			this.host = host.substring(0, host.length()-1);
		if (!basePath.startsWith("/"))
			this.basePath = "/" + basePath;
		if (!basePath.endsWith("/"))
			this.basePath += "/";
	}

	/**
	 * Sends a GET request.
	 * @param path
	 * @param args
	 * @return Answer
	 * @throws IOException
	 * @throws HttpException
	 */
	public String sendGetRequest(String path, Map<String, String> args) throws IOException, HttpException {
		return sendRequest("GET", path, args);
	}

	/**
	 * Sends a POST request.
	 * @param path
	 * @param args
	 * @return Answer
	 * @throws IOException
	 * @throws HttpException
	 */
	public String sendPostRequest(String path, Map<String, String> args) throws IOException, HttpException {
		return sendRequest("POST", path, args);
	}

	/**
	 * Sends a http request.
	 * @param method POST, GET...
	 * @param path 
	 * @param args
	 * @return Answer
	 * @throws IOException
	 * @throws HttpException Returned code is > 299.
	 */
	private String sendRequest(String method, String path, Map<String, String> args) throws IOException, HttpException {
		if (path.startsWith("/"))
			path = path.replaceFirst("/", "");
		
		
		String urlArgs = buildArgs(args);
		
		if (method.toUpperCase().equals("GET") && !urlArgs.isEmpty()) {
			path += "?" + urlArgs;
		}
		
		
		URL url = new URL(protocol, host, port, basePath + path);
		String result = "";
		HttpURLConnection co = (HttpURLConnection) url.openConnection();

		co.setRequestMethod(method);

		
		System.out.println(url);
		
		if (method.toUpperCase().equals("POST") && !urlArgs.isEmpty()) {
			co.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(co.getOutputStream());
			wr.writeBytes(urlArgs);
			wr.flush();
			wr.close();
		}
		
		if (co.getResponseCode()  >= 299) {
			throw new HttpException(co.getResponseCode(), co.getResponseMessage());
		}
		
		Scanner sc = new Scanner(co.getInputStream());

		try {
			while (sc.hasNextLine()) {
				result += sc.nextLine();
			}

			sc.close();

			System.out.println(result);
			
			return result;
		} finally {
			sc.close();
		}
	}
	
	public URL getRootUrl() throws MalformedURLException {
		return new URL(protocol, host, port, basePath);

	}
	
	public String getHost() {
		return host;
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public int getPort() {
		return port;
	}
	
	public static String webEscape(String input) {
		StringBuilder resultStr = new StringBuilder();
		for (char ch : input.toCharArray()) {
			if (isUnsafe(ch)) {
				resultStr.append('%');
				resultStr.append(toHex(ch / 16));
				resultStr.append(toHex(ch % 16));
			} else {
				resultStr.append(ch);
			}
		}
		
		
		return resultStr.toString().replaceAll("'", "%27").replaceAll("\"", "%22"); // Shitty fix
	}

	private static char toHex(int ch) {
		return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	}

	private static boolean isUnsafe(char ch) {
		if (ch > 128 || ch < 0)
			return true;
		return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
	}
	
	
	/**
	 * {{a, b}, {c, d}} -> "a=b&c=d"
	 * Will also escape characters
	 */
	private static String buildArgs(Map<String, String> args) {
		String result = "";
		boolean isFirst = true;
		
		for (String key : args.keySet()) {
			if (!isFirst)
				result += "&";
			else 
				isFirst = false;
			
			result += key + "=" + webEscape(args.get(key));
		}
		
		return result;
	}
	
	
}
