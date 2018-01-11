package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Activecode对象
 */
@Table(value = "t_activecode", PK = "id")
public class Activecode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long uid;
	
	private String code;
	
	private String type;
	
	private Integer is_use;
	
	//过期时间
	private Long expires_time;
	
	//创建时间
	private Long create_time;
	
	public Activecode(){}
	
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
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public Integer getIs_use() {
		return is_use;
	}

	public void setIs_use(Integer is_use) {
		this.is_use = is_use;
	}
	
	public Long getExpires_time() {
		return expires_time;
	}

	public void setExpires_time(Long expires_time) {
		this.expires_time = expires_time;
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