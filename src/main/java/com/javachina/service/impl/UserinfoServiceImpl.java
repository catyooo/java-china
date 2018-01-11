package com.javachina.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.blade.ioc.annotation.Service;
import com.javachina.model.Userinfo;
import com.javachina.service.UserinfoService;

import blade.kit.PatternKit;

@Service
public class UserinfoServiceImpl implements UserinfoService {
	
	@Override
	public Userinfo getUserinfo(Long uid) {
		return AR.findById(Userinfo.class, uid);
	}
		
	@Override
	public List<Userinfo> getUserinfoList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).list(Userinfo.class);
		}
		return null;
	}
	
	@Override
	public Page<Userinfo> getPageList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).page(Userinfo.class);
		}
		return null;
	}
	
	@Override
	public boolean save(Long uid) {
		if(null == uid){
			return false;
		}
		try {
			AR.update("insert into t_userinfo(uid) values(?)", uid).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean update(Long uid, String nickName, String jobs, String webSite, 
			String github, String weibo, String location, String signature, String instructions) {
		if(null != uid){
			StringBuffer updateSql = new StringBuffer("update t_userinfo set ");
			List<Object> params = new ArrayList<Object>();
			if(null != nickName){
				updateSql.append("nick_name = ?, ");
				params.add(nickName);
			}
			if(null != jobs){
				updateSql.append("jobs = ?, ");
				params.add(jobs);
			}
			if(null != webSite){
				updateSql.append("web_site = ?, ");
				params.add(webSite);
			}
			if(null != github){
				if(github.equals("") || PatternKit.isStudentNum(github)){
					updateSql.append("github = ?, ");
					params.add(github);
				}
			}
			if(null != weibo){
				if(weibo.equals("") || PatternKit.isStudentNum(weibo)){
					updateSql.append("weibo = ?, ");
					params.add(weibo);
				}
			}
			if(null != location){
				updateSql.append("location = ?, ");
				params.add(location);
			}
			if(null != signature){
				updateSql.append("signature = ?, ");
				params.add(signature);
			}
			if(null != instructions){
				updateSql.append("instructions = ?, ");
				params.add(instructions);
			}
			if(params.size() > 0){
				updateSql = new StringBuffer(updateSql.substring(0, updateSql.length() - 2));
				updateSql.append(" where uid = ? ");
				params.add(uid);
				AR.update(updateSql.toString(), params.toArray()).executeUpdate();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean delete(Long uid) {
		if(null != uid){
			AR.update("delete from t_userinfo where uid = ?", uid).executeUpdate();
			return true;
		}
		return false;
	}

}
