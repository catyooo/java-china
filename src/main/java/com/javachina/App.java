package com.javachina;

import java.util.Map;

import com.blade.Blade;
import com.blade.Bootstrap;
import com.blade.context.BladeWebContext;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.DB;
import com.blade.jdbc.cache.memory.FIFOCache;
import com.blade.web.verify.CSRFConfig;
import com.blade.web.verify.CSRFTokenManager;
import com.bladejava.view.template.JetbrickTemplateEngine;
import com.javachina.ext.Funcs;
import com.javachina.ext.Methods;
import com.javachina.service.SettingsService;

import blade.kit.AES;
import jetbrick.template.resolver.GlobalResolver;

public class App extends Bootstrap {
	
	@Inject
	private SettingsService settingsService;
	
	@Override
	public void init(Blade blade) {
		// 模板引擎
		JetbrickTemplateEngine jetbrickTemplateEngine = new JetbrickTemplateEngine(BladeWebContext.servletContext());
		
		GlobalResolver resolver = jetbrickTemplateEngine.getJetEngine().getGlobalResolver();
		resolver.registerFunctions(Funcs.class);
		resolver.registerMethods(Methods.class);
		Constant.VIEW_CONTEXT = jetbrickTemplateEngine.getJetEngine().getGlobalContext();
		blade.viewEngin(jetbrickTemplateEngine);
		
		// 配置数据库
		DB.open(blade.config().get("jdbc.driver"), 
				blade.config().get("jdbc.url"), 
				blade.config().get("jdbc.user"), 
				blade.config().get("jdbc.pass"), true);
		
		// 开启数据库缓存
		if(blade.config().getAsBoolean("app.db_cahce")){
			DB.setCache(new FIFOCache());
		}
		
		// 初始化配置
		Constant.SITE_URL = blade.config().get("app.site_url");
		Constant.APP_VERSION = blade.config().get("app.version");
		AES.setKey(blade.config().get("app.aes_salt"));
		Constant.CDN_URL = blade.config().get("qiniu.cdn");
		Constant.FAMOUS_KEY = blade.config().get("famous.key");
		
		// csrf 防御
		CSRFConfig conf = new CSRFConfig();
		conf.setSecret(blade.config().get("app.aes_salt"));
		conf.setSetHeader(true);
		CSRFTokenManager.config(conf);
		
		// 配置邮箱
		Constant.MAIL_HOST = blade.config().get("app.mail.host");
		Constant.MAIL_NICK = blade.config().get("app.mail.nick");
		Constant.MAIL_USER = blade.config().get("app.mail.user");
		Constant.MAIL_PASS = blade.config().get("app.mail.pass");
		Constant.MAIL_ADMIN = blade.config().get("app.mail.admin");
		
		// github授权key
		Constant.GITHUB_CLIENT_ID = blade.config().get("github.CLIENT_ID");
		Constant.GITHUB_CLIENT_SECRET = blade.config().get("github.CLIENT_SECRET");
		Constant.GITHUB_REDIRECT_URL = blade.config().get("github.REDIRECT_URL");
	}
	
	@Override
	public void contextInitialized(Blade blade) {
		settingsService.refreshCount();
		Constant.SYS_INFO = settingsService.getSystemInfo();
		Constant.VIEW_CONTEXT.set(Map.class, "sys_info", Constant.SYS_INFO);
		Constant.VIEW_CONTEXT.set(String.class, "site_url", Constant.SITE_URL);
	}
}
