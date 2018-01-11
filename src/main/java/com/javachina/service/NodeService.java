package com.javachina.service;

import java.util.List;
import java.util.Map;

import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;

import com.javachina.model.Node;

public interface NodeService {
	
	Node getNode(Long nid);
	
	Node getNode(QueryParam queryParam);
	
	Map<String, Object> getNodeDetail(Node node, Long nid);
	
	List<Node> getNodeList(QueryParam queryParam);
	
	List<Map<String, Object>> getNodeList();
	
	Page<Map<String, Object>> getPageList(QueryParam queryParam);
	
	boolean save(Long pid, String title, String description, String slug, String node_pic);
	
	boolean delete(Long nid);
	
	boolean updateCount(Long nid, String type, int count);

	boolean update(Long nid, Long pid, String title, String description, String node_slug, String node_pic);
}