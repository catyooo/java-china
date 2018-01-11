package com.javachina.service;

import com.javachina.model.TopicCount;

public interface TopicCountService {
	
	TopicCount getCount(Long tid);
	
	boolean update(String type, Long tid, int count);
	
	boolean save(Long tid, Integer create_time);
	
}
