package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Topic对象
 */
@Table(value = "t_topic", PK = "tid")
public class Topic implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long tid;
	
	//发布人
	private Long uid;
	
	//所属节点
	private Long nid;
	
	//帖子标题
	private String title;
	
	//帖子内容
	private String content;
	
	//是否置顶
	private Integer is_top;
	
	//是否是精华贴
	private Integer is_essence;
	
	// 帖子权重
	private Double weight;
	
	//帖子创建时间
	private Long create_time;
	
	//最后更新时间
	private Long update_time;
	
	//1:正常 2:删除
	private Integer status;
	
	public Topic(){}
	
	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getNid() {
		return nid;
	}

	public void setNid(Long nid) {
		this.nid = nid;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getIs_top() {
		return is_top;
	}

	public void setIs_top(Integer is_top) {
		this.is_top = is_top;
	}
	
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	public Long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getIs_essence() {
		return is_essence;
	}

	public void setIs_essence(Integer is_essence) {
		this.is_essence = is_essence;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
}