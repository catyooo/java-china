package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Userlog对象
 */
@Table(value = "t_userlog", PK = "id")
public class Userlog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long uid;
	
	private String action;
	
	private String content;
	
	private String ip_addr;
	
	private Long create_time;
	
	public Userlog(){}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public String getIp_addr() {
		return ip_addr;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}
	
}