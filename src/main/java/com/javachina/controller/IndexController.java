package com.javachina.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.blade.Blade;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.blade.route.annotation.Path;
import com.blade.route.annotation.PathVariable;
import com.blade.route.annotation.Route;
import com.blade.view.ModelAndView;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.multipart.FileItem;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.kit.FamousDay;
import com.javachina.kit.FamousKit;
import com.javachina.kit.SessionKit;
import com.javachina.kit.Utils;
import com.javachina.model.LoginUser;
import com.javachina.model.Node;
import com.javachina.service.FavoriteService;
import com.javachina.service.NodeService;
import com.javachina.service.NoticeService;
import com.javachina.service.TopicService;

import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.PatternKit;
import blade.kit.StringKit;
import blade.kit.json.JSONObject;

@Path("/")
public class IndexController extends BaseController {

	@Inject
	private TopicService topicService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private FavoriteService favoriteService;
	
	/**
	 * 首页热门
	 */
	@Route(value = "/", method = HttpMethod.GET)
	public ModelAndView show_home(Request request, Response response){
		
		this.putData(request);
		
		// 帖子
		String tab = request.query("tab");
		Integer page = request.queryAsInt("p");
		Long nid = null;
		
		if(StringKit.isNotBlank(tab)){
			QueryParam np = QueryParam.me();
			np.eq("is_del", 0).eq("slug", tab);
			Node node = nodeService.getNode(np);
			if(null != node){
				nid = node.getNid();
				request.attribute("tab", tab);
				request.attribute("node_name", node.getTitle());
			}
		}
		
		Page<Map<String, Object>> topicPage = topicService.getHotTopic(nid, page, 20);
		request.attribute("topicPage", topicPage);
		
		// 最热帖子
		List<Map<String, Object>> hot_topics = topicService.getHotTopic(null, 1, 10).getResults();
		request.attribute("hot_topics", hot_topics);
		
		// 最热门的8个节点
		QueryParam np = QueryParam.me();
		np.eq("is_del", 0).notEq("pid", 0).orderby("topics desc").add("limit 8");
		List<Node> hot_nodes = nodeService.getNodeList(np);
		request.attribute("hot_nodes", hot_nodes);
		
		return this.getView("home");
	}
	
	/**
	 * 最新
	 */
	@Route(value = "/recent", method = HttpMethod.GET)
	public ModelAndView show_recent(Request request, Response response){
		
		this.putData(request);
		
		// 帖子
		String tab = request.query("tab");
		Integer page = request.queryAsInt("p");
		Long nid = null;
		
		if(StringKit.isNotBlank(tab)){
			QueryParam np = QueryParam.me();
			np.eq("is_del", 0).eq("slug", tab);
			Node node = nodeService.getNode(np);
			if(null != node){
				nid = node.getNid();
				request.attribute("tab", tab);
				request.attribute("node_name", node.getTitle());
			}
		}
		
		Page<Map<String, Object>> topicPage = topicService.getRecentTopic(nid, page, 15);
		request.attribute("topicPage", topicPage);
				
		// 最热帖子
		List<Map<String, Object>> hot_topics = topicService.getHotTopic(null, 1, 10).getResults();
		request.attribute("hot_topics", hot_topics);
		
		// 最热门的10个节点
		QueryParam np = QueryParam.me();
		np.eq("is_del", 0).notEq("pid", 0).orderby("topics desc").add("limit 8");
		List<Node> hot_nodes = nodeService.getNodeList(np);
		request.attribute("hot_nodes", hot_nodes);
		
		return this.getView("recent");
	}
	
	private void putData(Request request){
		// 读取节点列表
		List<Map<String, Object>> nodes = nodeService.getNodeList();
		request.attribute("nodes", nodes);
		
		// 每日格言
		FamousDay famousDay = FamousKit.getTodayFamous();
		Constant.VIEW_CONTEXT.set("famousDay", famousDay);
		
		// 未读提醒
		LoginUser loginUser = SessionKit.getLoginUser();
		if(null != loginUser){
			Long notices = noticeService.getNotices(loginUser.getUid());
			Constant.VIEW_CONTEXT.set("my_notices", notices);
		}
	}
	
	/**
	 * 节点主题页
	 */
	@Route(value = "/go/:slug", method = HttpMethod.GET)
	public ModelAndView go(@PathVariable("slug") String slug,
			Request request, Response response){
		
		LoginUser loginUser = SessionKit.getLoginUser();
		QueryParam np = QueryParam.me();
		np.eq("is_del", 0).eq("slug", slug);
		Node node = nodeService.getNode(np);
		if(null == node){
			// 不存在的节点
//			response.text("not found node.");
			response.go("/");
			return null;
		}
		
		if(null == loginUser){
			SessionKit.setCookie(response, Constant.JC_REFERRER_COOKIE, request.url());
		} else {
			// 查询是否收藏
			boolean is_favorite = favoriteService.isFavorite(Types.node.toString(), loginUser.getUid(), node.getNid());
			request.attribute("is_favorite", is_favorite);
		}
		
		Integer page = request.queryAsInt("page");
		
		Page<Map<String, Object>> topicPage = topicService.getRecentTopic(node.getNid(), page, 15);
		request.attribute("topicPage", topicPage);
		
		Map<String, Object> nodeMap = nodeService.getNodeDetail(null, node.getNid());
		request.attribute("node", nodeMap);
		
		return this.getView("node_detail");
	}
	
	
	/**
	 * 上传头像
	 */
	@Route(value = "/uploadimg", method = HttpMethod.POST)
	public void uploadimg(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			return;
		}
		FileItem[] fileItems = request.files();
		if(null != fileItems && fileItems.length > 0){
			
			FileItem fileItem = fileItems[0];
			
			String type = request.query("type");
			String suffix = FileKit.getExtension(fileItem.getFileName());
			if(StringKit.isNotBlank(suffix)){
				suffix = "." + suffix;
			}
			if(!PatternKit.isImage(suffix)){
				return;
			}
			
			if(null == type){
				type = "temp";
			}
			
			String saveName = DateKit.dateFormat(new Date(), "yyyyMMddHHmmssSSS")  + "_" + StringKit.getRandomChar(10) + suffix;
			File file = new File(Blade.me().webRoot() + File.separator + Constant.UPLOAD_FOLDER + File.separator + saveName);
			
			try {
				
				Utils.copyFileUsingFileChannels(fileItem.getFile(), file);
				
				String filePath = Constant.UPLOAD_FOLDER + "/" + saveName;
				
				JSONObject res = new JSONObject();
				res.put("status", 200);
				res.put("savekey", filePath);
				res.put("savepath", filePath);
				res.put("url", Constant.SITE_URL + "/" + filePath);
				
				response.json(res.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * markdown页面
	 */
	@Route(value = "/markdown", method = HttpMethod.GET)
	public ModelAndView markdown(Request request, Response response){
		return this.getView("markdown");
	}
	
	/**
	 * about页面
	 */
	@Route(value = "/about", method = HttpMethod.GET)
	public ModelAndView about(Request request, Response response){
		return this.getView("about");
	}
	
	/**
	 * faq页面
	 */
	@Route(value = "/faq", method = HttpMethod.GET)
	public ModelAndView faq(Request request, Response response){
		String qa = request.query("aaa");
		System.out.println(qa);
		return this.getView("faq");
	}
	
	/**
	 * donate页面
	 */
	@Route(value = "/donate", method = HttpMethod.GET)
	public ModelAndView donate(Request request, Response response){
		return this.getView("donate");
	}
	
}
