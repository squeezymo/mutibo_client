/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.squeezymo.mutibo.client;

public class SecuredRestException extends RuntimeException {
    public static final int DEFAULT = 0;
    public static final int AUTHENTICATION_FAILURE = 1;
    public static final int CREDENTIAL_MISSING = 2;
    public static final int USER_EXISTS = 3;
    public static final int NO_CONNECTION = 4;

    private int mExceptionType;

	public SecuredRestException(int exceptionType) {
        super();
        mExceptionType = exceptionType;
	}

	public SecuredRestException(String message, Throwable cause, int exceptionType) {
		super(message, cause);
        mExceptionType = exceptionType;
	}

	public SecuredRestException(String message, int exceptionType) {
        super(message);
        mExceptionType = exceptionType;
	}

	public SecuredRestException(Throwable cause, int exceptionType) {
		super(cause);
        mExceptionType = exceptionType;
	}

    public int getExceptionType() {
        return mExceptionType;
    }
}
