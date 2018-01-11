package com.javachina.service.impl;

import java.util.List;

import com.blade.context.BladeWebContext;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.blade.jdbc.QueryParam;
import com.javachina.kit.Utils;
import com.javachina.model.Userlog;
import com.javachina.service.UserlogService;

import blade.kit.DateKit;

@Service
public class UserlogServiceImpl implements UserlogService {
	
	@Override
	public List<Userlog> getUserlogList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).list(Userlog.class);
		}
		return null;
	}
	
	@Override
	public void save(final Long uid, final String action, final String content) {
		final String ip = Utils.getIpAddr(BladeWebContext.request());
		Runnable t = new Runnable() {
			@Override
			public void run() {
				try {
					AR.update("insert into t_userlog(uid, action, content, ip_addr, create_time) values(?, ?, ?, ?, ?)",
							uid, action, content, ip, DateKit.getCurrentUnixTime()).executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Utils.run(t);
	}
	
}