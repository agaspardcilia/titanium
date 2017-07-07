package screach.titanium.core;

public class ConnectionFailureException extends Exception {
	private static final long serialVersionUID = 3698879369564085544L;
	
	public ConnectionFailureException() {
		super("Cannot connect to server");
	}
}
