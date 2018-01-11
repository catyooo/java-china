package com.javachina.controller.admin;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.blade.Blade;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.blade.route.annotation.Path;
import com.blade.route.annotation.PathVariable;
import com.blade.route.annotation.Route;
import com.blade.view.ModelAndView;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.controller.BaseController;
import com.javachina.model.Node;
import com.javachina.model.User;
import com.javachina.service.NodeService;
import com.javachina.service.SettingsService;
import com.javachina.service.TopicService;
import com.javachina.service.UserService;

import blade.kit.StringKit;

@Path("/admin/")
public class IndexController extends BaseController {

	@Inject
	private TopicService topicService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private SettingsService settingsService;
	
	/**
	 * 首页
	 */
	@Route(value = "/")
	public ModelAndView show_home(Request request, Response response){
		return this.getAdminView("home");
	}
	
	/**
	 * 节点列表页面
	 */
	@Route(value = "nodes")
	public ModelAndView show_nodes(Request request, Response response){
		Integer page = request.queryAsInt("p");
		if(null == page || page < 1){
			page = 1;
		}
		QueryParam np = QueryParam.me();
		np.eq("is_del", 0).orderby("topics desc").page(page, 10);
		Page<Map<String, Object>> nodePage = nodeService.getPageList(np);
		request.attribute("nodePage", nodePage);
		return this.getAdminView("nodes");
	}
	
	/**
	 * 添加节点页面
	 */
	@Route(value = "nodes/add", method = HttpMethod.GET)
	public ModelAndView show_add_node(Request request, Response response){
		putData(request);
		return this.getAdminView("add_node");
	}
	
	public void putData(Request request){
		QueryParam np = QueryParam.me();
		np.eq("is_del", 0).eq("pid", 0).orderby("topics desc");
		List<Node> nodes = nodeService.getNodeList(np);
		request.attribute("nodes", nodes);
	}
	
	/**
	 * 添加新节点 
	 * @return
	 */
	@Route(value = "nodes/add", method = HttpMethod.POST)
	public ModelAndView add_node(Request request, Response response){
		
		String title = request.query("node_name");
		String description = request.query("description");
		String node_slug = request.query("node_slug");
		Long pid = request.queryAsLong("pid");
		String node_pic = request.query("node_pic");
		
		if(StringKit.isBlank(title) || StringKit.isBlank(node_slug) || null == pid){
			request.attribute(this.ERROR, "骚年，有些东西木有填哎！！");
			request.attribute("node_name", title);
			request.attribute("node_slug", node_slug);
			return this.getAdminView("add_node");
		}
		
		boolean flag = nodeService.save(pid, title, description, node_slug, node_pic);
		if(flag){
			response.go("/admin/nodes");
			return null;
		} else {
			request.attribute(this.ERROR, "节点添加失败");
			request.attribute("node_name", title);
			request.attribute("node_slug", node_slug);
			return this.getAdminView("add_node");
		}
	}
	
	/**
	 * 编辑节点页面
	 */
	@Route(value = "nodes/:nid", method = HttpMethod.GET)
	public ModelAndView show_edit_node(@PathVariable("nid") Long nid, Request request, Response response){
		
		Map<String, Object> nodeMap = nodeService.getNodeDetail(null, nid);
		request.attribute("node", nodeMap);
		putData(request);
		return this.getAdminView("edit_node");
	}
	
	/**
	 * 编辑节点
	 */
	@Route(value = "nodes/edit", method = HttpMethod.POST)
	public void edit_node(Request request, Response response){
		
		Long nid = request.queryAsLong("nid");
		String title = request.query("node_name");
		String description = request.query("description");
		String node_slug = request.query("node_slug");
		Long pid = request.queryAsLong("pid");
		String node_pic = request.query("node_pic");
		
		if(StringKit.isNotBlank(node_pic)){
			node_pic = Blade.me().webRoot() + File.separator + node_pic;
		}
		
		boolean flag = nodeService.update(nid, pid, title, description, node_slug, node_pic);
		if(flag){
			this.success(response, "");
		} else {
			this.error(response, "节点修改失败");
		}
		
	}
	
	/**
	 * 用户列表页面
	 */
	@Route(value = "users")
	public ModelAndView show_users(Request request, Response response){
		Integer page = request.queryAsInt("p");
		String email = request.query("email");
		if(null == page || page < 1){
			page = 1;
		}
		QueryParam up = QueryParam.me();
		if(StringKit.isNotBlank(email)){
			up.eq("email", email);
			request.attribute("email", email);
		}
		up.orderby("update_time desc").page(page, 15);
		Page<User> userPage = userService.getPageList(up);
		request.attribute("userPage", userPage);
		return this.getAdminView("users");
	}
	
	/**
	 * 系统设置页面
	 */
	@Route(value = "settings")
	public ModelAndView show_settings(Request request, Response response){
		Map<String, Object> settings = settingsService.getSystemInfo();
		request.attribute("settings", settings);
		return this.getAdminView("settings");
	}
	
	/**
	 * 保存系统设置
	 */
	@Route(value = "settings", method = HttpMethod.POST)
	public void save_settings(Request request, Response response){
		String site_title = request.query("site_title");
		String site_keywords = request.query("site_keywords");
		String site_description = request.query("site_description");
		String allow_signup = request.query("allow_signup");
		settingsService.update(site_title, site_keywords, site_description, allow_signup);
		Constant.SYS_INFO = settingsService.getSystemInfo();
		Constant.VIEW_CONTEXT.set("sys_info", Constant.SYS_INFO);
		this.success(response, "");
	}
	
	/**
	 * 修改用户状态
	 */
	@Route(value = "status", method = HttpMethod.POST)
	public void updateStatus(Request request, Response response){
		String type = request.query("type");
		Long uid = request.queryAsLong("uid");
		if(StringKit.isBlank(type) || null == uid){
			this.error(response, "缺少参数");
			return;
		}
		Integer status = null;
		Integer role_id = null;
		if(type.equals(Types.activeAccount.toString()) || 
				type.equals(Types.recoveryAccount.toString())){
			status = 1;
		}
		
		if(type.equals(Types.disable.toString())){
			status = 2;
		}
		
		if(type.equals(Types.removeAdmin.toString())){
			role_id = 5;
		}
		
		if(type.equals(Types.setAdmin.toString())){
			role_id = 3;
		}
		
		if(null != status){
			userService.updateStatus(uid, status);
		}
		
		if(null != role_id){
			userService.updateRole(uid, role_id);
		}
		this.success(response, "");
	}
	
	/**
	 * 系统工具
	 */
	@Route(value = "tools")
	public ModelAndView show_tools(Request request, Response response){
		return this.getAdminView("tools");
	}
	
	/**
	 * 执行系统工具
	 */
	@Route(value = "tools", method = HttpMethod.POST)
	public ModelAndView save_tools(Request request, Response response){
		String type = request.query("type");
		if(StringKit.isBlank(type)){
			request.attribute(this.ERROR, "请选择执行的操作");
			return this.getAdminView("tools");
		}
		
		if(type.equals("clean_cache")){
			AR.cleanCache();
			request.attribute(this.INFO, "执行成功");
			return this.getAdminView("tools");
		}
		
		// 刷新帖子权重，慎用会扫描全表
		if(type.equals("refresh_weight")){
			try {
				topicService.refreshWeight();
				request.attribute(this.INFO, "权重刷新成功，请在首页进行查看。");
			} catch (Exception e) {
				e.printStackTrace();
				request.attribute(this.ERROR, "刷新失败");
			}
			return this.getAdminView("tools");
		}
		
		return this.getAdminView("tools");
	}
	
}
