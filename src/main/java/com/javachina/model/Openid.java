package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Activecode对象
 */
@Table(value = "t_openid", PK = "id")
public class Openid implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String type;
	
	private Long open_id;
	
	private Long uid;
	
	private Long create_time;
	
	public Openid(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOpen_id() {
		return open_id;
	}

	public void setOpen_id(Long open_id) {
		this.open_id = open_id;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}