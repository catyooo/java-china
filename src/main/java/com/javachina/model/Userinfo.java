package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Userinfo对象
 */
@Table(value = "t_userinfo", PK = "uid")
public class Userinfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer uid;
	
	private String nick_name;
	
	private String jobs;
	
	private String web_site;
	
	private String location;
	
	private String github;
	
	private String weibo;
	
	private String signature;
	
	private String instructions;
	
	public Userinfo(){}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	
	public String getWeb_site() {
		return web_site;
	}

	public void setWeb_site(String web_site) {
		this.web_site = web_site;
	}
	
	public String getGithub() {
		return github;
	}

	public void setGithub(String github) {
		this.github = github;
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getJobs() {
		return jobs;
	}

	public void setJobs(String jobs) {
		this.jobs = jobs;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}
	
}