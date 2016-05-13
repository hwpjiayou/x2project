package com.renren.mobile.x2.network.talk.messagecenter;

public class LoginErrorException extends ConnectionException {
	private static final long serialVersionUID = 7702052136720425360L;
	
	public int location;

	public LoginErrorException(int location) {
		super();
		this.location = location;
	}
	
	public LoginErrorException(int location, int exceptionDetail) {
		super(exceptionDetail);
		this.location = location;
	}
}
