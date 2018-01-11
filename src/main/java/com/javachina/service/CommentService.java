package com.javachina.service;

import java.util.List;
import java.util.Map;

import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;

import com.javachina.model.Comment;

public interface CommentService {
	
	Comment getComment(Long cid);
		
	Comment getTopicLastComment(Long tid);
	
	List<Comment> getCommentList(QueryParam queryParam);
	
	Page<Map<String, Object>> getPageListMap(QueryParam queryParam);
	
	boolean save( Long uid, Long toUid, Long tid, String content, String ua);
	
	boolean delete(Long cid);
	
	Long getComments(Long uid);
		
}
