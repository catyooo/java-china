package com.javachina.service;

import java.util.List;

import com.blade.jdbc.QueryParam;
import com.javachina.model.Link;

public interface LinkService {
	
	Link getLink(Integer id);
	
	List<Link> getLinkList(QueryParam queryParam);
	
	boolean save( String title, String url);
	
	boolean delete(Integer id);
		
}
