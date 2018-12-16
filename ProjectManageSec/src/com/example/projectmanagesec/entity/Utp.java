package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class Utp implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;


	private String userName;
	

	private String projectName;
	

	private String permission;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	
}
