package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Settings对象
 */
@Table(value = "t_settings", PK = "skey")
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String skey;
	
	private String svalue;
	
	public Settings(){}
	
	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}
	
	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}
	
}