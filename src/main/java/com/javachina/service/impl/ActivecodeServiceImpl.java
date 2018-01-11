package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.javachina.model.Activecode;
import com.javachina.model.User;
import com.javachina.service.ActivecodeService;
import com.javachina.service.SendMailService;
import com.javachina.service.UserinfoService;

import blade.kit.DateKit;
import blade.kit.StringKit;

@Service
public class ActivecodeServiceImpl implements ActivecodeService {
	
	@Inject
	private SendMailService sendMailService;
	
	@Inject
	private UserinfoService userinfoService;
	
	@Override
	public Activecode getActivecode(String code) {
		if(StringKit.isBlank(code)){
			return null;
		}
		
		return AR.find("select * from t_activecode where code = ?", code).first(Activecode.class);
	}
	
	public Activecode getActivecodeById(Integer id) {
		if(null == id){
			return null;
		}
		return AR.findById(Activecode.class, id);
	}
		
	@Override
	public String save(User user, String type) {
		
		if(null == user || StringKit.isBlank(type)){
			return null;
		}
		
		int time = DateKit.getCurrentUnixTime();
		int expires_time = time + 3600;
		String code = StringKit.getRandomChar(32);
		try {
			
			AR.update("insert into t_activecode(uid, code, type, expires_time, create_time) values(?, ?, ?, ?, ?)",
					user.getUid(), code, type, expires_time, time).executeUpdate();
			
			userinfoService.save(user.getUid());
			
			if(type.equals("signup")){
				sendMailService.signup(user.getLogin_name(), user.getEmail(), code);
			}
			
			if(type.equals("forgot")){
				sendMailService.forgot(user.getLogin_name(), user.getEmail(), code);
			}
			
			return code;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean useCode(String code) {
		if(StringKit.isBlank(code)){
			return false;
		}
		try {
			AR.update("update t_activecode set is_use = ? where code = ?", 1, code).executeUpdate(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
