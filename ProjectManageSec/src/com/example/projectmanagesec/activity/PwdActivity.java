package com.example.projectmanagesec.activity;

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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PwdActivity extends Activity implements OnClickListener{

	private ImageButton backImageButton;
	private EditText oldEditText,newEditText,againEditText;
	private Button okButton;
	
	String newpwd;
	String oldpwd;
	
	private String pwdkey = "pwd";
	private SharedPreferences sp;
	private com.example.projectmanagesec.http.ReadResp readResp;
	private String success = "修改成功";
	private String wrongPWD = "两次密码输入不同，请重新输入";
	
	private String url = Parameter.url+"user.do?method=changePwd";
	private List<NameValuePair> nameValuePairs;
	private Map<String , Object> pwdMap, userMap;
	private Gson gson;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwd);
		
		readResp = new ReadResp();
		sp = this.getSharedPreferences(Parameter.SP_NAME, Context.MODE_PRIVATE);
		userMap = (Map<String, Object>) getIntent().getSerializableExtra("user");
		nameValuePairs = new ArrayList<NameValuePair>();
		pwdMap = new HashMap<String , Object>();
		gson = new Gson();
		
		backImageButton = (ImageButton) findViewById(R.id.back);
		oldEditText = (EditText) findViewById(R.id.oldpwd);
		newEditText = (EditText) findViewById(R.id.newpwd);
		againEditText = (EditText) findViewById(R.id.again);
		okButton = (Button) findViewById(R.id.ok);
		
		backImageButton.setOnClickListener(this);
		okButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;
		case R.id.ok:
			hideSoftWin();
			okButton.setEnabled(false);
			if(newEditText.getText().toString().equals(againEditText.getText().toString())){
				sendPWD();
			}else{
				newEditText.setText("");
				againEditText.setText("");
				okButton.setEnabled(true);
				Toast.makeText(PwdActivity.this, wrongPWD, Toast.LENGTH_LONG).show();
			}
			break;
		}
		
	}

	private void sendPWD() {
		oldpwd = oldEditText.getText().toString().trim();
		newpwd = newEditText.getText().toString().trim();
		
		Handler h= new Handler(){
			@Override
			public void handleMessage(Message msg) {
				readResp.readRespNoData(msg);
				if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//
					Editor e = sp.edit();
					e.putString(userMap.get("name").toString(), newpwd);
					e.commit();
					Toast.makeText(PwdActivity.this, success, Toast.LENGTH_LONG).show();
					finish();
				}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){
					okButton.setEnabled(true);
					Toast.makeText(PwdActivity.this, readResp.getMessage().toString(), Toast.LENGTH_LONG).show();
				}
			}
		};
		pwdMap.put("name", userMap.get("name").toString());
		pwdMap.put("pwd", oldpwd);
		pwdMap.put("newPwd", newpwd);
		nameValuePairs.add(new BasicNameValuePair(pwdkey,gson.toJson(pwdMap)));
		new Thread(new AccessNetwork(url,nameValuePairs, h)).start();
		
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            	hideSoftWin();
            }
        }
        return super.onTouchEvent(event);
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK ){
			this.finish();
		}		
		return false;
	}
	
	public void hideSoftWin(){
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);  
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	
	
}
