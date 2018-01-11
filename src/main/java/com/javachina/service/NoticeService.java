package com.javachina.service;

import java.util.List;
import java.util.Map;

public interface NoticeService {
	
	boolean save(String type, Long uid, Long to_uid, Long event_id);
	
	boolean read(Long to_uid);
	
	List<Map<String, Object>> getNoticeList(Long uid);

	Long getNotices(Long uid);
	
}
