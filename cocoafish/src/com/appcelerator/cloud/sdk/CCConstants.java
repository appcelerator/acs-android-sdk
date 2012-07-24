package com.appcelerator.cloud.sdk;

public class CCConstants {
	// URLs
	public final static String HTTP_HEAD = "http://";
	public final static String HTTPS_HEAD = "https://";
	public final static String DEFAULT_HOSTNAME = "api.cloud.appcelerator.com/v1/";
	public final static String DEFAULT_AUTH_HOST = "secure-identity.cloud.appcelerator.com";
	public final static String KEY = "?key=";
	public final static String ACCESS_TOKEN = "access_token";
	public final static String ENCODING_UTF8 = "UTF-8";

	// Cookies
	public static final String COOKIES_FILE = "CocofishCookiesFile";

	// Session
	public static final String LOGIN_METHOD = "loginUser";
	public static final String CREATE_METHOD = "createUser";
	public static final String LOGOUT_METHOD = "logoutUser";
	public static final String USERS = "users";
	public static final int SUCCESS_CODE = 200;

	// User
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String USERNAME = "username";
	public static final String EMAIL = "email";

	// Object
	public static final String ID = "id";
	public static final String UPDATED_AT = "updated_at";
	public static final String CREATED_AT = "created_at";
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	// Header
	public static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
	public static final String GZIP = "gzip";
}
