package com.javachina.service;

import java.util.List;

import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;

import com.javachina.model.Userinfo;

public interface UserinfoService {
	
	Userinfo getUserinfo(Long uid);
	
	List<Userinfo> getUserinfoList(QueryParam queryParam);
	
	Page<Userinfo> getPageList(QueryParam queryParam);
	
	boolean save(Long uid);
	
	boolean update(Long uid, String nickName, String jobs, String webSite, String github, String weibo, String location, String signature, String instructions );
	
	boolean delete(Long uid);
		
}
