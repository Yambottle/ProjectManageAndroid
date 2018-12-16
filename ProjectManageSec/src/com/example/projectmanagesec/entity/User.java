package com.example.projectmanagesec.entity;


import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String name;
	

	private String truename;
	

	private String pwd;


	private String phone;

	
	private String date;
	

	private String remark;
	

	private String authority	;
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRemark() {
		return remark;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAuthority() {
		return authority;
	}

	
}
