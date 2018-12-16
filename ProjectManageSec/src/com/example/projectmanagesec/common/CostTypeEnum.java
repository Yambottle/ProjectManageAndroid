package com.example.projectmanagesec.common;

public enum CostTypeEnum {
	DAYTAI("day", "天/台"),DAYREN("day", "天/人"),HOURTAI("hour", "小时/台"),HOURREN("hour", "小时/人"),TIME("time","次");
	
	private String value;
	private String key;
	
	private CostTypeEnum(String key,String value) {
		this.value = value;
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
