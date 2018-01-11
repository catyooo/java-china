package com.javachina.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.javachina.ImageTypes;
import com.javachina.Types;
import com.javachina.kit.QiniuKit;
import com.javachina.kit.Utils;
import com.javachina.model.LoginUser;
import com.javachina.model.User;
import com.javachina.model.Userinfo;
import com.javachina.service.ActivecodeService;
import com.javachina.service.CommentService;
import com.javachina.service.FavoriteService;
import com.javachina.service.NoticeService;
import com.javachina.service.TopicService;
import com.javachina.service.UserService;
import com.javachina.service.UserinfoService;

import blade.kit.DateKit;
import blade.kit.EncrypKit;
import blade.kit.FileKit;
import blade.kit.StringKit;

@Service
public class UserServiceImpl implements UserService {
	
	@Inject
	private ActivecodeService activecodeService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private UserinfoService userinfoService;
	
	@Inject
	private FavoriteService favoriteService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private NoticeService noticeService;
	
	@Override
	public User getUser(Long uid) {
		return AR.findById(User.class, uid);
	}
	
	@Override
	public User getUser(QueryParam queryParam) {
		return AR.find(queryParam).first(User.class);
	}
		
	@Override
	public List<User> getUserList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).list(User.class);
		}
		return null;
	}
	
	@Override
	public Page<User> getPageList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).page(User.class);
		}
		return null;
	}
	
	@Override
	public User signup(String loginName, String passWord, String email) {
		if(StringKit.isBlank(loginName) || StringKit.isBlank(passWord) || StringKit.isBlank(email)){
			return null;
		}
		int time = DateKit.getCurrentUnixTime();
		String pwd = EncrypKit.md5(loginName + passWord);
		try {
			
			String avatar = "avatar/default/" + StringKit.getRandomNumber(1, 5) + ".png";
			
			Long uid = (Long) AR.update("insert into t_user(login_name, pass_word, email, avatar, status, create_time, update_time) values(?, ?, ?, ?, ?, ?, ?)",
					loginName, pwd, email, avatar, 0, time, time).key();
			
			User user = this.getUser(uid);
			
			// 发送邮件通知
			activecodeService.save(user, Types.signup.toString());
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean delete(Long uid) {
		if(null != uid){
			AR.update("delete from t_user where uid = ?", uid).executeUpdate();
			return true;
		}
		return false;
	}

	@Override
	public boolean resetPwd(String email) {
		
		return false;
	}

	@Override
	public User signin(String loginName, String passWord) {
		if(StringKit.isBlank(loginName) || StringKit.isBlank(passWord)){
			return null;
		}
		String pwd = EncrypKit.md5(loginName + passWord);
	    User user = AR.find("select * from t_user where login_name = ? and pass_word = ? and status in (0, 1)",
				loginName, pwd).first(User.class);
	    if(null == user){
	    	user = AR.find("select * from t_user where email = ? and pass_word = ? and status in (0, 1)",
					loginName, pwd).first(User.class);
	    }
		return user;
	}

	@Override
	public Map<String, Object> getUserDetail(Long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != uid){
			User user = this.getUser(uid);
			if(null == user){
				return map;
			}
			map.put("username", user.getLogin_name());
			map.put("uid", uid);
			map.put("email", user.getEmail());
			String avatar = Utils.getAvatar(user.getAvatar(), ImageTypes.normal);
			map.put("avatar", avatar);
			map.put("create_time", user.getCreate_time());
			
			Userinfo userinfo = userinfoService.getUserinfo(uid);
			if(null != userinfo){
				map.put("jobs", userinfo.getJobs());
				map.put("github", userinfo.getGithub());
				map.put("weibo", userinfo.getWeibo());
				map.put("nick_name", userinfo.getNick_name());
				map.put("location", userinfo.getLocation());
				map.put("signature", userinfo.getSignature());
				map.put("web_site", userinfo.getWeb_site());
				map.put("instructions", userinfo.getInstructions());
			}
		}
		return map;
	}
	
	@Override
	public boolean updateStatus(Long uid, Integer status) {
		if(null != uid && null != status){
			try {
				AR.update("update t_user set status = ? where uid = ?", status, uid).executeUpdate(true);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean updateAvatar(Long uid, String avatar_path) {
		try {
			if(null == uid || StringKit.isBlank(avatar_path)){
				return false;
			}
			
			File file = new File(avatar_path);
			if(file.exists()){
				
				User user = this.getUser(uid);
				if(null == user){
					return false;
				}
				
				String ext = FileKit.getExtension(file.getName());
				if(StringKit.isBlank(ext)){
					ext = "png";
				}
				
				String key = "avatar/" + user.getLogin_name() + "/" + StringKit.getRandomChar(4) + "/" + StringKit.getRandomNumber(4) + "." + ext;
				
				boolean flag = QiniuKit.upload(file, key);
				if(flag){
					AR.update("update t_user set avatar = ? where uid = ?", key, uid).executeUpdate();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean updatePwd(Long uid, String newpwd) {
		try {
			if(null == uid || StringKit.isBlank(newpwd)){
				return false;
			}
			AR.update("update t_user set pass_word = ? where uid = ?", newpwd, uid).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public LoginUser getLoginUser(User user, Long uid) {
		if(null == user){
			user = this.getUser(uid);
		}
		if(null != user){
			LoginUser loginUser = new LoginUser();
			loginUser.setUid(user.getUid());
			loginUser.setUser_name(user.getLogin_name());
			loginUser.setPass_word(user.getPass_word());
			loginUser.setStatus(user.getStatus());
			loginUser.setRole_id(user.getRole_id());
			String avatar = Utils.getAvatar(user.getAvatar(), ImageTypes.normal);
			loginUser.setAvatar(avatar);
			
			Long comments = commentService.getComments(user.getUid());
			loginUser.setComments(comments);
			
			Long topics = topicService.getTopics(user.getUid());
			loginUser.setTopics(topics);
			
			Long notices = noticeService.getNotices(user.getUid());
			loginUser.setNotices(notices);
			
			Userinfo userinfo = userinfoService.getUserinfo(user.getUid());
			if(null != userinfo){
				loginUser.setJobs(userinfo.getJobs());
				loginUser.setNick_name(userinfo.getNick_name());
			}
			
			Long my_topics = favoriteService.favorites(Types.topic.toString(), user.getUid());
			Long my_nodes = favoriteService.favorites(Types.node.toString(), user.getUid());
			
			loginUser.setMy_topics(my_topics);
			loginUser.setMy_nodes(my_nodes);
			
			Long following = favoriteService.favorites(Types.following.toString(), user.getUid());
			loginUser.setFollowing(following);
			
			return loginUser;
		}
		return null;
	}

	@Override
	public boolean hasUser(String login_name) {
		if(StringKit.isNotBlank(login_name)){
			Long count = AR.find("select count(1) from t_user where login_name = ? and status in (0, 1)", login_name).first(Long.class);
			if(count == 0){
				count = AR.find("select count(1) from t_user where email = ? and status in (0, 1)", login_name).first(Long.class);
			}
			return count > 0;
		}
		return false;
	}

	@Override
	public User getUser(String user_name) {
		if(StringKit.isNotBlank(user_name)){
			return AR.find("select * from t_user where login_name = ? and status = 1", user_name).first(User.class);
		}
		return null;
	}

	@Override
	public boolean updateRole(Long uid, Integer role_id) {
		try {
			if(null == uid || null == role_id || role_id == 1){
				return false;
			}
			AR.update("update t_user set role_id = ? where uid = ?", role_id, uid).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
