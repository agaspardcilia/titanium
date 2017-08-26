package screach.titanium.core.wsp;

import screach.titanium.core.server.RCONServerException;

public class WebApiException extends RCONServerException  {
	private static final long serialVersionUID = 599046660429131442L;

	private int code;
	private String errorMessage;
	
	public WebApiException(int code, String errorMessage) {
		super(code + " : " + errorMessage);

		this.code = code;
		this.errorMessage = errorMessage;
	}
	
	
	public int getCode() {
		return code;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
