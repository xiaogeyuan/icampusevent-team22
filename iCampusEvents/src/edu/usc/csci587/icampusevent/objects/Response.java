package edu.usc.csci587.icampusevent.objects;

public class Response {
	
	private String resp;
	private Object data;
	
	public String getResp() {
		return resp;
	}
	public Object getData() {
		return data;
	}
	
	public Response(String resp, Object data) {
		super();
		this.resp = resp;
		this.data = data;
	}
	
}
