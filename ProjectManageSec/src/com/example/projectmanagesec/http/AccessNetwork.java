package com.example.projectmanagesec.http;

import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class AccessNetwork implements Runnable{
	
	private String url ;
	private Handler h ;
	List<NameValuePair> nameValuePairs;
	
	private String timeoutError = "网络错误，请检查网络" ;  
	
	//universal
	public AccessNetwork(String url, List<NameValuePair> nameValuePairs, Handler h){
		super();
		this.url = url;
		this.h = h;
		this.nameValuePairs = nameValuePairs;
	}
		
	@Override
	public void run() throws AssertionError{
		Message m = new Message();
		try {	
			m.obj = sendPost(url, nameValuePairs);//send Post
		} catch (Exception e) {
			e.printStackTrace();
			Bundle data = new Bundle();
            data.putString("error", timeoutError);//timeout infor into Bundle
            m.setData(data);
		}finally{
			h.sendMessage(m);//return to Handler
		}
		
	}
	
	public static String sendPost(String url, List<NameValuePair> nameValuePairs) throws Exception {
		String displayString="nullvalue";//init resp return info

		DefaultHttpClient httpClient = new DefaultHttpClient();
   		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000); 
   		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
   		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		HttpResponse rsp = httpClient.execute(httpPost);
		HttpEntity httpEntity = rsp.getEntity();
		
		displayString = EntityUtils.toString(httpEntity);
		
		return displayString;
	}
}


