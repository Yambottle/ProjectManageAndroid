package com.example.projectmanagesec.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.projectmanagesec.R;
import com.example.projectmanagesec.common.Parameter;
import com.example.projectmanagesec.fragment.AnalysisFragment;
import com.example.projectmanagesec.fragment.LogFragment;
import com.example.projectmanagesec.fragment.WorkFragment;
import com.example.projectmanagesec.widget.CustomViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectActivity extends FragmentActivity{
	
	private TextView titleTextView;
	private ImageButton backButton;
	private Button analysisButton;
	
	private Map<String, Object> projectMap;
	private Map<String, Object> dataMap;
	
	public CustomViewPager mViewPager;
	private List<Fragment> fragmentList;
	private Fragment analysisFragment,logFragment,workFragment;
	private String[] fragName = {"","产值统计","工程名称","工程日志"};
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		String dataString = (String)getIntent().getStringExtra("projectMap");
        dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
        projectMap = (Map<String, Object>) dataMap.get("project");
        
        titleTextView = (TextView) findViewById(R.id.title);
        fragName[2] = projectMap.get("name").toString();
		backButton = (ImageButton) findViewById(R.id.back);
		analysisButton = (Button) findViewById(R.id.analysis);
		
		initLog();
		initAnalysis();
		initWork();
		mViewPager=(CustomViewPager) findViewById(R.id.pager);
		fragmentList=new ArrayList<Fragment>();
		fragmentList.add(analysisFragment);
		fragmentList.add(workFragment);
		fragmentList.add(logFragment);
		mViewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
		mViewPager.setCurrentItem(1, true);
		toWork();
		initEvent();
	}
	
	private void initAnalysis(){
		analysisFragment = new AnalysisFragment();
	}
	
	private void initLog(){
		logFragment = new LogFragment();
	}
	
	private void initWork(){
		workFragment = new WorkFragment();
	}
	
	public class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

		public MyFrageStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragmentList.size(); 
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public void finishUpdate(ViewGroup container){
			super.finishUpdate(container);
			switch (mViewPager.getCurrentItem()) {
			case 0:
				toAnalysis();
				break;
			case 1:
				toWork();
				break;
			case 2:
				toLog();
				break;
			}
		}
		
		
	}
	
	public void toAnalysis(){
		titleTextView.setText(fragName[1]);
		if(dataMap.get("permission").toString().equals("用户")){
			analysisButton.setVisibility(View.GONE);
		}else{
			analysisButton.setVisibility(View.VISIBLE);
		}
		backButton.setVisibility(View.GONE);
	}
		
	public void toLog(){
		titleTextView.setText(fragName[3]);
		analysisButton.setVisibility(View.GONE);
		backButton.setVisibility(View.GONE);
	}
		
	public void toWork(){
		titleTextView.setText(fragName[2]);
		analysisButton.setVisibility(View.GONE);
		backButton.setVisibility(View.VISIBLE);
	}
	
	private void initEvent(){
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hideSoftWin();
				AlertDialog isExit = new AlertDialog.Builder(ProjectActivity.this)
	  			.setTitle("系统提示")
	  			.setMessage("确定要返回吗?")
	  			.setPositiveButton("确定", listener)
	  			.setNegativeButton("取消", listener)
	  			.create();
	  			isExit.show();
			}
		});

		analysisButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
		        //toChart
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("project", (Serializable)projectMap);
				intent.putExtras(bundle);
				intent.setClass(ProjectActivity.this, ChartActivity.class);
				startActivity(intent);
			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				MyFrageStatePagerAdapter myAdapter = (MyFrageStatePagerAdapter)mViewPager.getAdapter();
				for(int i=0;i<2;i++){
					if(i==position){
						myAdapter.getItem(position).setUserVisibleHint(true);
					}else {
						myAdapter.getItem(position).setUserVisibleHint(false);
					}
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				hideSoftWin();
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		mViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent arg1) {
				if(!(view instanceof EditText)){
					hideSoftWin();
				}
				return false;
			}
		});
	}
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
  		public void onClick(DialogInterface dialog, int which){
  			switch (which){
  			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
  				finish();
  				break;
  			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
  				break;
  			default:
  				break;
  			}
  		}
  	};
	
	//hideSoftWin
		@Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
	            	hideSoftWin();
	            }
	        }
	        return super.onTouchEvent(event);
	    }
		
		public void hideSoftWin(){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				try{
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}catch(NullPointerException e){
					if(imm.isActive()){
						//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					}
					e.printStackTrace();
				}
			} 
		}
		
		@Override
	  	public boolean onKeyDown(int keyCode, KeyEvent event){
			AlertDialog isExit = new AlertDialog.Builder(ProjectActivity.this)
  			.setTitle("系统提示")
  			.setMessage("确定要返回吗?")
  			.setPositiveButton("确定", listener)
  			.setNegativeButton("取消", listener)
  			.create();
  			isExit.show();
			return false;
	  	}
}
