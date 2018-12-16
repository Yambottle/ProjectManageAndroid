package com.example.projectmanagesec.entity;

import java.io.Serializable;

public class ValuationReport implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;


	private String name;
	

	private String valuationId;
	

	private String price;
	

	private String priceDate;
	

	private String priceUser;
	

	private String priceUserTruename;
	

	private String total;
	

	private String totalDate;
	

	private String totalUser;
	

	private String totalUserTruename;
	

	private String remark;
	

	private String cost;

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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getPriceUser() {
		return priceUser;
	}

	public void setPriceUser(String priceUser) {
		this.priceUser = priceUser;
	}

	public String getPriceUserTruename() {
		return priceUserTruename;
	}

	public void setPriceUserTruename(String priceUserTruename) {
		this.priceUserTruename = priceUserTruename;
	}

	public String getTotalUser() {
		return totalUser;
	}

	public void setTotalUser(String totalUser) {
		this.totalUser = totalUser;
	}

	public String getTotalUserTruename() {
		return totalUserTruename;
	}

	public void setTotalUserTruename(String totalUserTruename) {
		this.totalUserTruename = totalUserTruename;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPriceDate() {
		return priceDate;
	}

	public void setPriceDate(String priceDate) {
		this.priceDate = priceDate;
	}

	public String getTotalDate() {
		return totalDate;
	}

	public void setTotalDate(String totalDate) {
		this.totalDate = totalDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
