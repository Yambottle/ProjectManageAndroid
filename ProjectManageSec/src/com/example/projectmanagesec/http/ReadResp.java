package com.example.projectmanagesec.http;

import java.util.Map;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ReadResp {

	private String message;
	private String code;
	private String data;
	
	@SuppressWarnings("unchecked")
	public String readResp(Message msg){//analysis resp and return:resp->(data->(list(list)->utp,project),user),code,msg
		Gson gson = new Gson();
		Map<String,Object> jsonMap = gson.fromJson(msg.obj.toString(), new TypeToken<Map<String,Object>>() {}.getType());
		message = (String) jsonMap.get("msg");
		code = (String) jsonMap.get("code");
		data = new Gson().toJson((Map<String, Object>)jsonMap.get("data"));
				
		return data;
	}
	
	public void readRespNoData(Message msg){//analysis resp and return:resp->(data->(list(list)->utp,project),user),code,msg
		Gson gson = new Gson();
		Map<String,Object> jsonMap = gson.fromJson(msg.obj.toString(), new TypeToken<Map<String,Object>>() {}.getType());
		message = (String) jsonMap.get("msg");
		code = (String) jsonMap.get("code");

	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	public String getData() {
		return data;
	}
	
}
