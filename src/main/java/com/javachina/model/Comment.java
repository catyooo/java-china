package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Comment对象
 */
@Table(value = "t_comment", PK = "cid")
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long cid;
	
	//评论人uid
	private Long uid;
	
	//被评论人uid
	private Long to_uid;
	
	//帖子id
	private Long tid;
	
	//评论内容
	private String content;
	
	//设备
	private String device;
	
	//评论时间
	private Long create_time;
	
	public Comment(){}
	
	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(Long to_uid) {
		this.to_uid = to_uid;
	}
	
	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
	
}