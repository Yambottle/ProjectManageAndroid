package com.example.projectmanagesec.entity;

import java.io.Serializable;


public class ContractStowage implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String name	;
	

	private String type;
	

	private String date;
	

	private String company;
	

	private String remark;
	

	private String times;
	

	private String allAverage;
	

	private String allScore;
	

	private String allManage;
	

	private String allFund;
	

	private String allTechnology;
	

	private String status;

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAllScore() {
		return allScore;
	}

	public void setAllScore(String allScore) {
		this.allScore = allScore;
	}

	public String getAllManage() {
		return allManage;
	}

	public void setAllManage(String allManage) {
		this.allManage = allManage;
	}

	public String getAllFund() {
		return allFund;
	}

	public void setAllFund(String allFund) {
		this.allFund = allFund;
	}

	public String getAllTechnology() {
		return allTechnology;
	}

	public void setAllTechnology(String allTechnology) {
		this.allTechnology = allTechnology;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getAllAverage() {
		return allAverage;
	}

	public void setAllAverage(String allAverage) {
		this.allAverage = allAverage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
