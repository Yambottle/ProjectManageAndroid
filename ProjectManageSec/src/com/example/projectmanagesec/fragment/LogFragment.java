package com.example.projectmanagesec.fragment;

import java.util.ArrayList;
import java.util.Date;
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
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class LogFragment extends Fragment{
	
	private boolean isCreated = false;
	private boolean isGetLog = false;
	
	private Button manButton,fullButton;
	
	private PullToRefreshListView logListView;
	private SimpleAdapter sa;
	private int type = 0;//0:pro/1:man
	private int offset = 0;
	private int limit = 15;
	private int stopNew = 0;
	private ArrayList<Map<String, Object>> logList;
	
	//request
	private String projectkey = "projectName";
	private String offsetkey = "offset";
	private String limitkey = "limit";
	private String getProUrl =Parameter.url+ "projectlog.do?method=get";
	private String userkey = "userName";
	private String getManUrl =Parameter.url+ "projectlog.do?method=getbyuser";
	private List<NameValuePair> nameValuePairs;
		
	//Response
	private ReadResp readResp;
	private Map<String, Object> projectMap, userMap;
	
	//progressDialog
	private ProgressDialog progressDialog;
	private String progressInfo = "正在请求...";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isCreated = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_log, null);
		String dataString = (String)getActivity().getIntent().getStringExtra("projectMap");
        Map<String, Object> dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
        projectMap = (Map<String, Object>) dataMap.get("project");
        userMap = (Map<String, Object>) getActivity().getIntent().getSerializableExtra("userMap");
        manButton = (Button) v.findViewById(R.id.man_log);
		fullButton = (Button) v.findViewById(R.id.full_log);
		resetButton();
		setButton(fullButton);
		
		logListView = (PullToRefreshListView) v.findViewById(R.id.logLV);
		logListView.setMode(Mode.BOTH);  
		logListView.getLoadingLayoutProxy(true, false).setPullLabel("放开以刷新...");  
		logListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");  
		logListView.getLoadingLayoutProxy(true, false).setReleaseLabel("下拉刷新...");
		logListView.getLoadingLayoutProxy(false, true).setPullLabel("放开以加载...");  
		logListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");  
		logListView.getLoadingLayoutProxy(false, true).setReleaseLabel("上拉加载...");
		logList = new ArrayList<Map<String,Object>>();
		sa = new SimpleAdapter(getActivity(), logList, R.layout.item_log_listview,
				new String[]{"logdate","logcontent"}, 
				new int[]{R.id.logdate,R.id.logcontent});
		logListView.setAdapter(sa);
		
		nameValuePairs = new ArrayList<NameValuePair>();
		readResp = new ReadResp();
		
		return v;
	}

	private void resetButton(){
		manButton.setBackground(getActivity().getResources().getDrawable(R.drawable.dialogbuttonshape));
		manButton.setTextColor(Color.WHITE);
		fullButton.setBackground(getActivity().getResources().getDrawable(R.drawable.dialogbuttonshape));
		fullButton.setTextColor(Color.WHITE);
	}
	
	private void setButton(Button button){
		button.setBackground(getActivity().getResources().getDrawable(R.drawable.dialogbuttonshape_white));
		button.setTextColor(Color.rgb(30, 41, 60));
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isCreated) {
	          return;
	      }
	      if (isVisibleToUser&&!isGetLog) {
	    	  isGetLog = true;
	    	  logList.clear();
	    	  initProListView();
	      }else{
	    	  isGetLog = false;
	      }
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initEvent();
	}
	
	//TODO initProListview
	private void initProListView(){
		progressDialog = ProgressDialog.show(getActivity(), null, progressInfo);
		Handler h = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					String dataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Map<String, Object> dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
				        List<Map<String, Object>> logPutList = (ArrayList<Map<String,Object>>)dataMap.get("list");
				        for(Map<String, Object> logMap : logPutList){
				        	Map<String, Object> putMap = new HashMap<String, Object>();
				        	Date date = new Date(Long.parseLong(logMap.get("date").toString()));
				        	putMap.put("logdate", DateFormat.format("MM-dd kk:mm", date));
				        	putMap.put("logcontent", logMap.get("logString").toString());
				        	logList.add(putMap);
				        }
						sa.notifyDataSetChanged();
						
						logListView.getRefreshableView().setSelection(offset);
						
						stopNew = logPutList.size();
						if(stopNew!=limit){
							Toast.makeText(getActivity(), "没有更多加载", Toast.LENGTH_LONG).show();
						}
				        //Toast.makeText(getActivity(),readResp.getMessage(), Toast.LENGTH_LONG).show();
						logListView.onRefreshComplete();
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(getActivity(),readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(projectkey,projectMap.get("name").toString()));
		nameValuePairs.add(new BasicNameValuePair(offsetkey,String.valueOf(offset)));
		nameValuePairs.add(new BasicNameValuePair(limitkey,String.valueOf(limit)));
		new Thread(new AccessNetwork(getProUrl,nameValuePairs, h)).start();
	}
	
	//TODO initManListview
	private void initManListView() {
		progressDialog = ProgressDialog.show(getActivity(), null, progressInfo);
		Handler h = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					String dataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Map<String, Object> dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
				        List<Map<String, Object>> logPutList = (ArrayList<Map<String,Object>>)dataMap.get("list");
				        for(Map<String, Object> logMap : logPutList){
				        	Map<String, Object> putMap = new HashMap<String, Object>();
				        	Date date = new Date(Long.parseLong(logMap.get("date").toString()));
				        	putMap.put("logdate", DateFormat.format("MM-dd kk:mm", date));
				        	putMap.put("logcontent", logMap.get("logString").toString());
				        	logList.add(putMap);
				        }
				        sa.notifyDataSetChanged();
						
						logListView.getRefreshableView().setSelection(offset);
						
						stopNew = logPutList.size();
						if(stopNew!=limit){
							Toast.makeText(getActivity(), "没有更多加载", Toast.LENGTH_LONG).show();
						}
				        //Toast.makeText(getActivity(),readResp.getMessage(), Toast.LENGTH_LONG).show();
						logListView.onRefreshComplete();
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(getActivity(),readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(projectkey,projectMap.get("name").toString()));
		nameValuePairs.add(new BasicNameValuePair(userkey, userMap.get("name").toString()));
		nameValuePairs.add(new BasicNameValuePair(offsetkey,String.valueOf(offset)));
		nameValuePairs.add(new BasicNameValuePair(limitkey,String.valueOf(limit)));
		new Thread(new AccessNetwork(getManUrl,nameValuePairs, h)).start();
	}
	
	//TODO initEvent
	private void initEvent(){
		manButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				type = 1;
				logList.clear();
				offset = 0;
				resetButton();
				setButton(manButton);
				initManListView();
			}
		});
		
		fullButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				type = 0;
				logList.clear();
				offset = 0;
				resetButton();
				setButton(fullButton);
				initProListView();
			}
		});
		
		logListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				offset = 0;
				logList.clear();
				if(type == 0){
	        		initProListView();
	        	}else{
	        		initManListView();
	        	}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				offset += limit;
        		if(type == 0){
        			initProListView();
        		}else{
        			initManListView();
        		}
			}
			
		});	

	}	

}
