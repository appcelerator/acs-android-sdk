package com.appcelerator.cloud.sdk;

/**
 * Encapsulation of a Cocoafish Error: a Cocoafish request that could not be
 * fulfilled.
 * 
 */
public class CocoafishError extends Throwable {

	private static final long serialVersionUID = 1L;

	private int mErrorCode = 0;
	private String mErrorType;
	
	public CocoafishError(String message) {
		super(message);
	}

	public CocoafishError(String message, int code) {
		super(message);
		mErrorCode = code;
	}

    public CocoafishError(String message, String type, int code) {
        super(message);
        mErrorType = type;
        mErrorCode = code;
    }
    
	public int getErrorCode() {
		return mErrorCode;
	}

    public String getErrorType() {
        return mErrorType;
    }
}
