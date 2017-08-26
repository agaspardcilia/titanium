package screach.titanium.core.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.ServerInformationRefresher;
import screach.titanium.core.cmdparser.SimpleAnswerParser;
import screach.titanium.core.factories.ParserFactory;
import screach.titanium.core.wsp.Organization;
import screach.titanium.core.wsp.WebApiException;
import screach.titanium.core.wsp.WebServiceProvider;
import utils.ErrorUtils;
import utils.webapi.HttpException;
import utils.webapi.WebApi;

public class WSPServer extends Server {
	public final static int DEFAULT_TIMEOUT = 1000;

	private Organization organization;
	private WebApi apiProvider;
	private int serverId;
	private String name;

	private boolean isConnected;

	private SimpleAnswerParser answerParser;

	private ServerInformationRefresher refresher;
	private Thread refresherThread;

	public WSPServer(Organization organization, int serverId, String serverName, String address, int port) {
		super(organization.getName() + ":" + serverName, address, port);
		this.name = serverName;
		this.organization = organization;
		this.serverId = serverId;
		apiProvider = organization.getWsp().getApi();

		isConnected = false;

		answerParser = ParserFactory.newSimpleAnswerParser(this);

	}

	@Override
	public void executeCommand(String command) throws WebApiException {
		String raw = "";
		Map<String, String> args = new HashMap<>();

		args.put("key", getKey());
		args.put("idserver", serverId+ "");
		args.put("command", command);
		args.put("timeout", DEFAULT_TIMEOUT + "");

		System.out.println("executing " + command);

		try {
			raw = apiProvider.sendGetRequest("servers/exec", args);
			JSONObject answer = new JSONObject(raw);
			if (ErrorUtils.isSuccessful(answer)) {
				answerParser.parseAnswer(answer.getString("answer"));
			} else {
				if (answer.has("errorCode")) {
					throw new WebApiException(answer.getInt("errorCode"), answer.getString("errorMessage"));
				} else {
					throw new WebApiException(-1, "Unkown error");
				}
			}

		} catch (IOException | HttpException e) {
			System.out.println(e.getClass());
			System.out.println("Execution failed : " + e.getMessage());
			System.out.println("raw : ");
			System.out.println(raw);

		} catch(JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("raw : ");
			System.out.println(raw);
		}

	}


	public boolean checkConnection() throws WebApiException, UnableToConnectException {
		String raw = "";
		Map<String, String> args = new HashMap<>();

		args.put("key", getKey());
		args.put("idserver", serverId+ "");

		try {
			raw = apiProvider.sendGetRequest(WebServiceProvider.API_PATH_CHECK_SERVERS, args);
		} catch (IOException | HttpException e) {
			throw new UnableToConnectException(e.getClass() + " : " + e.getMessage());
		}
		JSONObject answer = new JSONObject(raw);

		if (ErrorUtils.isSuccessful(answer)) {
			return true;
		} else {
			throw ErrorUtils.parseError(answer);
		}
	}

	private String getKey() {
		return organization.getWsp().getKey();
	}

	public int getServerId() {
		return serverId;
	}

	public String getWSPServerName() {
		return name;
	}

	@Override
	public void connect() throws WebApiException, UnableToConnectException {
		try {
			if (!checkConnection()) {
				isConnected = false;
			} else {
				isConnected = true;
				refresher = new ServerInformationRefresher(this);
				refresherThread = new Thread(refresher);
				refresherThread.start();
			}
		} catch (Exception e) {
			isConnected = false;
			throw e;
		}
	}

	@Override
	public void disconnect() {
		if (isConnected) {
			isConnected = false;
			refresherThread.interrupt();
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
	@Override
	public String toString() {
		return organization.getName() + " : " + getWSPServerName() + " " + getAddress() + ":" + getPort();
	}

}
