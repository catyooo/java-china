package com.javachina;

import java.util.HashMap;
import java.util.Map;

import jetbrick.template.JetGlobalContext;

public class Constant {

	// 登录用户session key
	public static final String LOGIN_SESSION_KEY = "login_user";

	public static final String UPLOAD_FOLDER = "assets/temp";

	public static final String USER_IN_COOKIE = "SH_SIGNIN_USER";
	public static final String JC_REFERRER_COOKIE = "JC_REFERRER_COOKIE";

	// 名人名言key
	public static String FAMOUS_KEY = "";
	
	// 今日名言
	public static Map<String, Object> today_famous = new HashMap<String, Object>();
	
	public static String SITE_URL = "";
	public static String CDN_URL = "";
	public static String APP_VERSION = "";
	
	public static String GITHUB_CLIENT_ID = "";
	public static String GITHUB_CLIENT_SECRET = "";
	public static String GITHUB_REDIRECT_URL = "";
	
	public static String MAIL_HOST;
	public static String MAIL_NICK;
	public static String MAIL_USER;
	public static String MAIL_PASS;
	public static String MAIL_ADMIN;

	public static JetGlobalContext VIEW_CONTEXT = null;
	public static Map<String, Object> SYS_INFO = null;
	
}
