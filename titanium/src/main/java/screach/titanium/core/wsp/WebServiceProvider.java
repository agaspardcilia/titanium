package screach.titanium.core.wsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.server.WSPServer;
import utils.ErrorUtils;
import utils.WapiLoader;
import utils.webapi.HttpException;
import utils.webapi.WebApi;

public class WebServiceProvider {
	public final static String API_ARG_KEY = "key";
	public final static String API_ARG_IDORGA = "idorga";
	public final static String API_ARG_USERNAME = "username";
	public final static String API_ARG_PASSWORD = "password";
	public final static String API_ARG_ID = "id";
	public final static String API_ARG_IDUSER = "iduser";
	public final static String API_ARG_IDSERVER = "idserver";
	public final static String API_ARG_NAME = "name";
	public final static String API_ARG_OWNER = "owner";
	public final static String API_ARG_ORGANIZATIONS = "organizations";
	public final static String API_ARG_ADDRESS = "address";
	public final static String API_ARG_SERVERS = "servers";
	public final static String API_ARG_HOST = "host";
	public final static String API_ARG_PORT = "port";
	public final static String API_ARG_SUCCESS = "success";
	public final static String API_ARG_MEMBERS = "members";
	public final static String API_ARG_QUERY = "query";
	public final static String API_ARG_PAGE = "page";
	public final static String API_ARG_SIZE = "size";
	public final static String API_ARG_RESULT = "result";
	public final static String API_ARG_USERS = "users";

	public final static String API_PATH_LOGIN = "user/login";
	public final static String API_PATH_SEARCH_MEMBERS = "user/search";

	public final static String API_PATH_LIST_ORGS = "org/list";
	public final static String API_PATH_CREATE_ORG = "org/create";
	public final static String API_PATH_REMOVE_ORG = "org/remove";
	public final static String API_PATH_TRANSFER_ORG = "org/transfer";
	public final static String API_PATH_LIST_MEMBERS = "org/members/list";
	public final static String API_PATH_ADD_MEMBERS = "org/members/add";
	public final static String API_PATH_REMOVE_MEMBERS = "org/members/remove";

	public final static String API_PATH_EDIT_SERVERS = "servers/edit";
	public final static String API_PATH_LIST_SERVERS = "servers/list";
	public final static String API_PATH_ADD_SERVERS = "servers/add";
	public final static String API_PATH_REMOVE_SERVERS = "servers/remove";
	public final static String API_PATH_CHECK_SERVERS = "servers/check";

	public final static int API_ERROR_WRONG_SIGNIN_INFO = 1001;

	private String name;
	private WebApi api;
	private String username;
	private String password;
	private String key;
	private int userId;
	private List<Organization> organizations;
	
	private boolean connected;
	private boolean rememberUsername;
	private boolean rememberPassword;

	public WebServiceProvider(WebApi api, String username, String password, String name, boolean rememberUsername, boolean rememberPassword) {
		this.name = name;
		this.api = api;
		this.username = username;
		this.password = password;
		this.rememberPassword = rememberPassword;
		this.rememberUsername = rememberUsername;
			
		connected = false;		
		userId = -1;

		organizations = new ArrayList<>();
	}
	
	public WebServiceProvider(WebApi api, String name) {
		this(api, "", "", name, false, false);
	}

	public void connect() throws IOException, HttpException, WebApiException {
		HashMap<String, String> param = new HashMap<>();

		param.put(API_ARG_USERNAME, username);
		param.put(API_ARG_PASSWORD, password);

		String rawAnser = api.sendGetRequest(API_PATH_LOGIN, param);

		JSONObject root = new JSONObject(rawAnser);

		if (ErrorUtils.isSuccessful(root)) {
			key = root.getString(API_ARG_KEY);
			userId = root.getInt(API_ARG_IDUSER);
			System.out.println("Connection to wapi " + name + " is successful."); // XXX
			connected = true;
		} else {
			System.out.println("Connection to wapi " + name + " has failed.");
			throw ErrorUtils.parseError(root);
		}

	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public List<Organization> fetchOrganizationList() throws IOException, HttpException, WebApiException {
		List<Organization> result = new ArrayList<>();
		Map<String, String> args = new HashMap<>();

		args.put(API_ARG_KEY, key);


		JSONObject answer = new JSONObject(api.sendGetRequest(API_PATH_LIST_ORGS, args));

		if (ErrorUtils.isSuccessful(answer)) {
			JSONArray orga = answer.getJSONArray(API_ARG_ORGANIZATIONS);

			orga.forEach(o -> {
				if (o instanceof JSONObject) {
					JSONObject crt = (JSONObject) o;

					result.add(new Organization(this, crt.getInt(API_ARG_ID), crt.getString(API_ARG_NAME), crt.getInt(API_ARG_OWNER)));
				}
			});

		} else {
			throw ErrorUtils.parseError(answer);
		}

		return result;
	}

	public void fecthAndSetOrganization() throws WebApiException, IOException, HttpException {
		List<Organization> newOnes = fetchOrganizationList();

		organizations.retainAll(newOnes);

		newOnes.forEach(o -> {
			if (!organizations.contains(o))
				organizations.add(o);
		});
	}

	public void updateServerList() {
		organizations.forEach(orga -> {
			List<WSPServer> servers = new ArrayList<>();

			Map<String, String> args = new HashMap<>();
			args.put(API_ARG_KEY, key);
			args.put(API_ARG_IDORGA, orga.getId()+"");

			try {
				JSONObject answer = new JSONObject(api.sendGetRequest(API_PATH_LIST_SERVERS, args));

				if (ErrorUtils.isSuccessful(answer)) {
					JSONArray serverList = answer.getJSONArray(API_ARG_SERVERS);

					serverList.forEach(node -> {
						if (node instanceof JSONObject) {
							JSONObject jsonNode = (JSONObject) node;
							System.out.println("node : ");
							System.out.println(jsonNode);
							String[] split = jsonNode.getString(API_ARG_ADDRESS).split(":"); // XXX Too lazy way to do it.
							String address = split[0];
							int port = Integer.parseInt(split[1]);

							WSPServer s = new WSPServer(orga, jsonNode.getInt(API_ARG_ID), jsonNode.getString(API_ARG_NAME), address, port);
							servers.add(s);
						}
					});

					orga.setServers(servers);

				} else {
					System.out.println(ErrorUtils.parseError(answer)); // XXX
				}



			} catch (IOException | HttpException e) {
				// TODO handle this.
				e.printStackTrace();
			}
		});
	}

	public JSONObject sendRequest(String path, Map<String, String> args, boolean includeKey) throws JSONException, IOException, HttpException {
		JSONObject answer;
		Map<String, String> realArgs;

		if (includeKey) {
			realArgs = new HashMap<>();
			realArgs.putAll(args);
			realArgs.put(API_ARG_KEY, key);

		} else {
			realArgs = args;
		}

		answer = new JSONObject(api.sendGetRequest(path, realArgs));

		return answer;
	}

	public List<Member> searchMembers(String query, int page, int size) throws JSONException, IOException, HttpException, WebApiException {
		List<Member> result = new ArrayList<>();

		Map<String, String> args = new HashMap<>();

		args.put(API_ARG_QUERY, query);
		args.put(API_ARG_PAGE, page+"");
		args.put(API_ARG_SIZE, size+"");

		JSONObject answer = sendRequest(API_PATH_SEARCH_MEMBERS, args, true);

		if (ErrorUtils.isSuccessful(answer)) {
			answer.getJSONObject(API_ARG_RESULT).getJSONArray(API_ARG_USERS).forEach(e -> {
				JSONObject crt = (JSONObject) e;
				String username = crt.getString(WebServiceProvider.API_ARG_USERNAME);
				int id = crt.getInt(WebServiceProvider.API_ARG_ID);

				result.add(new Member(id, username, id == userId));
			});
		} else {
			throw ErrorUtils.parseError(answer);
		}

		return result;
	}

	public void newOrganization(String name) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();

		args.put(API_ARG_NAME, name);
		
		JSONObject answer = sendRequest(API_PATH_CREATE_ORG, args, true);
		
		if (!ErrorUtils.isSuccessful(answer)) {
			throw ErrorUtils.parseError(answer);
		}
			
	}

	public void removeOrganization(Organization toRemove) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();

		args.put(API_ARG_IDORGA, toRemove.getId()+"");

		JSONObject answer = sendRequest(API_PATH_REMOVE_ORG, args, true);

		if (ErrorUtils.isSuccessful(answer)) {
			organizations.remove(toRemove);
		} else {
			throw ErrorUtils.parseError(answer);
		}
	}

	public List<WSPServer> getAllAvailableServers() {
		List<WSPServer> result = new ArrayList<>();

		organizations.forEach(org -> {
			result.addAll(org.getServers());
		});

		return result;
	}

	public String getName() {
		return name;
	}

	public WebApi getApi() {
		return api;
	}

	public String getKey() {
		return key; // TODO
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public int getUserId() {
		return userId;
	}
	
	public boolean rememberUsername() {
		return rememberUsername;
	}
	
	public boolean rememberPassword() {
		return rememberPassword;
	}
	
	public void setRememberPassword(boolean rememberPassword) {
		this.rememberPassword = rememberPassword;
	}
	
	public void setRememberUsername(boolean rememberUsername) {
		this.rememberUsername = rememberUsername;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}



}
