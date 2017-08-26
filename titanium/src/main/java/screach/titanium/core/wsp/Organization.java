package screach.titanium.core.wsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import screach.titanium.core.server.LocalServer;
import screach.titanium.core.server.WSPServer;
import utils.ErrorUtils;
import utils.webapi.HttpException;

public class Organization {
	private WebServiceProvider wsp;

	private String name;
	private int id;
	private int ownerId;
	
	private List<WSPServer> servers;
	
	// TODO refactor command execution.
	
	public Organization(WebServiceProvider wsp, int id, String name, int ownerId) {
		this.wsp = wsp;
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		
		servers = new ArrayList<>();
	}
	
	public void editServer(int serverId, LocalServer server) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		args.put(WebServiceProvider.API_ARG_NAME, server.getName());
		args.put(WebServiceProvider.API_ARG_HOST, server.getAddress());
		args.put(WebServiceProvider.API_ARG_PORT, server.getPort()+"");
		args.put(WebServiceProvider.API_ARG_PASSWORD, server.getPassword());
		args.put(WebServiceProvider.API_ARG_IDSERVER, serverId+"");
		
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_EDIT_SERVERS, args, true);
	
		if (!(answer.has(WebServiceProvider.API_ARG_SUCCESS) && answer.getBoolean(WebServiceProvider.API_ARG_SUCCESS))) {
			throw ErrorUtils.parseError(answer);
		}
		
		wsp.updateServerList();
	}
	
	public void addServer(LocalServer server) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		args.put(WebServiceProvider.API_ARG_NAME, server.getName());
		args.put(WebServiceProvider.API_ARG_HOST, server.getAddress());
		args.put(WebServiceProvider.API_ARG_PORT, server.getPort()+"");
		args.put(WebServiceProvider.API_ARG_PASSWORD, server.getPassword());
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_ADD_SERVERS, args, true);
	
		if (!(answer.has(WebServiceProvider.API_ARG_SUCCESS) && answer.getBoolean(WebServiceProvider.API_ARG_SUCCESS))) {
			throw ErrorUtils.parseError(answer);
		}
		
		wsp.updateServerList();
	}
	
	public void removeServer(WSPServer server) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDSERVER, server.getServerId()+"");
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_REMOVE_SERVERS, args, true);
		
		if (!(answer.has(WebServiceProvider.API_ARG_SUCCESS) && answer.getBoolean(WebServiceProvider.API_ARG_SUCCESS))) {
			throw ErrorUtils.parseError(answer);
		}
		
		wsp.updateServerList();
	}
	
	
	public List<Member> getMemberList() throws JSONException, IOException, HttpException, WebApiException {
		List<Member> result = new ArrayList<>();

		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_LIST_MEMBERS, args, true);
		
		if (!(answer.has(WebServiceProvider.API_ARG_SUCCESS) && answer.getBoolean(WebServiceProvider.API_ARG_SUCCESS))) {
			throw ErrorUtils.parseError(answer);
		}
		
		answer.getJSONArray(WebServiceProvider.API_ARG_MEMBERS).forEach(e -> {
			JSONObject crt = (JSONObject) e;
			String username = crt.getString(WebServiceProvider.API_ARG_USERNAME);
			int id = crt.getInt(WebServiceProvider.API_ARG_ID);
			
			
			result.add(new Member(id, username, id == wsp.getUserId()));
		});
		
		
		return result;
	}
	
	public void addMember(int userId) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		args.put(WebServiceProvider.API_ARG_IDUSER, userId+"");
		
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_ADD_MEMBERS, args, true);
		
		if (!ErrorUtils.isSuccessful(answer)) {
			throw ErrorUtils.parseError(answer);
		}
		
		ownerId = userId;
	}
	
	public void removeMember(int userId) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		args.put(WebServiceProvider.API_ARG_IDUSER, userId+"");
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_REMOVE_MEMBERS, args, true);
		
		if (!ErrorUtils.isSuccessful(answer)) {
			throw ErrorUtils.parseError(answer);
		}
		
	}
	
	public void transfertOwnership(int userId) throws JSONException, IOException, HttpException, WebApiException {
		Map<String, String> args = new HashMap<>();
		args.put(WebServiceProvider.API_ARG_IDORGA, id+"");
		args.put(WebServiceProvider.API_ARG_IDUSER, userId+"");
		
		
		JSONObject answer = wsp.sendRequest(WebServiceProvider.API_PATH_TRANSFER_ORG, args, true);
		
		if (!ErrorUtils.isSuccessful(answer)) {
			throw ErrorUtils.parseError(answer);
		}
	}
	
	public List<WSPServer> getServers() {
		return servers;
	}
	
	public void setServers(List<WSPServer> servers) {
		this.servers = servers;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public int getOwnerID() {
		return ownerId;
	}
	
	public WebServiceProvider getWsp() {
		return wsp;
	}
	
}
