package com.example.projectmanagesec.common;

public enum PermissionEnum {
	CREATER("1", "创建者"), ADMIN("2", "管理员"), USER("3", "用户"), APPLICANT("0",
			"申请者");

	private String key;
	private String value;

	private PermissionEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
