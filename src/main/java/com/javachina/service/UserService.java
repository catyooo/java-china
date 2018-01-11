package com.javachina.service;

import java.util.List;
import java.util.Map;

import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.javachina.model.LoginUser;
import com.javachina.model.User;

public interface UserService {
	
	User getUser(Long uid);
	
	User getUser(String user_name);
	
	User getUser(QueryParam queryParam);
	
	Map<String, Object> getUserDetail(Long uid);
	
	List<User> getUserList(QueryParam queryParam);
	
	Page<User> getPageList(QueryParam queryParam);
	
	User signup(String loginName, String passWord, String email);
	
	User signin(String loginName, String passWord);
	
	LoginUser getLoginUser(User user, Long uid);
	
	boolean hasUser(String login_name);
	
	boolean delete(Long uid);
	
	boolean updateStatus(Long uid, Integer status);
	
	boolean resetPwd(String email);

	boolean updateAvatar(Long uid, String avatar_path);

	boolean updatePwd(Long uid, String new_pwd);

	boolean updateRole(Long uid, Integer role_id);
	
}
