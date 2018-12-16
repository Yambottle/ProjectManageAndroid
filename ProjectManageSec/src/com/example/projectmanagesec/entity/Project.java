package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class Project implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	

	private String name;
	

	private String fullName;
	

	private String center;
	

	private String map;
	

	private String creator;
	

	private String date;
	

	private String machineCost;
	

	private String peopleCost;
	

	private String materialCost;
	

	private String otherCost;
	

	private String allCost;
	

	private String costDate;
	

	private String remark;


	private String status;
	

	private String expectEndDate;
	

	private String expectProfit;
	

	private String departmentName;
	

	private String allValuation;
	
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

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMachineCost() {
		return machineCost;
	}

	public void setMachineCost(String machineCost) {
		this.machineCost = machineCost;
	}

	public String getPeopleCost() {
		return peopleCost;
	}

	public void setPeopleCost(String peopleCost) {
		this.peopleCost = peopleCost;
	}

	public String getMaterialCost() {
		return materialCost;
	}

	public void setMaterialCost(String materialCost) {
		this.materialCost = materialCost;
	}

	public String getOtherCost() {
		return otherCost;
	}

	public void setOtherCost(String otherCost) {
		this.otherCost = otherCost;
	}

	public String getAllCost() {
		return allCost;
	}

	public void setAllCost(String allCost) {
		this.allCost = allCost;
	}

	public String getCostDate() {
		return costDate;
	}

	public void setCostDate(String costDate) {
		this.costDate = costDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExpectEndDate() {
		return expectEndDate;
	}

	public void setExpectEndDate(String expectEndDate) {
		this.expectEndDate = expectEndDate;
	}

	public String getExpectProfit() {
		return expectProfit;
	}

	public void setExpectProfit(String expectProfit) {
		this.expectProfit = expectProfit;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getAllValuation() {
		return allValuation;
	}

	public void setAllValuation(String allValuation) {
		this.allValuation = allValuation;
	}

}
