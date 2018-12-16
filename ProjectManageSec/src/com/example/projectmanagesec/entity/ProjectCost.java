package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class ProjectCost implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String projectName;
	

	private String date;
	

	private String cost;
	

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

}
