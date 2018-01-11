package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Notice对象
 */
@Table(value = "t_notice", PK = "id")
public class Notice implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long uid;
	
	private Long to_uid;
	
	private Long event_id;
	
	private String type;
	
	private Boolean is_read;
	
	private Long create_time;
	
	public Notice(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(Long to_uid) {
		this.to_uid = to_uid;
	}

	public Long getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Long event_id) {
		this.event_id = event_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIs_read() {
		return is_read;
	}

	public void setIs_read(Boolean is_read) {
		this.is_read = is_read;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
}