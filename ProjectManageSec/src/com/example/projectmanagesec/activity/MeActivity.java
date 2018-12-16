package com.example.projectmanagesec.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.projectmanagesec.R;
import com.example.projectmanagesec.common.Parameter;
import com.example.projectmanagesec.http.AccessNetwork;
import com.example.projectmanagesec.http.ReadResp;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeActivity extends Activity implements OnClickListener {

	private ImageButton backButton;
	
	private TextView myInfo, myName,myDepart, myPhone, pwdTextView, outTextView  ;
	private RelativeLayout pwdRelativeLayout, outRelativeLayout ;
	private ImageView pwdImageView, outImageView ;
	
	private SharedPreferences sp ;
	
	private Map<String, Object> userMap;
	private ArrayList<Map<String, Object>> departList;	
	
	// login interface
	private String url = Parameter.url + "user.do?method=login";
	private String userkey = "user";
	private List<NameValuePair> nameValuePairs;
	private Gson gson;

	// response info
	private ReadResp readResp;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		
		backButton = (ImageButton) findViewById(R.id.back);
		
		sp = MeActivity.this.getSharedPreferences(Parameter.SP_NAME, Context.MODE_PRIVATE);
		
		userMap = (Map<String, Object>) getIntent().getSerializableExtra("user");
		departList = (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("utds");
		
		myInfo = (TextView) findViewById(R.id.myInfo);
		myName = (TextView) findViewById(R.id.myTrueName);
		myPhone = (TextView) findViewById(R.id.myPhone);
		myDepart = (TextView) findViewById(R.id.myDepart);
		
		myInfo.setText(userMap.get("name").toString());
		myName.setText("姓名:"+userMap.get("truename").toString());
		myPhone.setText("电话:"+userMap.get("phone").toString());
		String departString = "";
		for(Map<String, Object> departMap : departList){
			departString = departString + departMap.get("departmentName").toString() + ";";
		}
		if(departString.equals("")){
			myDepart.setText("部门:--");
		}else{
			myDepart.setText("部门:"+departString);
		}
		
		
		pwdRelativeLayout = (RelativeLayout) findViewById(R.id.pwdRL);
		pwdTextView = (TextView) findViewById(R.id.pwdTV);
		pwdImageView = (ImageView) findViewById(R.id.pwdIV);
		
		outRelativeLayout = (RelativeLayout) findViewById(R.id.outRL);
		outTextView = (TextView) findViewById(R.id.outTV);
		outImageView = (ImageView) findViewById(R.id.outIV);
		
		pwdRelativeLayout.setOnClickListener(this);
		pwdTextView.setOnClickListener(this);
		pwdImageView.setOnClickListener(this);
		outRelativeLayout.setOnClickListener(this);
		outTextView.setOnClickListener(this);
		outImageView.setOnClickListener(this);
		
		readResp = new ReadResp();
		nameValuePairs = new ArrayList<NameValuePair>();
		gson = new Gson();
		
		initEvent();
	}

	private void initEvent(){
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				login();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pwdIV:
		case R.id.pwdRL:
		case R.id.pwdTV:
			changePWD();
			break;
		case R.id.outIV:
		case R.id.outRL:
		case R.id.outTV:
			logout();
			break;
		}
	}
		
	public void logout(){
		Editor e = sp.edit();
		e.remove(userMap.get("name").toString()+"Autologin");
		e.commit();
		
		Intent intent = new Intent();
		intent.setClass(MeActivity.this, LoginActivity.class);
		MeActivity.this.startActivity(intent);
		MeActivity.this.finish();
	}
	
	public void changePWD(){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", (Serializable)userMap);
		intent.putExtras(bundle);
		intent.setClass(MeActivity.this, PwdActivity.class);
		MeActivity.this.startActivity(intent);
	}

	// TODO login
		public void login(){	
			Handler h= new Handler(){
				@Override
				public void handleMessage(Message msg) {
					String error = msg.getData().getString("error");
					if(error == null){
						String dataString = readResp.readResp(msg);
						if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){
							Intent i = new Intent();
							Bundle bundle = new Bundle();
							bundle.putString("data", dataString);
							i.putExtras(bundle);
							i.setClass(MeActivity.this, MainActivity.class);
							startActivity(i);
							MeActivity.this.finish();
						}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){
							Toast.makeText(MeActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(MeActivity.this,error, Toast.LENGTH_LONG).show();
					}		
				}
			};
			nameValuePairs.clear();
			Map<String, Object> loginMap = new HashMap<String, Object>();
			loginMap.put("name", userMap.get("name").toString());
			loginMap.put("pwd", userMap.get("pwd").toString());
			nameValuePairs.add(new BasicNameValuePair(userkey,gson.toJson(loginMap)));
			new Thread(new AccessNetwork(url,nameValuePairs,h)).start();
		}
		
		@Override
	  	public boolean onKeyDown(int keyCode, KeyEvent event){
	  		if (keyCode == KeyEvent.KEYCODE_BACK ){
	  			login();
	  		}
	  		return false;
	  	}
}
