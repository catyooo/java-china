package com.javachina.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.AR;
import com.blade.jdbc.QueryParam;
import com.blade.route.annotation.Path;
import com.blade.route.annotation.Route;
import com.blade.view.ModelAndView;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.kit.SessionKit;
import com.javachina.model.LoginUser;
import com.javachina.model.Openid;
import com.javachina.model.User;
import com.javachina.service.OpenIdService;
import com.javachina.service.UserService;

import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSONKit;
import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

@Path("/oauth/")
public class OAuthController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthController.class);
	
	@Inject
	private UserService userService;
	
	@Inject
	private OpenIdService openIdService;
	
	/**
	 * github回调
	 */
	@Route(value = "/github")
	public void github(Request request, Response response){
		try {
			String url = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s";
			String redirect_uri = URLEncoder.encode(Constant.GITHUB_REDIRECT_URL, "utf-8");
			response.redirect(String.format(url, Constant.GITHUB_CLIENT_ID, redirect_uri, StringKit.getRandomNumber(15)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * github回调
	 */
	@Route(value = "/github/call")
	public ModelAndView githubCall(Request request, Response response){
		
		String code = request.query("code");
		
		if(StringKit.isNotBlank(code)){
			LOGGER.info("code = {}", code);
			
			String body = HttpRequest.post("https://github.com/login/oauth/access_token", true,
					"client_id" , Constant.GITHUB_CLIENT_ID,
					"client_secret" , Constant.GITHUB_CLIENT_SECRET,
					"code", code)
					.accept("application/json")
					.trustAllCerts().trustAllHosts().body();
			
			LOGGER.info("body = {}", body);
			
			JSONObject result = JSONKit.parseObject(body);
		 	String access_token = result.getString("access_token");
		 	
		 	String body_ = HttpRequest.get("https://api.github.com/user?access_token=" + access_token).body();
		 	
		 	System.out.println("body = " + body_);
		 	
		 	JSONObject user = JSONKit.parseObject(body_);
		 	Long open_id = user.getLong("id");
		 	String login = user.getString("login");
		 	
		 	// 判断用户是否已经绑定
		 	Openid openid = openIdService.getOpenid(Types.github.toString(), open_id);
		 	if(null == openid){
		 		Map<String, String> githubInfo = new HashMap<String, String>(3);
		 		githubInfo.put("login_name", login);
		 		githubInfo.put("open_id", open_id.toString());
		 		
		 		SessionKit.set(request.session(), Types.github.toString(), githubInfo);
		 		
		 		response.go("/oauth/user/bind");
		 	} else {
		 		User user_ = userService.getUser(openid.getUid());
		 		if(null == user_){
		 			request.attribute(this.INFO, "不存在该用户");
		 			return this.getView("info");
		 		}
		 		
		 		if(user_.getStatus() == 0){
		 			request.attribute(this.INFO, "该用户未激活，无法登录");
		 			return this.getView("info");
		 		}
		 		
		 		LoginUser loginUser = userService.getLoginUser(null, openid.getUid());
				SessionKit.setLoginUser(request.session(), loginUser);
				response.go("/");
			}
		} else {
			request.attribute(this.ERROR, "请求发生异常");
			return this.getView("info");
		}
	 	return null;
	}
	
	@Route(value = "/user/bind", method = HttpMethod.GET)
	public ModelAndView bindPage(Request request, Response response){
		Map<String, String> githubInfo = request.session().attribute(Types.github.toString());
		LoginUser loginUser = SessionKit.getLoginUser();
		if(null == githubInfo || null != loginUser){
			response.go("/");
			return null;
		}
		return this.getView("github");
	}
	
	/**
	 * 绑定github帐号
	 */
	@Route(value = "/user/bind", method = HttpMethod.POST)
	public void bindCheck(Request request, Response response){
		
		Map<String, String> githubInfo = request.session().attribute(Types.github.toString());
		if(null == githubInfo){
			response.go("/");
			return;
		}
		
		String type = request.query("type");
		String login_name = request.query("login_name");
		String pass_word = request.query("pass_word");
		
		if(StringKit.isBlank(type) ||
				StringKit.isBlank(login_name)||
				StringKit.isBlank(pass_word)){
			return;
		}
		
		if(type.equals("signin")){
			
			boolean hasUser = userService.hasUser(login_name);
			if(!hasUser){
				response.text("no_user");
				return;
			}
			
			User user = userService.signin(login_name, pass_word);
			if(null == user){
				response.text("pwd_error");
				return;
			}
			
			if(user.getStatus() == 0){
				response.text("no_active");
				return;
			}
			
			Long open_id = Long.valueOf(githubInfo.get("open_id"));
			boolean flag = openIdService.save(Types.github.toString(), open_id, user.getUid());
			if(flag){
				LoginUser loginUser = userService.getLoginUser(user, null);
				SessionKit.setLoginUser(request.session(), loginUser);
				response.text(this.SUCCESS);
			}
			return;
		}
		
		if(type.equals("signup")){
			String email = request.query("email");
			if(StringKit.isBlank(email)){
				return;
			}
			
			QueryParam queryParam = QueryParam.me();
			queryParam.eq("login_name", login_name);
			queryParam.in("status", AR.in(0, 1));
			User user = userService.getUser(queryParam);
			if(null != user){
				response.text("exist_login");
				return;
			}
			
			queryParam = QueryParam.me();
			queryParam.eq("email", email);
			queryParam.in("status", 0, 1);
			user = userService.getUser(queryParam);
			if(null != user){
				response.text("exist_email");
				return;
			}
			
			User user_ = userService.signup(login_name, pass_word, email);
			if(null != user_){
				Long open_id = Long.valueOf(githubInfo.get("open_id"));
				boolean saveFlag = openIdService.save(Types.github.toString(), open_id, user_.getUid());
				if(saveFlag){
					request.session().removeAttribute(Types.github.toString());
					response.text(this.SUCCESS);
				}
			} else {
				response.text(this.FAILURE);
			}
			return;
		}
	}
	
}
