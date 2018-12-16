package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class ProjectLog implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String projectName;
	

	private String contractId;
	

	private String userName;
	

	private String userTruename;
	

	private String logString;
	

	private String date;
	

	private String cause;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLogString() {
		return logString;
	}

	public void setLogString(String logString) {
		this.logString = logString;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getUserTruename() {
		return userTruename;
	}

	public void setUserTruename(String userTruename) {
		this.userTruename = userTruename;
	}
	
	
}
