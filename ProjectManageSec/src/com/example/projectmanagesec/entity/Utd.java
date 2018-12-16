package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class Utd implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;


	private String userName;
	

	private String departmentName;

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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	
}
