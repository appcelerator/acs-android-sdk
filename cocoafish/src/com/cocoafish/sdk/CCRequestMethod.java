package com.cocoafish.sdk;

public final class CCRequestMethod {

	public final static CCRequestMethod GET = new CCRequestMethod();
	public final static CCRequestMethod POST = new CCRequestMethod();
	public final static CCRequestMethod PUT = new CCRequestMethod();
	public final static CCRequestMethod DELETE = new CCRequestMethod();
	
	private CCRequestMethod() {
		
	}
	
	public String getTypeString() {
		if(this == GET) {
			return "GET";
		}
		if(this == POST) {
			return "POST";
		}
		if(this == PUT) {
			return "PUT";
		}
		if(this == DELETE) {
			return "DELETE";
		}
		return null;
	}
}
