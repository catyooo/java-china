package com.javachina.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.javachina.Types;
import com.javachina.model.Favorite;
import com.javachina.model.Topic;
import com.javachina.service.FavoriteService;
import com.javachina.service.NodeService;
import com.javachina.service.TopicCountService;
import com.javachina.service.TopicService;
import com.javachina.service.UserService;

import blade.kit.DateKit;
import blade.kit.StringKit;

@Service
public class FavoriteServiceImpl implements FavoriteService {

	@Inject
	private TopicService topicService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicCountService topicCountService;
	
	public Favorite getFavorite(String type,Long uid, Long event_id) {
		return AR.find(QueryParam.me().eq("type", type).eq("uid", uid).eq("event_id", event_id)).first(Favorite.class);
	}
		
	public List<Favorite> getFavoriteList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).list(Favorite.class);
		}
		return null;
	}
	
	public Page<Favorite> getPageList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).page(Favorite.class);
		}
		return null;
	}
	
	@Override
	public Integer update(String type, Long uid, Long event_id) {
		
		try {
			
			int count = 0;
			boolean isFavorite = this.isFavorite(type, uid, event_id);
			if(!isFavorite){
				AR.update("insert into t_favorite(type, uid, event_id, create_time) values(?, ?, ?, ?)", 
						type, uid, event_id, DateKit.getCurrentUnixTime()).executeUpdate();
				count = 1;
			} else {
				AR.update("delete from t_favorite where type = ? and uid = ? and event_id = ?", type, uid, event_id).executeUpdate();
				count = -1;
			}
			
			// 收藏帖子
			if(type.equals(Types.topic.toString())){
				topicCountService.update(Types.favorites.toString(), event_id, count);
				topicService.updateWeight(event_id);
			}
			
			// 帖子点赞
			if(type.equals(Types.love.toString())){
				topicCountService.update(Types.loves.toString(), event_id, count);
				topicService.updateWeight(event_id);
			}
			
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isFavorite(String type, Long uid, Long event_id) {
		if (StringKit.isBlank(type) || null == uid || null == event_id) {
			return false;
		}
		return null != this.getFavorite(type, uid, event_id);
	}

	@Override
	public Long favorites(String type, Long uid) {
		if(null != uid && StringKit.isNotBlank(type)){
			return AR.find("select count(1) from t_favorite where type = ? and uid = ?", type, uid).first(Long.class);
		}
		return 0L;
	}

	@Override
	public Page<Map<String, Object>> getMyTopics(Long uid, Integer page, Integer count) {
		if(null != uid){
			if(null == page || page < 1){
				page = 1;
			}
			
			if(null == count || count < 1){
				count = 10;
			}
			
			QueryParam queryParam = QueryParam.me();
			queryParam.eq("type", Types.topic.toString()).eq("uid", uid).orderby("id desc").page(page, count);
			Page<Favorite> faPage = this.getPageList(queryParam);
			if(null != faPage && faPage.getTotalCount() > 0){
				long totalCount = faPage.getTotalCount();
				int page_ = faPage.getPage();
				int pageSize = faPage.getPageSize();
				Page<Map<String, Object>> result = new Page<Map<String,Object>>(totalCount, page_, pageSize);
				
				List<Favorite> favorites = faPage.getResults();
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				if(null != favorites && favorites.size() > 0){
					for(Favorite favorite : favorites){
						Long tid = favorite.getEvent_id();
						Topic topic = topicService.getTopic(tid);
						Map<String, Object> topicMap = topicService.getTopicMap(topic, false);
						if(null != topicMap && !topicMap.isEmpty()){
							list.add(topicMap);
						}
					}
				}
				result.setResults(list);
				
				return result;
			}
		}
		return null;
	}

	@Override
	public Page<Map<String, Object>> getFollowing(Long uid, Integer page, Integer count) {
		if(null != uid){
			if(null == page || page < 1){
				page = 1;
			}
			if(null == count || count < 1){
				count = 10;
			}
			
			QueryParam queryParam = QueryParam.me();
			queryParam.eq("type", Types.following.toString()).eq("uid", uid).orderby("id desc").page(page, count);
			Page<Favorite> faPage = this.getPageList(queryParam);
			if(null != faPage && faPage.getTotalCount() > 0){
				long totalCount = faPage.getTotalCount();
				int page_ = faPage.getPage();
				int pageSize = faPage.getPageSize();
				Page<Map<String, Object>> result = new Page<Map<String,Object>>(totalCount, page_, pageSize);
				
				List<Favorite> favorites = faPage.getResults();
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				if(null != favorites && favorites.size() > 0){
					for(Favorite favorite : favorites){
						Long user_id = favorite.getEvent_id();
						Map<String, Object> userMap = userService.getUserDetail(user_id);
						if(null != userMap && !userMap.isEmpty()){
							list.add(userMap);
						}
					}
				}
				result.setResults(list);
				return result;
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getMyNodes(Long uid) {
		if(null != uid){
			QueryParam queryParam = QueryParam.me();
			queryParam.eq("type", Types.node.toString()).eq("uid", uid).orderby("id desc");
			List<Favorite> favorites = AR.find(queryParam).list(Favorite.class);
			if(null != favorites && favorites.size() > 0){
				List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(favorites.size());
				for(Favorite favorite : favorites){
					Long nid = favorite.getEvent_id();
					Map<String, Object> node = nodeService.getNodeDetail(null, nid);
					result.add(node);
				}
				return result;
			}
		}
		return null;
	}
	
}
