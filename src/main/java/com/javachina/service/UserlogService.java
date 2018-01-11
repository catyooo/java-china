package com.javachina.service;

import java.util.List;

import com.blade.jdbc.QueryParam;
import com.javachina.model.Userlog;

public interface UserlogService {
	
	List<Userlog> getUserlogList(QueryParam queryParam);
	
	void save(Long uid, String action, String content);
	
}
