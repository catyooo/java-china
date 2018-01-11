package com.javachina.service.impl;

import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.javachina.model.TopicCount;
import com.javachina.service.TopicCountService;

import blade.kit.StringKit;

@Service
public class TopicCountServiceImpl implements TopicCountService {

	@Override
	public boolean update(String type, Long tid, int count) {
		if(StringKit.isBlank(type) || null == tid){
			return false;
		}
		TopicCount topicCount = this.getCount(tid);
		if(null != topicCount){
			String sql = String.format("update t_topiccount set %s = (%s + ?) where tid = ?", type, type);
			AR.update(sql, count, tid).executeUpdate();
		}
		return false;
	}

	@Override
	public TopicCount getCount(Long tid) {
		if(null == tid){
			return null;
		}
		return AR.findById(TopicCount.class, tid);
	}
	
	@Override
	public boolean save(Long tid, Integer create_time) {
		try {
			if(null == tid || tid < 1){
				return false;
			}
			AR.update("insert into t_topiccount(tid, views, loves, favorites, comments, sinks, create_time) values(?, ?, ?, ?, ?, ?, ?)", tid, 0, 0, 0, 0, 0, create_time).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
