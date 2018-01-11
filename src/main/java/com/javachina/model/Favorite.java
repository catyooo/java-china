package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Favorite对象
 */
@Table(value = "t_favorite", PK = "id")
public class Favorite implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	//topic:帖子 node:节点
	private String type;
	
	private Long uid;
	
	private Long event_id;
	
	private Long create_time;
	
	public Favorite(){}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Long event_id) {
		this.event_id = event_id;
	}
	
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
}