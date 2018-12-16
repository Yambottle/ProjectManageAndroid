package com.example.projectmanagesec.activity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.model.LatLng;
import com.example.projectmanagesec.http.AccessNetwork;
import com.example.projectmanagesec.http.ReadResp;
import com.example.projectmanagesec.map.MapInitialization;
import com.example.projectmanagesec.common.Parameter;
import com.example.projectmanagesec.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProjectActivity extends Activity implements OnMapTouchListener, OnTouchListener {

	private String fromString ;
	
	//step
	private int steps = 1;
	private String[] step ={"","输入工程信息","选择中心点","绘制路线"};
	private String[] buttonStatus ={"下一步","保存"};
	
	//map
	private MapView mMapView;
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private FrameLayout mapLayout;
    private ImageView pointView;
	private RelativeLayout whiteLayout ;
    
    //draw
    private LinearLayout buttonLayout;
    private ImageButton startButton, resetButton, clearButton;
    public ArrayList<Polyline> polylines;
    public Polyline dottedPolyLine;
    private Point point1, point2, point3;
    boolean drawFlag = false;
    private TextView widthTextView ;
    private SeekBar widthSeekBar;
    private int lineWidth = 10;
    
    //request info
	private String proName="",profullName="",proEndDate="",proRate="",proCutRate="",proDes="";
	private long endDate;
	private LatLng proLatLng;
	public ArrayList<PolylineOptions> polyoptions;
	
	//progressdialog
	private ProgressDialog progressDialog;
	private String progressInfo = "正在请求...";
	
	//save interface
    private String createUrl =Parameter.url+ "project.do?method=save";
    private String prokey = "project";
    private String mapkey = "map";
    private String pointkey = "center";
    private String updateUrl =Parameter.url+ "project.do?method=update";
    private String proNamekey = "projectName";
    private String userkey = "user";
    private String loadUrl =Parameter.url+ "project.do?method=load";
    private List<NameValuePair> nameValuePairs;
	private Gson gson;
	
	//response info
	private ReadResp readResp ;
	private Map<String, Object> proDataMap;
	private ArrayList<Map<String,Object>> utp_pro_list;

	private ImageButton backButton;
	private TextView titleTextView;
	private Button saveButton;
	private Button firstStepButton;
	private LinearLayout editLayout;
	private EditText proNameText,proFullNameText,proRateText,proCutRateText,proDesText;
	private EditText proEndDateText;
	private DatePicker datePicker;
	private Spinner departSpinner;
	private SimpleAdapter departsa;
	private List<Map<String, Object>> departDataList;
	private List<Map<String, Object>> departList;
	private String departString;
	
	private Map<String, Object> projectMap;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_project);
		
		fromString = getIntent().getStringExtra("from");
		
		whiteLayout = (RelativeLayout) findViewById(R.id.whiteWall);
		
		backButton = (ImageButton) findViewById(R.id.back);
		titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(step[steps]);
		saveButton = (Button) findViewById(R.id.save);
		saveButton.setText(buttonStatus[0]);
		saveButton.setVisibility(View.GONE);
		firstStepButton = (Button) findViewById(R.id.firststep);
		editLayout = (LinearLayout) findViewById(R.id.editparent);
		proNameText = (EditText) findViewById(R.id.proname);
		proFullNameText = (EditText) findViewById(R.id.profullname);
		proEndDateText = (EditText) findViewById(R.id.proenddate);
		proEndDateText.setInputType(0);
		proRateText = (EditText) findViewById(R.id.prorate);
		proRateText.setInputType(InputType.TYPE_CLASS_PHONE);
		proCutRateText = (EditText) findViewById(R.id.procutrate);
		proCutRateText.setInputType(InputType.TYPE_CLASS_PHONE);
		proDesText = (EditText) findViewById(R.id.prodes);
		proNameText.requestFocus();
		datePicker = (DatePicker) findViewById(R.id.proenddatepicker);
		Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int monthOfYear=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener(){
            public void onDateChanged(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
            	proEndDate = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
            	proEndDateText.setText(proEndDate);
            }
            
        });
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);  
        datePicker.setVisibility(View.GONE);
        departSpinner = (Spinner) findViewById(R.id.departSp);
        departDataList = (List<Map<String,Object>>)getIntent().getSerializableExtra("departList");
        departList = new ArrayList<Map<String, Object>>();
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mapLayout = (FrameLayout) findViewById(R.id.mapparent);
		
		buttonLayout = (LinearLayout) findViewById(R.id.buttongroup);
		startButton = (ImageButton) findViewById(R.id.draw);
    	resetButton = (ImageButton) findViewById(R.id.reset);
    	clearButton = (ImageButton) findViewById(R.id.clear);
    	polylines = new ArrayList<Polyline>();//save lines
    	polyoptions = new ArrayList<PolylineOptions>();
		widthSeekBar = (SeekBar) findViewById(R.id.widthbar);
		widthSeekBar.setProgress(lineWidth);
		widthTextView = (TextView) findViewById(R.id.linewidth);
    	
    	proDataMap = new HashMap<String, Object>();//save info:point,lines
    	nameValuePairs = new ArrayList<NameValuePair>();
		gson = new Gson();
		readResp = new ReadResp();
    	
		if(!fromString.equals("main")){
			projectMap = (Map<String, Object>) getIntent().getSerializableExtra("project");
			proNameText.setText(projectMap.get("name").toString());
			proFullNameText.setText(projectMap.get("fullName").toString());
			proEndDateText.setText(DateFormat.format("yyyy-MM-dd", new Date(Long.parseLong(projectMap.get("date").toString()))));
			proRateText.setText(projectMap.get("expectProfit").toString().replace("%", ""));
			if(projectMap.get("cut")!=null){
				proCutRateText.setText(projectMap.get("cut").toString().replace("%", ""));
			}else{
				proCutRateText.setText("");
			}
			proDesText.setText(projectMap.get("remark").toString());
		}
		
		initMapView();
		getDepartData();
		initDepartSpinner();
		initEvent();

	}

	private void initMapView() {
		// TODO Auto-generated method stub
		//remove widget
		mMapView.removeViewAt(1);//logo
		mMapView.showZoomControls(false);//zoom
		//init map
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setCompassEnabled(true);//compass
		mUiSettings.setOverlookingGesturesEnabled(false);
		//init map status
		LatLng ll;
		if(fromString.equals("main")){
			ll = new LatLng(36.072982329734174, 120.38889101891933);
		}else{
			ll = gson.fromJson(projectMap.get("center").toString(), new TypeToken<LatLng>(){}.getType());
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 13);
		mBaiduMap.setMapStatus(msu);
	}

	private void initDepartSpinner(){
		departSpinner.post(new Runnable() {
			@Override
			public void run() {
				departsa=new SimpleAdapter(
				CreateProjectActivity.this, departList,R.layout.item_order_spinner,
				new String[]{"item_title"}, 
				new int[]{R.id.item_title});
				departSpinner.setAdapter(departsa);
			}
		});
		
	}
	
	private List<Map<String,Object>> getDepartData(){
		Map<String, Object> departFirst = new HashMap<String, Object>();
		departFirst.put("item_title", "请选择部门");
		departList.add(departFirst);
		for(Map<String, Object> departMap : departDataList){
			Map<String, Object> departFlagMap = new HashMap<String, Object>();
			departFlagMap.put("item_title", departMap.get("departmentName"));
			departList.add(departFlagMap);
		}
		return departList;
	}
	
	private void initEvent() {
		// TODO Auto-generated method stub
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog isExit = new AlertDialog.Builder(CreateProjectActivity.this)
	  			.setTitle("系统提示")
	  			.setMessage("确定要返回吗?")
	  			.setPositiveButton("确定", listener)
	  			.setNegativeButton("取消", listener)
	  			.create();
	  			isExit.show();
			}
		});
		
		firstStepButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				proName = proNameText.getText().toString();
				profullName = proFullNameText.getText().toString();
				proEndDate = proEndDateText.getText().toString();
				proRate = proRateText.getText().toString();
				proCutRate = proCutRateText.getText().toString();
				proDes = proDesText.getText().toString();
				if(proName.equals("")||profullName.equals("")||proRate.equals("")||departString.equals("请选择部门")){
					Toast.makeText(CreateProjectActivity.this, "请输入全部信息", Toast.LENGTH_LONG).show();
					return;
				}
				if (proEndDate.equals("")) {
					Toast.makeText(CreateProjectActivity.this, "请选择日期",
							Toast.LENGTH_LONG).show();
					return;
				}
				Pattern p1 = Pattern.compile("[0-9]*"); 
                Matcher m1 = p1.matcher(proRateText.getText().toString());
                Pattern p2 = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$"); 
                Matcher m2 = p2.matcher(proRateText.getText().toString());
                Pattern p3 = Pattern.compile("[0-9]*"); 
                Matcher m3 = p3.matcher(proCutRateText.getText().toString());
                Pattern p4 = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$"); 
                Matcher m4 = p4.matcher(proCutRateText.getText().toString());
                if(!m1.matches()&&!m2.matches()){
             		Toast.makeText(CreateProjectActivity.this, "利润率请输入数字", Toast.LENGTH_LONG).show();
             		return;
             	}
                if(!m3.matches()&&!m4.matches()){
             		Toast.makeText(CreateProjectActivity.this, "取费率请输入数字", Toast.LENGTH_LONG).show();
             		return;
             	}
                hideSoftWin();
				inputName();
			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (steps) {
				case 2:
					choosePoint();
					break;
				case 3:
					sendInfo();
					break;
				}
			}
		});
		
		startButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			if(drawFlag){
    				startButton.setImageDrawable(getResources().getDrawable(R.drawable.paintbrush72));
    				mUiSettings.setAllGesturesEnabled(true);
        			drawFlag = false;
    			}else{
    				startButton.setImageDrawable(getResources().getDrawable(R.drawable.check72));
    				mUiSettings.setAllGesturesEnabled(false);
        			drawFlag = true;
    			}
    		}
    	});
    	resetButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			if(!polylines.isEmpty() && !polyoptions.isEmpty()){
    				polylines.get(polylines.size()-1).remove();
    				polylines.remove(polylines.get(polylines.size()-1));
    				polyoptions.remove(polyoptions.get(polyoptions.size()-1));
    			}
    		}
    	});
    	clearButton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			AlertDialog isExit = new AlertDialog.Builder(CreateProjectActivity.this)
	  			.setTitle("系统提示")
	  			.setMessage("确定要清空吗?")
	  			.setPositiveButton("确定", clearlistener)
	  			.setNegativeButton("取消", clearlistener)
	  			.create();
	  			isExit.show();
    		}
    	});
    	
    	widthSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int position, boolean fromUser) {
				if(fromUser){
					widthTextView.setText("宽度："+String.valueOf(position));
					lineWidth = position;
				}
			}
		});
    	
    	departSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				Map<String, Object> kindMap = (HashMap<String, Object>)departSpinner.getSelectedItem();
				departString = kindMap.get("item_title").toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
    	
    	mBaiduMap.setOnMapTouchListener(this);
    	
    	proNameText.setOnTouchListener(this);
    	proFullNameText.setOnTouchListener(this);
    	proEndDateText.setOnTouchListener(this);
    	proRateText.setOnTouchListener(this);
    	proCutRateText.setOnTouchListener(this);
    	proDesText.setOnTouchListener(this);
	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
  		public void onClick(DialogInterface dialog, int which){
  			switch (which){
  			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
  				if(!fromString.equals("main")){
					load();
				}else{
					hideSoftWin();
					finish();
				}
  				break;
  			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
  				break;
  			default:
  				break;
  			}
  		}
  	};
	
  	DialogInterface.OnClickListener clearlistener = new DialogInterface.OnClickListener(){
  		public void onClick(DialogInterface dialog, int which){
  			switch (which){
  			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
  				mBaiduMap.clear();
    			polylines.clear();
    			polyoptions.clear();
  				break;
  			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
  				break;
  			default:
  				break;
  			}
  		}
  	};
	@SuppressWarnings("unchecked")
	private void load(){
		progressDialog = ProgressDialog.show(CreateProjectActivity.this, null, progressInfo);
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					String projectDataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("projectMap", projectDataString);
						bundle.putSerializable("userMap", (Serializable)getIntent().getSerializableExtra("user"));
						bundle.putSerializable("departList", (Serializable)departDataList);
						intent.putExtras(bundle);
						intent.setClass(CreateProjectActivity.this, ProjectActivity.class);
						startActivity(intent);
						hideSoftWin();
						finish();
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(CreateProjectActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(CreateProjectActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(userkey, gson.toJson((Map<String, Object>)getIntent().getSerializableExtra("user"))));
		nameValuePairs.add(new BasicNameValuePair(proNamekey, ((Map<String, Object>)getIntent().getSerializableExtra("project")).get("name").toString()));
		new Thread(new AccessNetwork(loadUrl,nameValuePairs, h)).start();
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent arg1) {
		switch (view.getId()) {
		case R.id.proenddate:
			hideSoftWin();
			datePicker.setVisibility(View.VISIBLE);
			break;
		case R.id.proname:
		case R.id.profullname:
		case R.id.prorate:
		case R.id.prodes:
			datePicker.setVisibility(View.GONE);
			break;
		}
		return false;
	}
	
	//draw
	@Override
	public void onTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
  			point1 = getPointXY(event);
  		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
  			point3 = getPointXY(event);
  			if(drawFlag){
  				drawDottedLine(point1, point3);
  			}
  		}else if(event.getAction() == MotionEvent.ACTION_UP){
  			point2 = getPointXY(event);
  			if(drawFlag){
  				drawLine(point1, point2);
  				if(dottedPolyLine != null)
  					dottedPolyLine.remove();
  			}
  		}
	}
	
	public Point getPointXY(MotionEvent event){
  		Point point = new Point();
  		point.x = (int) event.getX();
  		point.y = (int) event.getY();
  		return point;
  	}
	
	public void drawLine(Point point1, Point point2){
  		List<LatLng> points = new ArrayList<LatLng>();
          points.add(coorConvert(point1));
          points.add(coorConvert(point2));
          PolylineOptions PolylineOptions = new PolylineOptions().width(lineWidth)
                  .color(0xAAFF0000).points(points);
          Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(PolylineOptions);
          polylines.add(mPolyline);
          polyoptions.add(PolylineOptions);
  	}
  	
  	public void drawDottedLine(Point point1, Point point3){
  		if(dottedPolyLine != null){
  			dottedPolyLine.remove();
  		}
  		List<LatLng> points = new ArrayList<LatLng>();
          points.add(coorConvert(point1));
          points.add(coorConvert(point3));
          PolylineOptions PolylineOptions = new PolylineOptions().width(lineWidth)
                  .color(0xAAFF0000).points(points);
          dottedPolyLine = (Polyline) mBaiduMap.addOverlay(PolylineOptions);
          dottedPolyLine.setDottedLine(true);
  	}
	
  	public LatLng coorConvert(Point point){
  		return mBaiduMap.getProjection().fromScreenLocation(point);
  	}
  	
	private void choosePoint() {
		// TODO Auto-generated method stub
		//save point
		Point proPoint = new Point((int)(mMapView.getWidth()*1.0/2), (int)(mMapView.getBottom()-mMapView.getHeight()*1.0/2));
	    proLatLng = coorConvert(proPoint);
	    
	    ++steps;
		titleTextView.setText(step[steps]);
		pointView.setVisibility(View.GONE);
		saveButton.setText(buttonStatus[1]);
		
		if(!fromString.equals("main")){
			initLines();
		}
		buttonLayout.setVisibility(View.VISIBLE);
	}

	private void initLines(){
		ArrayList<PolylineOptions> options = gson.fromJson(projectMap.get("map").toString(), new TypeToken<ArrayList<PolylineOptions>>(){}.getType());
		for(PolylineOptions option : options){
			Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(option);
	        polylines.add(mPolyline);
	        polyoptions.add(option);
		}
	}
	
	private void inputName() {
			// TODO Auto-generated method stub
			whiteLayout.setVisibility(View.GONE);
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
				Date date = dateFormat.parse(proEndDate);
				endDate = date.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			++steps;
			titleTextView.setText(step[steps]);
			editLayout.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);

			pointView = new ImageView(CreateProjectActivity.this);
			pointView.setImageDrawable(getResources().getDrawable(R.drawable.point48));
			FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pointParams.gravity = Gravity.CENTER;
			mapLayout.addView(pointView, pointParams);
			Toast.makeText(CreateProjectActivity.this, "请精确选择工程中心点，将比例尺缩放到最小", Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	private void sendInfo(){
		// TODO Auto-generated method stub
		if(!fromString.equals("main")){
			proDataMap.put("id", ((Map<String, Object>)getIntent().getSerializableExtra("project")).get("id").toString());
		}
		proDataMap.put("name",proName);
		proDataMap.put("fullName",profullName);
		proDataMap.put("expectEndDate",endDate);
		proDataMap.put("expectProfit",proRate);
		proDataMap.put("cut",proCutRate);
		proDataMap.put("remark",proDes);
		proDataMap.put("departmentName", departString);
		proDataMap.put("creator",((Map<String, Object>)getIntent().getSerializableExtra("user")).get("name").toString() );
		progressDialog = ProgressDialog.show(CreateProjectActivity.this, null, progressInfo);
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");
				if(error == null){
					String dataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){
						if(fromString.equals("main")){
							Map<String, Object>dataMap = new Gson().fromJson(dataString,  new TypeToken<Map<String,Object>>() {}.getType());
							utp_pro_list = (ArrayList<Map<String,Object>>) dataMap.get("list");
							
							MainActivity.sa=new SimpleAdapter(
									CreateProjectActivity.this, getData(),R.layout.item_project_listview,//MainActivity.this
									new String[]{"item_title"}, 
									new int[]{R.id.item_title});
							
							MapInitialization app = (MapInitialization) getApplication();
							app.setNewPoint(proLatLng);
							app.setNewPoingName(proName);
						}
						
						progressDialog.dismiss();
						finish();
						Toast.makeText(CreateProjectActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){
						saveButton.setEnabled(true);
						Toast.makeText(CreateProjectActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					saveButton.setEnabled(true);
					Toast.makeText(CreateProjectActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}		
			}
		};
		nameValuePairs.add(new BasicNameValuePair(prokey, gson.toJson(proDataMap)));
		nameValuePairs.add(new BasicNameValuePair(mapkey, gson.toJson(polyoptions)));
		nameValuePairs.add(new BasicNameValuePair(pointkey, gson.toJson(proLatLng)));
		if(fromString.equals("main")){
			new Thread(new AccessNetwork(createUrl,nameValuePairs, h)).start();
		}else{
			nameValuePairs.add(new BasicNameValuePair(userkey, gson.toJson((Map<String, Object>)getIntent().getSerializableExtra("user"))));
			nameValuePairs.add(new BasicNameValuePair(proNamekey, ((Map<String, Object>)getIntent().getSerializableExtra("project")).get("name").toString()));
			new Thread(new AccessNetwork(updateUrl,nameValuePairs, h)).start();
		}
		
		saveButton.setClickable(false);
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> getData() {
		if(utp_pro_list.isEmpty()){
			return null;
		}
		ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for(Map<String,Object> listMap : utp_pro_list){
			Map<String,Object> projectMap = (Map<String,Object>)listMap.get("project");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("item_title", projectMap.get("name"));
			dataList.add(map);
		}
		return dataList;
	}
	
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
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				e.printStackTrace();
			}
		} 
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
