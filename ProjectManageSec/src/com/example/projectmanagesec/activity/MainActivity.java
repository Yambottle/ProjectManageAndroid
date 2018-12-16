package com.example.projectmanagesec.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.example.projectmanagesec.http.AccessNetwork;
import com.example.projectmanagesec.http.ReadResp;
import com.example.projectmanagesec.map.MapInitialization;
import com.example.projectmanagesec.thread.applyFresher;
import com.example.projectmanagesec.R;
import com.example.projectmanagesec.common.Parameter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean isCreated = false;
	private boolean isRefresh = false;
	
	//applyFresher
	private Thread applyThread;
	private applyFresher aFresher;
	
	//menu
	private Button searchButton ;
	
	//listview
	private Handler handler;
	private PullToRefreshListView lv;
	public static SimpleAdapter sa;
	private List<Map<String,Object>> dataList;
	
	//data from login
	private String dataString;
	private Map<String, Object> dataMap;
	private ArrayList<Map<String, Object>> inProList;//green
	private ArrayList<Map<String, Object>> notInProList;//yellow
	private ArrayList<Map<String, Object>> notInPermisProList;//red
	private ArrayList<Map<String, Object>> applyList;//to apply
	private ArrayList<Map<String, Object>> readList;//to read
	private Map<String, Object> userMap ;//to me
	private List<Map<String, Object>> departList;
	private SharedPreferences sp;
	private Editor editor;
	private Gson gson;
	private Point centerPoint;
	
	//join&load interface
	private String projectkey = "projectName";
	private String userkey = "userName";
	private String joinUrl =Parameter.url+ "project.do?method=join";
	private String loadkey = "user";
	private String loadUrl =Parameter.url+ "project.do?method=load";
	private String freshUrl = Parameter.url+ "user.do?method=login";
	private String applyUrl = Parameter.url+ "project.do?method=checkJoin";
	private List<NameValuePair> nameValuePairs;
	
	//progressdialog
	private ProgressDialog progressDialog;
	private String progressInfo = "正在请求...";
	
	//respones
	private ReadResp readResp;
	private String projectDataString;
	
	//map
	private MapView mMapView;
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private BitmapDescriptor redBitmapDescriptor,greenBitmapDescriptor,yellowBitmapDescriptor ;
    private Button button;
    private InfoWindow mInfoWindow ;
    private String proName ;
    private Marker yellowChangedMarker;//after create project

    private Button accountButton, applyButton, createButton,findButton;
    
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		isCreated = true;
		
		searchButton = (Button) findViewById(R.id.search);
		
		lv=(PullToRefreshListView) findViewById(R.id.proLV);
		lv.setMode(Mode.PULL_FROM_START);  
		lv.getLoadingLayoutProxy(true, false).setPullLabel("");  
		lv.getLoadingLayoutProxy(true, false).setRefreshingLabel("");  
		lv.getLoadingLayoutProxy(true, false).setReleaseLabel("");
		handler=new Handler();
		dataList =new ArrayList<Map<String, Object>>();
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		redBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.point48);
		greenBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.point48_green);
		yellowBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.point48_yellow);
		
		accountButton = (Button) findViewById(R.id.account_manage);
		applyButton = (Button) findViewById(R.id.check_apply);
		createButton = (Button) findViewById(R.id.create_project);
		findButton = (Button) findViewById(R.id.find_contract);
		
		dataString = (String)getIntent().getStringExtra("data");
		dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
		inProList = (ArrayList<Map<String,Object>>) dataMap.get("inProjects");
		notInProList = (ArrayList<Map<String,Object>>) dataMap.get("notInProjects");
		notInPermisProList = (ArrayList<Map<String,Object>>) dataMap.get("notInPermission");
		sp = getSharedPreferences(Parameter.SP_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		gson = new Gson();
		
		applyList = (ArrayList<Map<String,Object>>) dataMap.get("checkJoinList");
		readList = (ArrayList<Map<String,Object>>) dataMap.get("checkReadList");
		if(readList==null||readList.isEmpty()){
			applyButton.setText(applyButton.getText().toString()+"("+String.valueOf(applyList.size())+")");
		}else{
			applyButton.setText(applyButton.getText().toString()+"("+String.valueOf(applyList.size()+readList.size())+")");
		}
		userMap = (Map<String, Object>) dataMap.get("user");
		Editor e = MainActivity.this.getSharedPreferences(Parameter.SP_NAME, MODE_PRIVATE).edit();
		e.putString("truename", userMap.get("truename").toString());
		e.commit();
		
		departList = (List<Map<String, Object>>) dataMap.get("utds");
		
		nameValuePairs = new ArrayList<NameValuePair>();
		readResp = new ReadResp();
		
		initListView();
		initMapView();
		initPoint();
		initEvent();
		
		startFreshApply();
	}

	private void startFreshApply(){
		try{
			Handler applyHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(msg.obj.equals("1")){
						Handler h= new Handler(){
							@SuppressWarnings("unchecked")
							@Override
							public void handleMessage(Message msg) {
								String error = msg.getData().getString("error");
								if(error == null){
									String applyString = readResp.readResp(msg);
									if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){
										synchronized (this) {
											Map<String, Object> applyMap = new Gson().fromJson(applyString,  new TypeToken<Map<String,Object>>() {}.getType());
											applyList = (ArrayList<Map<String, Object>>) applyMap.get("checkJoinList");
											readList = (ArrayList<Map<String, Object>>) applyMap.get("checkReadList");
											applyButton.setText("查看申请"+"("+String.valueOf(applyList.size()+readList.size())+")");
										}
									}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){
										Toast.makeText(MainActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
									}
								}else{
									Toast.makeText(MainActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
								}		
							}
						};
						nameValuePairs.clear();
						Map<String, Object> freshSendMap = new HashMap<String, Object>();
						freshSendMap.put("name", userMap.get("name").toString());
						freshSendMap.put("pwd", userMap.get("pwd").toString());
						nameValuePairs.add(new BasicNameValuePair("user",new Gson().toJson(freshSendMap)));
						new Thread(new AccessNetwork(applyUrl,nameValuePairs,h)).start();
					}else if(msg.obj.equals("0")){
					}else{
						Toast.makeText(MainActivity.this, "申请列表刷新失败", Toast.LENGTH_LONG).show();
					}
				}
			};
			aFresher = new applyFresher(applyHandler);
			applyThread = new Thread(aFresher);
			applyThread.start();
		}catch(Exception ex){
			Toast.makeText(MainActivity.this, "申请列表刷新失败", Toast.LENGTH_LONG).show();
		}
	}
	
	// TODO initListView
	private void initListView() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				sa=new SimpleAdapter(
				MainActivity.this, getData(),R.layout.item_project_listview,//MainActivity.this
				new String[]{"item_title"}, 
				new int[]{R.id.item_title});
				lv.setAdapter(sa);
			}
		});
		lv.requestFocus();
	}
	
	private List<Map<String,Object>> getData() {
		if(inProList.isEmpty()){
			return dataList;
		}
		dataList.clear();
		for(Map<String,Object> listMap : inProList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("item_title", listMap.get("name"));
			dataList.add(map);
		}
		return dataList;
	}

	private void load(int position){
		progressDialog = ProgressDialog.show(MainActivity.this, null, progressInfo);
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					projectDataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("projectMap", projectDataString);
						bundle.putSerializable("userMap", (Serializable)userMap);
						bundle.putSerializable("departList", (Serializable)departList);
						intent.putExtras(bundle);
						intent.setClass(MainActivity.this, ProjectActivity.class);
						startActivity(intent);
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(MainActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(MainActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(projectkey,dataList.get(position).get("item_title").toString()));
		nameValuePairs.add(new BasicNameValuePair(loadkey,new Gson().toJson(userMap)));
		new Thread(new AccessNetwork(loadUrl,nameValuePairs, h)).start();
	}
	
	// TODO initMap
	private void initMapView() {
		
		//remove widget
		if(!isCreated){
			mMapView.removeViewAt(1);//logo
		}
    	mMapView.showZoomControls(false);//zoom
    	//init map
    	mBaiduMap = mMapView.getMap();
    	mUiSettings = mBaiduMap.getUiSettings();
    	mUiSettings.setCompassEnabled(false);//compass
    	mUiSettings.setOverlookingGesturesEnabled(true);
    	//init map status       
        String spLaLnString = sp.getString(userMap.get("name").toString()+"mainPosition", "");
        LatLng stateLatLng;
    	if(spLaLnString.equals("")){
    		stateLatLng = new LatLng(36.072982329734174, 120.38889101891933);
    	}else{
    		stateLatLng = gson.fromJson(spLaLnString, new TypeToken<LatLng>(){}.getType());
    	}
    	MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(stateLatLng, sp.getFloat(userMap.get("name").toString()+"mainZoom", 13.0f));
        mBaiduMap.setMapStatus(msu);
	}
	
	// TODO initPoint
	private void initPoint() {
		mBaiduMap.clear();
		for(Map<String, Object> pro : inProList){
			LatLng ll = (LatLng)new Gson().fromJson((String)pro.get("center"), new TypeToken<LatLng>() {}.getType());
			//marker
			Bundle bundle = new Bundle();
			bundle.putString("proname", (String)pro.get("name"));
			MarkerOptions mo = new MarkerOptions().
					position(ll).
					extraInfo(bundle).
					icon(greenBitmapDescriptor).zIndex(9).
					animateType(MarkerAnimateType.none);
			mBaiduMap.addOverlay(mo);
		}
		for(Map<String, Object> pro : notInProList){
			LatLng ll = (LatLng)new Gson().fromJson((String)pro.get("center"), new TypeToken<LatLng>() {}.getType());
			//marker
			Bundle bundle = new Bundle();
			bundle.putString("proname", (String)pro.get("name"));
			MarkerOptions mo = new MarkerOptions().
					position(ll).
					extraInfo(bundle).
					icon(yellowBitmapDescriptor).zIndex(9).
					animateType(MarkerAnimateType.none);
			mBaiduMap.addOverlay(mo);
		}
		for(Map<String, Object> pro : notInPermisProList){
			LatLng ll = (LatLng)new Gson().fromJson((String)pro.get("center"), new TypeToken<LatLng>() {}.getType());
			//marker
			Bundle bundle = new Bundle();
			bundle.putString("proname", (String)pro.get("name"));
			MarkerOptions mo = new MarkerOptions().
					position(ll).
					extraInfo(bundle).
					icon(redBitmapDescriptor).zIndex(9).
					animateType(MarkerAnimateType.none);
			mBaiduMap.addOverlay(mo);
		}
		
		//markerInfoButton
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				MainActivity.this.yellowChangedMarker = marker;
				proName = marker.getExtraInfo().getString("proname").toString();
				button = new Button(MainActivity.this);
				button.setBackgroundResource(R.drawable.popup);
				button.setText((String)marker.getExtraInfo().getString("proname"));
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), marker.getPosition(), -47,new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick() {
						
						for(Map<String, Object> proMap : inProList){
							if(proMap.get("name").equals(proName)){
								Map<String, Object> proListMap = new HashMap<String, Object>();
								proListMap.put("item_title", proName);
								load(dataList.indexOf(proListMap));
								return;
							}
						}
						
						AlertDialog isExit = new AlertDialog.Builder(MainActivity.this)
						.setTitle("发送申请")
						.setMessage("确定要发送申请吗?")
						.setPositiveButton("确定", dialoglistener)
						.setNegativeButton("取消", dialoglistener)
						.create();
						isExit.show();
					}
				});
				mBaiduMap.showInfoWindow(mInfoWindow);
				return false;
			}
		});
	}
	
	private DialogInterface.OnClickListener dialoglistener = new DialogInterface.OnClickListener(){
		public void onClick(DialogInterface dialog, int which){
			switch (which){
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				Handler h = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						readResp.readRespNoData(msg);
						if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){
							Toast.makeText(getApplicationContext(), readResp.getMessage(), Toast.LENGTH_LONG).show();
							yellowChangedMarker.setIcon(yellowBitmapDescriptor);
						}else{
							Toast.makeText(getApplicationContext(), readResp.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
				};
				nameValuePairs.clear();
				nameValuePairs.add(new BasicNameValuePair(projectkey, proName));
				nameValuePairs.add(new BasicNameValuePair(userkey, userMap.get("name").toString()));
				new Thread(new AccessNetwork(joinUrl,nameValuePairs, h)).start();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};
	
	// TODO afterCreatProject
    private void afterCreateProject(){
    	//Map change
        MapInitialization app = (MapInitialization) getApplication();
        if(app.getNewPoint()!=null){
        	Map<String, Object> proMap = new HashMap<String, Object>();
            proMap.put("name", app.getNewPoingName());
            proMap.put("center", new Gson().toJson(app.getNewPoint()));
            inProList.add(proMap);
            Map<String, Object> menuMap = new HashMap<String, Object>();
            menuMap.put("item_title", app.getNewPoingName());
            dataList.add(menuMap);
            sa=new SimpleAdapter(
    				MainActivity.this, dataList,R.layout.item_project_listview,//MainActivity.this
    				new String[]{"item_title"}, 
    				new int[]{R.id.item_title});
    		lv.setAdapter(sa);
    		mBaiduMap.clear();
            initPoint();
            app.setNewPoint(null);
            app.setNewPoingName(null);

        }
    }
	
    // TODO afterApply
    private void afterApply(){

    	//apply list
        MapInitialization app = (MapInitialization) getApplication();
        if(app.getApplyList()!=null){
        	applyList = (ArrayList<Map<String,Object>>)app.getApplyList();
        }
        
        //button changed
        applyButton.setText("查看申请("+applyList.size()+")");
    }
    
	// TODO initEvent
	private void initEvent() {
		//保存地图退出坐标
        mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent arg0) {
				try{
					editor.putString(userMap.get("name").toString()+"mainPosition", gson.toJson(getMapViewCenter()));
					editor.commit();
				}catch(NullPointerException e){
					return;
				}
			}
		});
        //保存地图退出缩放
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
			}
			
			@Override
			public void onMapStatusChangeFinish(MapStatus msu) {
				editor.putFloat(userMap.get("name").toString()+"mainZoom",msu.zoom);
				editor.commit();
			}
			
			@Override
			public void onMapStatusChange(MapStatus arg0) {
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//toProject
				load(position-1);
			}
		});
		
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				isRefresh = true;
				login();
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//toSearch
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("from", "main");
				bundle.putSerializable("in", (Serializable)inProList);
				bundle.putSerializable("notin", (Serializable)notInProList);
				bundle.putSerializable("notinper", (Serializable)notInPermisProList);
				bundle.putSerializable("user", (Serializable)userMap);
				bundle.putSerializable("departList", (Serializable)departList);
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});
		
		accountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//toAccount
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", (Serializable)userMap);
				bundle.putSerializable("utds", (Serializable)departList);
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, MeActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
		
		applyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//toApply
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("checkJoinList", applyList);
				bundle.putSerializable("checkReadList", readList);
				bundle.putSerializable("user", (Serializable)userMap);
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, ApplyActivity.class);
				startActivity(intent);
			}
		});
		
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//toCreate
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("departList", (Serializable)departList);
				bundle.putString("from", "main");
				bundle.putSerializable("user", (Serializable)userMap);
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, CreateProjectActivity.class);
				startActivity(intent);
			}
		});
		
		findButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//toContractLib
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("type", "all");
				bundle.putString("in", "main");
				bundle.putSerializable("user", (Serializable)userMap);
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, ContractActivity.class);
				startActivity(intent);
			}
		});

	}
	
	private LatLng getMapViewCenter(){
		centerPoint = new Point((int)(mMapView.getWidth()*1.0/2), (int)(mMapView.getBottom()-mMapView.getHeight()*1.0/2));
		LatLng ll = mBaiduMap.getProjection().fromScreenLocation(centerPoint);
		return ll;
	}
	
	//TODO fresh
	public void login(){
		progressDialog = ProgressDialog.show(MainActivity.this, null, progressInfo);
		Handler h= new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");
				if(error == null){
					String freshString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){
						dataMap = new Gson().fromJson(freshString,  new TypeToken<Map<String,Object>>() {}.getType());
						inProList = (ArrayList<Map<String,Object>>) dataMap.get("inProjects");
						notInProList = (ArrayList<Map<String,Object>>) dataMap.get("notInProjects");
						notInPermisProList = (ArrayList<Map<String,Object>>) dataMap.get("notInPermission");
						
						initListView();
						initMapView();
						initPoint();
						initEvent();
						
						if(isRefresh){
							lv.onRefreshComplete();
							isRefresh = false;
						}
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){
						Toast.makeText(MainActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(MainActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}		
			}
		};
		nameValuePairs.clear();
		Map<String, Object> freshSendMap = new HashMap<String, Object>();
		freshSendMap.put("name", userMap.get("name").toString());
		freshSendMap.put("pwd", userMap.get("pwd").toString());
		nameValuePairs.add(new BasicNameValuePair("user",new Gson().toJson(freshSendMap)));
		new Thread(new AccessNetwork(freshUrl,nameValuePairs,h)).start();
	}
	
	// TODO activityLifeCycle
	@Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
	protected void onStop() {
		super.onStop();
		if(applyThread!=null&&applyThread.isAlive()&&aFresher!=null){
        	aFresher.setRunning(false);
        }
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(applyThread!=null&&applyThread.isAlive()&&aFresher!=null){
        	aFresher.setRunning(true);
        }
	}

	@Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        initMapView();
        afterCreateProject();
        afterApply();
        if(isCreated){
        	login();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    
    // TODO Dialog Window
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event){
  		if (keyCode == KeyEvent.KEYCODE_BACK ){
  			// 创建退出对话框
  			AlertDialog isExit = new AlertDialog.Builder(this)
  			.setTitle("系统提示")
  			.setMessage("确定要退出吗?")
  			.setPositiveButton("确定", listener)
  			.setNegativeButton("取消", listener)
  			.create();
  			isExit.show();
  		}
  		return false;
  	}
  	/**监听对话框里面的button点击事件*/
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
}
