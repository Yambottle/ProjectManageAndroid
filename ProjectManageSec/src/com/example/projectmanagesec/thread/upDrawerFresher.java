package com.example.projectmanagesec.thread;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class upDrawerFresher implements Runnable{

	private Handler handler;
	
	public upDrawerFresher(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		while(true){
			Message m = handler.obtainMessage();
			try {
				m.obj = "1";
				handler.sendMessage(m);
				Thread.sleep(1000);  
            } catch (Exception ex) {
            } 
		}
	}

}
