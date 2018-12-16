package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class ContractStowageScore implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String date;
	

	private String userName;
	

	private String userTruename;
	

	private String contracStowageId;
	

	private String comment;
	

	private String manage;
	

	private String fund;
	

	private String technology;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContracStowageId() {
		return contracStowageId;
	}

	public void setContracStowageId(String contracStowageId) {
		this.contracStowageId = contracStowageId;
	}

	public String getManage() {
		return manage;
	}

	public void setManage(String manage) {
		this.manage = manage;
	}

	public String getFund() {
		return fund;
	}

	public void setFund(String fund) {
		this.fund = fund;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserTruename() {
		return userTruename;
	}

	public void setUserTruename(String userTruename) {
		this.userTruename = userTruename;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
