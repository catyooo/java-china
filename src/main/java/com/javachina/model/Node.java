package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Node对象
 */
@Table(value = "t_node", PK = "nid")
public class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long nid;
	
	//父节点id
	private Long pid;
	
	//节点名称
	private String title;
	
	//节点描述
	private String description;
	
	//节点英文简写
	private String slug;
	
	// 节点图片
	private String pic;
	
	//帖子数
	private Long topics;
	
	//创建时间
	private Long create_time;
	
	//最后更新时间
	private Long update_time;
	
	//是否删除
	private Integer is_del;
	
	public Node(){}
	
	public Long getNid() {
		return nid;
	}

	public void setNid(Long nid) {
		this.nid = nid;
	}
	
	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Long getTopics() {
		return topics;
	}

	public void setTopics(Long topics) {
		this.topics = topics;
	}
	
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	public Integer getIs_del() {
		return is_del;
	}

	public void setIs_del(Integer is_del) {
		this.is_del = is_del;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
	
}