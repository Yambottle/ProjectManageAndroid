package com.example.projectmanagesec.thread;

import android.os.Handler;
import android.os.Message;

public class applyFresher implements Runnable {
	
	private boolean isRunning = true;
	private Handler handler;
	
	public applyFresher(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		while(true){
			Message m = handler.obtainMessage();
			try {
				if(isRunning){
					m.obj = "1";
					handler.sendMessage(m);
				}else{
					m.obj = "0";
					handler.sendMessage(m);
				}
				Thread.sleep(5000);  
            } catch (Exception ex) {
            	m.obj = "2";
				handler.sendMessage(m);
            } 
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
