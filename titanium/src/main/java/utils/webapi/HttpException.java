package utils.webapi;

public class HttpException extends Exception {
	private static final long serialVersionUID = -2523765737173167475L;
	
	private int code;
	private String httpMessage;
	
	public HttpException(int code, String httpMessage) {
		super(code + " : " + httpMessage);
		
		this.code = code;
		this.httpMessage = httpMessage;
	}
	
	
	public int getCode() {
		return code;
	}
	
	public String getHttpMessage() {
		return httpMessage;
	}
}
