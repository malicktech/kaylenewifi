package com.kaylene.wifi.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "fr";
    
    // move to config
    public final static String SMS_SENDER_NAME = "KAYLENE-Wifi";
	public final static String GET_TOKEN_URL = "https://api.orange.com/oauth/v2/token";
	public final static String GET_TOKEN_AUTHORIZATION_HEADER = "Basic cDdXcGpreDQ2QmRUa2R0WkFaRkFXdGgxMDFiak9MRmk6SDd3VkdNSEVDdnNTVUJmUA==";
	public final static String GET_TOKEN_REQUEST_BODY = "grant_type=client_credentials";
	public final static String SEND_SMS_URL = "https://api.orange.com/smsmessaging/v1/outbound/tel%3A%2B221776149662/requests";
	public final static String SMS_SENDER_ADDRESS = "tel:+221776149662";
    
    private Constants() {
    }
}
