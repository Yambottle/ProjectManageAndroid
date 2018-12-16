package com.example.projectmanagesec.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Valuation implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String valuationId;
	

	private String projectName;
	

	private Double cost;
	

	private String date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValuationId() {
		return valuationId;
	}

	public void setValuationId(String valuationId) {
		this.valuationId = valuationId;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
