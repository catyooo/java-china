package com.javachina.service.impl;

import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.javachina.kit.DateKit;
import com.javachina.model.Openid;
import com.javachina.service.OpenIdService;

import blade.kit.StringKit;

@Service
public class OpenIdServiceImpl implements OpenIdService {

	@Override
	public Openid getOpenid(String type, Long open_id) {
		if(null == open_id || StringKit.isBlank(type)){
			return null;
		}
		return AR.find("select * from t_openid where open_id = ? and type = ?", open_id, type).first(Openid.class);
	}

	@Override
	public boolean save(String type, Long open_id, Long uid) {
		if(StringKit.isNotBlank(type) && null != open_id && null != uid){
			try {
				
				Long count = AR.find("select count(1) from t_openid where open_id = ? and type = ? and uid = ?", open_id, type, uid).first(Long.class);
				if(count == 0){
					AR.update("insert into t_openid(type, open_id, uid, create_time) values(?, ?, ?, ?)", type, open_id,
							uid, DateKit.getCurrentUnixTime()).executeUpdate();
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean delete(String type, Long open_id) {
		if(null != open_id && StringKit.isNotBlank(type)){
			AR.update("delete from t_openid where type = ? and open_id = ?", type, open_id).executeUpdate();
			return true;
		}
		return false;
	}

}
