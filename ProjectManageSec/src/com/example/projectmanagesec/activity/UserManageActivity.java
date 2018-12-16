package com.example.projectmanagesec.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.projectmanagesec.R;
import com.example.projectmanagesec.common.Parameter;
import com.example.projectmanagesec.common.PermissionEnum;
import com.example.projectmanagesec.http.AccessNetwork;
import com.example.projectmanagesec.http.ReadResp;
import com.example.projectmanagesec.map.MapInitialization;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UserManageActivity extends Activity {

	private ImageButton backButton;
	
	private ListView empListView;
	private MySimpleAdapter sa;
	private ArrayList<Map<String, Object>> dataList;
	private ArrayList<Map<String, Object>> userList;
	
	private Spinner kindSpinner;
	private Handler kindhandlerSpinner;
	private SimpleAdapter kindsaSpinner;
	private String kindString;
	
	private String proNamekey = "projectName";
	private String getUrl = Parameter.url+ "project.do?method=getProjectUsers";
	private String userkey = "user";
	private String utpkey = "utp";
	private String sendUrl = Parameter.url+ "project.do?method=updatePermisson";
	private String userNameKey = "userName";
	private String refuseUrl =Parameter.url+ "project.do?method=refuse";
	private List<NameValuePair> nameValuePairs;
	private SharedPreferences sp;
	
	private Dialog checkDialog;
	
	//progressdialog
	private ProgressDialog progressDialog;
	private String progressInfo = "正在请求...";
			
	//respones
	private ReadResp readResp;
	private Gson gson;
	
	private Map<String, Object> userMap, projectMap;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_manage);
		
		userMap = (Map<String, Object>)getIntent().getSerializableExtra("user");
		projectMap = (Map<String, Object>)getIntent().getSerializableExtra("project");
		
		backButton = (ImageButton) findViewById(R.id.back);
		
		empListView = (ListView) findViewById(R.id.userlist);
		dataList = new ArrayList<Map<String,Object>>(); 
		
		kindhandlerSpinner = new Handler();
		
		nameValuePairs = new ArrayList<NameValuePair>();
		readResp = new ReadResp();
		gson = new Gson();
		sp = getSharedPreferences(Parameter.SP_NAME, Context.MODE_PRIVATE);
		
		initListView();
		initEvent();
	}
	
	//TODO initEvent
	private void initEvent(){
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	//TODO initListView
	private void initListView(){
		progressDialog = ProgressDialog.show(UserManageActivity.this, null, progressInfo);
		Handler h = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					String dataString = readResp.readResp(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Map<String, Object> dataMap= gson.fromJson(dataString, new TypeToken<HashMap<String, Object>>(){}.getType());
						userList = (ArrayList<Map<String, Object>>) dataMap.get("utps");
						dataList.clear();
						for(Map<String, Object> flagMap : userList){
							Map<String, Object> putMap = new HashMap<String, Object>();
							putMap.put("contract_item_id_s", "名字：");
							putMap.put("contract_item_id", flagMap.get("userTruename").toString());
							putMap.put("item_des_s", "角色：");
							putMap.put("item_des", flagMap.get("permission"));
							if(flagMap.get("permission").toString().equals("创建者")){
								putMap.put("permiss", 6);
								flagMap.put("permiss", 6);
							}else if(flagMap.get("permission").toString().equals("上级部门")){
								putMap.put("permiss", 5);
								flagMap.put("permiss", 5);
							}else if(flagMap.get("permission").toString().equals("管理员")){
								putMap.put("permiss", 4);
								flagMap.put("permiss", 4);
							}else if(flagMap.get("permission").toString().equals("申请者")){
								putMap.put("permiss", 3);
								flagMap.put("permiss", 3);
							}else if(flagMap.get("permission").toString().equals("用户")){
								putMap.put("permiss", 2);
								flagMap.put("permiss", 2);
							}
							putMap.put("phone_s", "电话：");
							if(flagMap.get("phone")==null||flagMap.get("phone").toString().equals("")){
								putMap.put("phone", "--");
							}else{
								putMap.put("phone", flagMap.get("phone").toString());
							}
							putMap.put("modify", "编辑");
							dataList.add(putMap);
						}
						
						Collections.sort(dataList, new PermissComparator());
						Collections.sort(userList, new PermissComparator());
						
						sa=new MySimpleAdapter(
								UserManageActivity.this, dataList,R.layout.item_usermanage_listview,
								new String[]{"contract_item_id_s","contract_item_id","item_des_s","item_des","phone_s","phone","modify"}, 
								new int[]{R.id.contract_item_id_s,R.id.contract_item_id,R.id.item_des_s,R.id.item_des,R.id.phone_s,R.id.phone,R.id.modify});
						empListView.setAdapter(sa);
						
						Toast.makeText(UserManageActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(UserManageActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(UserManageActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(proNamekey,projectMap.get("name").toString()));
		new Thread(new AccessNetwork(getUrl,nameValuePairs, h)).start();		
	}
	
	static class PermissComparator implements Comparator<Map<String, Object>> {  
		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			return Integer.parseInt(arg0.get("permiss").toString())>Integer.parseInt(arg1.get("permiss").toString()) ? -1 : 1;
		}  
    } 
	
	//TODO initModify
	private void initModify(final int position){
		LayoutInflater inflaterDl = LayoutInflater.from(UserManageActivity.this);
        LinearLayout layout = (LinearLayout)inflaterDl.inflate(R.layout.dialog_modifyuser, null );
       
        checkDialog = new AlertDialog.Builder(UserManageActivity.this).create();
        checkDialog.show();
        checkDialog.getWindow().setContentView(layout);

		//modify map
		final Map<String, Object> modMap = new HashMap<String, Object>();
		
		//Text
		TextView name = (TextView) layout.findViewById(R.id.name);
		name.setText(userList.get(position).get("userTruename").toString());
		TextView role = (TextView) layout.findViewById(R.id.role);
		role.setText(userList.get(position).get("permission").toString());

		//Spinner
		kindSpinner = (Spinner) layout.findViewById(R.id.kindSp);
		initKindSpinner();
		kindSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				Map<String, Object> kindMap = (HashMap<String, Object>)kindSpinner.getSelectedItem();
				kindString = kindMap.get("item_title").toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

        //modify
        Button btnModify = (Button) layout.findViewById(R.id.modify_item);
        btnModify.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  if(kindString.equals("移出")){
        		  checkDialog.dismiss();
        		  removeMan(userList.get(position).get("userName").toString(),position);
        		  return;
        	  }
        	  modMap.put("id", userList.get(position).get("id").toString());
      		  modMap.put("userName", userList.get(position).get("userName").toString());
      		  if(kindString.equals("创建者")){
      			modMap.put("permission", "1");
      		  }else if(kindString.equals("管理员")){
      			modMap.put("permission", "2");
      		  }else if(kindString.equals("用户")){
      			modMap.put("permission", "3");
      		  }else if(kindString.equals("上级部门")){
      			modMap.put("permission", "4");
      		  }
      		  modMap.put("projectName", projectMap.get("name").toString());
        	  sendModify(modMap);
        	  checkDialog.dismiss();
          }
        });

        //close
        ImageButton btnClose = (ImageButton) layout.findViewById(R.id.close);
        btnClose.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  checkDialog.dismiss();
          }
        });
	}
	
	private void sendModify(Map<String, Object> modMap){
		progressDialog = ProgressDialog.show(UserManageActivity.this, null, progressInfo);
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String error = msg.getData().getString("error");//check time out
				if(error == null){
					readResp.readRespNoData(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						Toast.makeText(UserManageActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
						initListView();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(UserManageActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(UserManageActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(userkey,gson.toJson(userMap)));
		nameValuePairs.add(new BasicNameValuePair(utpkey,gson.toJson(modMap)));
		new Thread(new AccessNetwork(sendUrl,nameValuePairs, h)).start();	
	}
	
	private void removeMan(final String removeName, final int positon){
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				progressDialog = ProgressDialog.show(UserManageActivity.this, null, progressInfo);
				String error = msg.getData().getString("error");//check time out
				if(error == null){	
					readResp.readRespNoData(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
							userList.remove(positon);
							dataList.remove(positon);
							sa.notifyDataSetChanged();
							
							Toast.makeText(UserManageActivity.this, readResp.getMessage(), Toast.LENGTH_LONG).show();
							progressDialog.dismiss();
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(UserManageActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(UserManageActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		nameValuePairs.add(new BasicNameValuePair(userkey,userMap.get("name").toString()));
		nameValuePairs.add(new BasicNameValuePair(userNameKey,removeName));
		nameValuePairs.add(new BasicNameValuePair(proNamekey,projectMap.get("name").toString()));
		new Thread(new AccessNetwork(refuseUrl,nameValuePairs, h)).start();
	}
	
	// TODO KindSpinner
	private void initKindSpinner() {
		kindhandlerSpinner.post(new Runnable() {
			@Override
			public void run() {
				kindsaSpinner = new SimpleAdapter(UserManageActivity.this, getKind(),
						R.layout.item_order_spinner,
						new String[] { "item_title" },
						new int[] { R.id.item_title });
				kindSpinner.setAdapter(kindsaSpinner);
			}
		});
	}
	
	private ArrayList<Map<String, Object>> getKind(){
		ArrayList<Map<String, Object>> kindList = new ArrayList<Map<String,Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("item_title", "创建者");
		kindList.add(map1);
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("item_title", "上级部门");
		kindList.add(map6);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("item_title", "管理员");
		kindList.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("item_title", "用户");
		kindList.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("item_title", "客户");
		kindList.add(map4);
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("item_title", "移出");
		kindList.add(map5);
		return kindList;
	}
	
	//TODO Adapter&Listener
	private class ModifyListener implements OnClickListener{  
		int mPosition;  
        public ModifyListener(int inPosition){  
            mPosition= inPosition;  
        }  
		@Override  
        public void onClick(View v) {
			initModify(mPosition);
        }    
    }
	
	public class MySimpleAdapter extends SimpleAdapter {  
		private LayoutInflater mInflater;  
		private int[] mTo;
	    private String[] mFrom;
	    private ViewBinder mViewBinder;
	    private List<? extends Map<String, ?>> mData;
	    private int mResource;
	    
        public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.mInflater = LayoutInflater.from(context);
			mData = data;
	        mResource = resource;
	        mFrom = from;
	        mTo = to;
		}
  
        public View getView(int position, View convertView, ViewGroup parent) {
	        return createViewFromResource(position, convertView, parent, mResource);
	    }
	    private View createViewFromResource(int position, View convertView,
	            ViewGroup parent, int resource) {
	        View v;
	        if (convertView == null) {
	            v = mInflater.inflate(resource, parent, false);
	 
	            final int[] to = mTo;
	            final int count = to.length;
	            final View[] holder = new View[count];
	 
	            for (int i = 0; i < count; i++) {
	                holder[i] = v.findViewById(to[i]);
	            }
	 
	            v.setTag(holder);
	        } else {
	            v = convertView;
	        }
	        bindView(position, v);
	 
	        return v;
	    }
        
		@SuppressWarnings("unchecked")
		private void bindView(int position, View view) {
			final Map<String, Object> dataSet;
			if(position >= mData.size()){
				return;
			}
			dataSet = (Map<String, Object>) mData.get(position);
			if (dataSet == null) {
				return;
			}
			final ViewBinder binder = mViewBinder;
			final View[] holder = (View[]) view.getTag();
			final String[] from = mFrom;
			final int[] to = mTo;
			final int count = to.length;

			for (int i = 0; i < count; i++) {
				final View v = holder[i];
				if (v != null) {
					final Object data = dataSet.get(from[i]);
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}
					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(v, data, text);
					}
					if (!bound) {
						if (v instanceof Checkable) {
							if (data instanceof Boolean) {
								((Checkable) v).setChecked((Boolean) data);
							} else {
								throw new IllegalStateException(
										v.getClass().getName()
												+ " should be bound to a Boolean, not a "
												+ data.getClass());
							}
						}  else if (v instanceof ImageView) {
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data);
							} else if (data instanceof byte[]) { // 备注1
								Bitmap bmp;
								byte[] image = (byte[]) data;
								if (image.length != 0) {
									bmp = BitmapFactory.decodeByteArray(image,
											0, image.length);
									((ImageView) v).setImageBitmap(bmp);
								}
							} else {
								throw new IllegalStateException(
										v.getClass().getName()
												+ " is not a "
												+ " view that can be bounds by this SimpleAdapter");
							}
						} else if (v instanceof RatingBar) {
							if (data instanceof Float) {
								float score = Float.parseFloat(data.toString()); // 备注2
								((RatingBar) v).setRating(score);
							}else{
								throw new IllegalStateException(
										v.getClass().getName()
												+ " is not a "
												+ " view that can be bounds by this SimpleAdapter");
							}
						} else if (v instanceof EditText) {
							((EditText) v).setText(text);
						} else if (v instanceof TextView) {
							// Note: keep the instanceof TextView check at the
							// bottom of these
							// ifs since a lot of views are TextViews (e.g.
							// CheckBoxes).
							// setViewText((TextView) v, text);
							((TextView) v).setText(text);
						} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleAdapter");
						}
					}
				}
				switch (v.getId()) {
				case R.id.modify:
					Button modifyButton = (Button) v;
					modifyButton.setOnClickListener(new ModifyListener(position));
					break;

				}
			}
		}
	    
        @Override  
        public int getCount() { 
        	
            return dataList.size();
        }  
  
        @Override  
        public Object getItem(int position) {    
            return null;
        }  
  
        @Override  
        public long getItemId(int position) {  
            return 0;  
        }  
    }
}
