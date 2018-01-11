package com.javachina.service;

import com.javachina.model.Openid;

public interface OpenIdService {
	
	Openid getOpenid(String type, Long open_id);
	
	boolean save(String type, Long open_id, Long uid);
	
	boolean delete(String type, Long uid);
		
}
