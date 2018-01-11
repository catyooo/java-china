package com.javachina.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.javachina.ImageTypes;
import com.javachina.Types;
import com.javachina.kit.DateKit;
import com.javachina.kit.Utils;
import com.javachina.model.Comment;
import com.javachina.model.Node;
import com.javachina.model.Topic;
import com.javachina.model.TopicCount;
import com.javachina.model.User;
import com.javachina.service.CommentService;
import com.javachina.service.NodeService;
import com.javachina.service.NoticeService;
import com.javachina.service.SettingsService;
import com.javachina.service.TopicCountService;
import com.javachina.service.TopicService;
import com.javachina.service.UserService;

import blade.kit.CollectionKit;
import blade.kit.StringKit;

@Service
public class TopicServiceImpl implements TopicService {
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private SettingsService settingsService;
	
	@Inject
	private TopicCountService topicCountService;
	
	@Override
	public Topic getTopic(Long tid) {
		return AR.findById(Topic.class, tid);
	}
	
	@Override
	public List<Map<String, Object>> getTopicList(QueryParam queryParam) {
		if(null != queryParam){
			List<Topic> topics = AR.find(queryParam).list(Topic.class);
			return this.getTopicListMap(topics);
		}
		return null;
	}
	
	@Override
	public Page<Map<String, Object>> getPageList(QueryParam queryParam) {
		if(null != queryParam){
			Page<Topic> topicPage = AR.find(queryParam).page(Topic.class);
			return this.getTopicPageMap(topicPage);
		}
		return null;
	}
	
	private List<Map<String, Object>> getTopicListMap(List<Topic> topics){
		List<Map<String, Object>> topicMaps = new ArrayList<Map<String,Object>>();
		if(null != topics && topics.size() > 0){
			for(Topic topic : topics){
				Map<String, Object> map = this.getTopicMap(topic, false);
				if(null != map && !map.isEmpty()){
					topicMaps.add(map);
				}
			}
		}
		return topicMaps;
	}
	
	private Page<Map<String, Object>> getTopicPageMap(Page<Topic> topicPage){
		
		long totalCount = topicPage.getTotalCount();
		int page = topicPage.getPage();
		int pageSize = topicPage.getPageSize();
		Page<Map<String, Object>> result = new Page<Map<String,Object>>(totalCount, page, pageSize);
		
		List<Topic> topics = topicPage.getResults();
		List<Map<String, Object>> topicMaps = this.getTopicListMap(topics);
		result.setResults(topicMaps);
		
		return result;
	}
	
	@Override
	public Long save(Long uid, Long nid, String title, String content, Integer isTop) {
		
		try {
			Integer time = DateKit.getCurrentUnixTime();
			
			Long tid = (Long) AR
					.update("insert into t_topic(uid, nid, title, content, is_top, create_time, update_time, status) values(?, ?, ?, ?, ?, ?, ?, ?)",
							uid, nid, title, content, isTop, time, time, 1).key();
			
			if(null != tid){
				
				topicCountService.save(tid, time);
				
				this.updateWeight(tid);
				
				// 更新节点下的帖子数
				nodeService.updateCount(nid, Types.topics.toString(), +1);
				
				// 更新总贴数
				settingsService.updateCount(Types.topic_count.toString(), +1);
				
				// 通知@的人
				if(null != tid){
					Set<String> atUsers = Utils.getAtUsers(content);
					if(CollectionKit.isNotEmpty(atUsers)){
						for(String user_name : atUsers){
							User user = userService.getUser(user_name);
							if(null != user && !user.getUid().equals(uid)){
								noticeService.save(Types.at.toString(), uid, user.getUid(), tid);
							}
						}
					}
				}
			}
			return tid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean delete(Long tid) {
		if(null != tid){
			AR.update("update t_topic set status = 2 where tid = ?", tid).executeUpdate(true);
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> getTopicMap(Topic topic, boolean isDetail) {
		if(null == topic){
			return null;
		}
		Long tid = topic.getTid();
		Long uid = topic.getUid();
		Long nid = topic.getNid();
		
		User user = userService.getUser(uid);
		Node node = nodeService.getNode(nid);
		
		if(null == user || null == node){
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tid", tid);
		
		TopicCount topicCount = topicCountService.getCount(tid);
		Long views = 0L, loves = 0L, favorites = 0L, comments = 0L;
		if(null != topicCount){
			views = topicCount.getViews();
			loves = topicCount.getLoves();
			favorites = topicCount.getFavorites();
			comments = topicCount.getComments();
		}
		
		map.put("views", views);
		map.put("loves", loves);
		map.put("favorites", favorites);
		map.put("comments", comments);
		map.put("title", topic.getTitle());
		map.put("is_essence", topic.getIs_essence());
		map.put("create_time", topic.getCreate_time());
		map.put("update_time", topic.getUpdate_time());
		map.put("user_name", user.getLogin_name());
		
		String avatar = Utils.getAvatar(user.getAvatar(), ImageTypes.small);
		
		map.put("avatar", avatar);
		map.put("node_name", node.getTitle());
		map.put("node_slug", node.getSlug());
		
		if(comments > 0){
			Comment comment = commentService.getTopicLastComment(tid);
			if(null != comment){
				User reply_user = userService.getUser(comment.getUid());
				map.put("reply_name", reply_user.getLogin_name());
			}
		}
		
		if(isDetail){
			String content = Utils.markdown2html(topic.getContent());
			map.put("content", content);
		}
		return map;
	}

	

	/**
	 * 评论帖子
	 * @param uid		评论人uid
	 * @param to_uid	发帖人uid
	 * @param tid		帖子id
	 * @param content	评论内容
	 * @return
	 */
	@Override
	public boolean comment(Long uid, Long to_uid, Long tid, String content, String ua) {
		try {
			boolean flag = commentService.save(uid, to_uid, tid, content, ua);
			if(flag){
				
				topicCountService.update(Types.comments.toString(), tid, 1);
				this.updateWeight(tid);
				
				// 通知
				if(!uid.equals(to_uid)){
					noticeService.save(Types.comment.toString(), uid, to_uid, tid);
					
					// 通知@的用户
					Set<String> atUsers = Utils.getAtUsers(content);
					if(CollectionKit.isNotEmpty(atUsers)){
						for(String user_name : atUsers){
							User user = userService.getUser(user_name);
							if(null != user && !user.getUid().equals(uid)){
								noticeService.save(Types.at.toString(), uid, user.getUid(), tid);
							}
						}
					}
					
					// 更新总评论数
					settingsService.updateCount(Types.comment_count.toString(), +1);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Long getTopics(Long uid) {
		if(null != uid){
			return AR.find("select count(1) from t_topic where uid = ? and status = 1", uid).first(Long.class);
		}
		return 0L;
	}

	@Override
	public Long update(Long tid, Long nid, String title, String content) {
		if(null != tid && null != nid && StringKit.isNotBlank(title) && StringKit.isNotBlank(content)){
			try {
				AR.update("update t_topic set nid = ?, title = ?, content = ?, update_time = ? where tid = ?",
						nid, title, content, DateKit.getCurrentUnixTime(), tid).executeUpdate(true);
				return tid;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Long getLastCreateTime(Long uid) {
		if(null == uid){
			return null;
		}
		return AR.find("select create_time from t_topic where uid = ? order by create_time desc", uid).first(Long.class);
	}
	
	@Override
	public Long getLastUpdateTime(Long uid) {
		if(null == uid){
			return null;
		}
		return AR.find("select update_time from t_topic where uid = ? order by update_time desc", uid).first(Long.class);
	}
	
	@Override
	public boolean refreshWeight() {
		List<Long> topics = AR.find("select tid from t_topic where status = 1").list(Long.class);
		if(null != topics) {
			for(Long tid : topics){
				this.updateWeight(tid);
			}
		}
		return false;
	}

	public boolean updateWeight(Long tid, Long loves, Long favorites, Long comment, Long sinks, Long create_time) {
		if(null == tid){
			return false;
		}
		
		try {
			double weight = Utils.getWeight(loves, favorites, comment, sinks, create_time);
			AR.update("update t_topic set weight = ? where tid = ?", weight, tid).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Page<Map<String, Object>> getHotTopic(Long nid, Integer page, Integer count) {
		if(null == page || page < 1){
			page = 1;
		}
		QueryParam tp = QueryParam.me();
		if(null != nid){
			tp.eq("nid", nid);
		}
		tp.eq("status", 1).orderby("weight desc").page(page, count);
		return this.getPageList(tp);
	}

	@Override
	public Page<Map<String, Object>> getRecentTopic(Long nid, Integer page, Integer count) {
		if(null == page || page < 1){
			page = 1;
		}
		QueryParam tp = QueryParam.me();
		if(null != nid){
			tp.eq("nid", nid);
		}
		tp.eq("status", 1).orderby("create_time desc").page(page, count);
		return this.getPageList(tp);
	}

	@Override
	public void essence(Long tid, Integer count) {
		try {
			AR.update("update t_topic set is_essence = ? where tid = ?", count, tid).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean updateWeight(Long tid) {
		if(null != tid){
			TopicCount topicCount = topicCountService.getCount(tid);
			Long loves = topicCount.getLoves();
			Long favorites = topicCount.getFavorites();
			Long comment = topicCount.getComments();
			Long sinks = topicCount.getSinks();
			Long create_time = topicCount.getCreate_time();
			return this.updateWeight(tid, loves, favorites, comment, sinks, create_time);
		}
		return false;
	}
	
}
