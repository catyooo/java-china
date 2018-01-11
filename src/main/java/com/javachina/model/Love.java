package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Love对象
 */
@Table(value = "t_love", PK = "id")
public class Love implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long tid;
	
	private Long uid;
	
	public Love(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
	
}