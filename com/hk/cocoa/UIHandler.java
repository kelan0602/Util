package com.tvb.hk.anywhere.util;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UIHandler {

	private static final String TAG = "UIHandler";
	private static UIHandler mInstance = null;
	private Handler mHandler = null;

	private static final int MSG_SHOW_TOAST = 0;
	
	private class ToastMessage{
		public Context c;
		public String str;
		public ToastMessage(Context c,String str){
			this.c = c;
			this.str = str;
		}
	}
	
	private Toast mToast = null;
	private UIHandler() {
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d(TAG, "handleMessage in what = " + msg.what);
	            switch (msg.what){
	            	case MSG_SHOW_TOAST:
	            		ToastMessage tm= (ToastMessage)msg.obj;	
	            		if(mToast != null){
	            			mToast.setText(tm.str);
	            		}else{
	            			mToast = Toast.makeText(tm.c, tm.str, Toast.LENGTH_LONG);
	            		}
	            		mToast.show();
	            		break;
	            }
			}
		};

	}

	public static UIHandler getInstance(){
		if(mInstance == null){
			mInstance =  new UIHandler();
		}
		return mInstance;
	}
	
	public void post(Runnable  r){
		mHandler.post(r);
	}
	
	public void postDelay(Runnable r,long delayMillis){
		mHandler.postDelayed(r, delayMillis);
	}
	
	public void remove(Runnable r){
		mHandler.removeCallbacks(r);
	}
	
	
	public void showToast(Context c,String str){
		if(c == null || Util.isEmpty(str)){
			return;
		}
		mHandler.removeMessages(MSG_SHOW_TOAST);
		Message msg=  new Message();
		msg.what = MSG_SHOW_TOAST;
		msg.obj = new ToastMessage(c,str);
		mHandler.sendMessage(msg); 
	}

	
	public void sendEmptyMsg(int what){
		mHandler.removeMessages(what);
		mHandler.sendEmptyMessage(what); 
	}
	public void sendMsg(Message msg){
		mHandler.removeMessages(msg.what);
		mHandler.sendMessage(msg); 
	}
	
	public void postImmediately(Runnable r){
		mHandler.postAtFrontOfQueue(r);
	}

}
