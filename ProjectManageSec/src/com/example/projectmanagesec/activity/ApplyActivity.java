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
import com.example.projectmanagesec.map.MapInitialization;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplyActivity extends Activity {

	private ImageButton backButton;
	
	private ListView lv;
	private ListViewAdapter sa;
	private List<Map<String,Object>> dataList;
	private Handler handler;
	
	private Map<String, Object> userMap;
	private List<Map<String, Object>> applyList;
	private List<Map<String, Object>> readList;
	private String applyUserStatic = "申请人：";
	private String applyProjectStatic = "申请工程：";
	private String takeText = "接受";
	private String refuseText = "拒绝";
	private String okText = "确认 ";
	
	//progressdialog
	private ProgressDialog progressDialog;
	private String progressInfo = "发送请求...";
	
	//join interface
	private String projectKey = "projectName";
	private String userNameKey = "userName";
	private String userKey = "user";
	private String admitUrl =Parameter.url+ "project.do?method=admit";
	private String refuseUrl =Parameter.url+ "project.do?method=refuse";
	private String okUrl =Parameter.url+ "project.do?method=readJoin";
	private List<NameValuePair> nameValuePairs;
	
	//respones
	private ReadResp readResp;
	
	//after respones
	private int removePosition = -1;
	private int removeDataPosition = -1;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply);
		
		backButton = (ImageButton) findViewById(R.id.back);
		
		lv = (ListView) findViewById(R.id.applyLV);
		
		applyList = (ArrayList<Map<String,Object>>) getIntent().getSerializableExtra("checkJoinList");
		readList = (ArrayList<Map<String,Object>>) getIntent().getSerializableExtra("checkReadList");
		userMap = (Map<String, Object>) getIntent().getSerializableExtra("user");
		
		dataList = new ArrayList<Map<String, Object>>();
		getData();
		handler = new Handler();
		
		nameValuePairs = new ArrayList<NameValuePair>();
		
		readResp = new ReadResp();
		
		initListView();
		initEvent();
	}
	
	private void initListView() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				sa = new ListViewAdapter(ApplyActivity.this);
				lv.setAdapter(sa);
			}
		});
	}
			
	private void getData() {
		for(Map<String,Object> listMap : applyList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("apply_user_static", applyUserStatic);
			map.put("apply_user", listMap.get("userTruename").toString());
			map.put("apply_project_static", applyProjectStatic);
			map.put("apply_project", listMap.get("projectName").toString());
			map.put("take", takeText);
			map.put("refuse", refuseText);
			map.put("read", 0);
			dataList.add(map);
		}
		for(Map<String,Object> listMap : readList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("apply_user_static", applyUserStatic);
			map.put("apply_user", listMap.get("userTruename").toString());
			map.put("apply_project_static", applyProjectStatic);
			map.put("apply_project", listMap.get("projectName").toString());
			map.put("take", "");
			map.put("refuse", okText);
			map.put("read", 1);
			dataList.add(map);
		}
	}
	
	public class ListViewAdapter extends BaseAdapter {  
	 
		private LayoutInflater mInflater;  
		
        public ListViewAdapter(Context context){
			this.mInflater = LayoutInflater.from(context); 
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

        //注意原本getView方法中的int position变量是非final的，现在改为final  
        @Override  
        public View getView(final int position, View convertView, ViewGroup parent) {  
             ViewHolder holder = null;  
            if (convertView == null) {  
                  
                holder=new ViewHolder();    
                  
                //可以理解为从vlist获取view  之后把view返回给ListView  
                convertView = mInflater.inflate(R.layout.item_apply_listview, null);
                holder.userNameStatic = (TextView)convertView.findViewById(R.id.apply_user_static);
                holder.userName = (TextView)convertView.findViewById(R.id.apply_user);
                holder.proNameStatic = (TextView)convertView.findViewById(R.id.apply_project_static);
                holder.proName = (TextView)convertView.findViewById(R.id.apply_project);
                holder.takeBtn = (Button)convertView.findViewById(R.id.take);  
                holder.refuseBtn = (Button)convertView.findViewById(R.id.refuse); 
                convertView.setTag(holder);               
            }else {               
                holder = (ViewHolder)convertView.getTag();  
            }         
              
            holder.userNameStatic.setText((String)dataList.get(position).get("apply_user_static"));
            holder.userName.setText((String)dataList.get(position).get("apply_user"));
            holder.proNameStatic.setText((String)dataList.get(position).get("apply_project_static"));
            holder.proName.setText((String)dataList.get(position).get("apply_project"));
            if((Integer)dataList.get(position).get("read")==1){
            	holder.takeBtn.setVisibility(View.INVISIBLE);
            	holder.refuseBtn.setText((String)dataList.get(position).get("refuse"));
            }
            holder.takeBtn.setTag(position);  
            holder.refuseBtn.setTag(position);  
            //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉  
            holder.takeBtn.setOnClickListener(new TakeItemListener(position));
            if((Integer)dataList.get(position).get("read")==1){
            	holder.refuseBtn.setOnClickListener(new RefuseItemListener(position,"ok"));
            }else{
            	holder.refuseBtn.setOnClickListener(new RefuseItemListener(position,"refuse"));
            }
                      
            return convertView;  
        }  
    } 
	
	private class TakeItemListener implements OnClickListener{  
        int mPosition;  
        public TakeItemListener(int inPosition){  
            mPosition= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub  
        	process(mPosition, admitUrl, "admit");
        }  
          
    } 
	
	private class RefuseItemListener implements OnClickListener{
		int mPosition;  
		String type;
        public RefuseItemListener(int inPosition, String type){  
            mPosition = inPosition;
            this.type = type;
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub
        	if(type.equals("ok")){
        		process(mPosition, okUrl, type);
        	}else{
        		process(mPosition, refuseUrl, type);
        	}
        }  
          
    }
	
	public final class ViewHolder { 
		public TextView userName;
		public TextView userNameStatic;
		public TextView proName;
		public TextView proNameStatic;
        public Button takeBtn;
        public Button refuseBtn;
    }

	private void initEvent(){
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
	
	private void process(int mPosition, String URL,final String type){
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				progressDialog = ProgressDialog.show(ApplyActivity.this, null, progressInfo);
				String error = msg.getData().getString("error");//check time out
				if(error == null){	
					readResp.readRespNoData(msg);
					if(readResp.getCode().equals(Parameter.SUCCESS_CODE)){//check resp_code
						if(removePosition!=-1){
							if(type.equals("ok")){
								readList.remove(removePosition);
							}else{
								applyList.remove(removePosition);
							}
							dataList.remove(removeDataPosition);
							sa.notifyDataSetChanged();
							
							MapInitialization app = (MapInitialization) getApplication();
							app.setApplyList(applyList);
							
							Toast.makeText(ApplyActivity.this, readResp.getMessage(), Toast.LENGTH_LONG).show();
							progressDialog.dismiss();
						}else{
							Toast.makeText(ApplyActivity.this, readResp.getMessage(), Toast.LENGTH_LONG).show();
						}
					}else if(readResp.getCode().equals(Parameter.FALSE_CODE)){//login false to Login act
						Toast.makeText(ApplyActivity.this,readResp.getMessage(), Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
					}
				}else{
					Toast.makeText(ApplyActivity.this,error, Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
				}
			}
		};
		nameValuePairs.clear();
		if(type.equals("ok")){
			nameValuePairs.add(new BasicNameValuePair(userKey,new Gson().toJson(userMap)));
			for(Map<String, Object> readMap : readList){
				if(readMap.get("userTruename").toString().equals(dataList.get(mPosition).get("apply_user").toString())){
					nameValuePairs.add(new BasicNameValuePair(projectKey,dataList.get(mPosition).get("apply_project").toString()));
					removePosition = readList.indexOf(readMap);
					removeDataPosition = mPosition;
					break;
				}
			}
			
		}else{
			nameValuePairs.add(new BasicNameValuePair(userKey,userMap.get("name").toString()));
			for(Map<String, Object> applyMap : applyList){
				if(applyMap.get("userTruename").toString().equals(dataList.get(mPosition).get("apply_user").toString())){
					nameValuePairs.add(new BasicNameValuePair(projectKey,dataList.get(mPosition).get("apply_project").toString()));
					nameValuePairs.add(new BasicNameValuePair(userNameKey,applyMap.get("userName").toString()));
					removePosition = applyList.indexOf(applyMap);
					removeDataPosition = mPosition;
					break;
				}
			}
		}
		new Thread(new AccessNetwork(URL,nameValuePairs, h)).start();
	}
}
