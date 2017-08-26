package screach.titanium.gui.org.views;

import screach.titanium.core.wsp.Organization;
import screach.titanium.core.wsp.WebServiceProvider;

public class OrganizationView {
	private Organization organization;
	private WebServiceProvider wsp;
	
	public OrganizationView(WebServiceProvider wsp, Organization orga) {
		this.organization = orga;
		this.wsp = wsp;
	}
	
	
	public int getId() {
		return organization.getId();
	}
	
	public String getName() {
		return organization.getName();
	}
	
	public String getIsOwned() {
		return (wsp.getUserId() == organization.getOwnerID())? "Yes" : "No";
	}
	
	public int getServerCount() {
		return organization.getServers().size();
	}
	
	public boolean isOwned() {
		return wsp.getUserId() == organization.getOwnerID();
	}
	
	public Organization getOrganization() {
		return organization;
	}
	
}
