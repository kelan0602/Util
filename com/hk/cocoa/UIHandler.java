package com.base.util;

import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class UIHandler {

	public static final String MAIN_HANDER = "MainActivity_Hander";
	public static final String LIVE_PLAYER_HANDER = "LivePlayerActivity_Hander";
	public static final String VOD_PLAYER_HANDER = "VodPlayerActivity_Hander";
	
	private static final String TAG = "UIHandler";
	private static UIHandler mInstance = null;
	private Handler mHandler = null;
	private HashMap<String, Handler> handlerMap =  new HashMap<String, Handler>();
	
	
	private UIHandler(){
		mHandler = new Handler(Looper.getMainLooper());
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
	
	public void setHander(String hand,Handler h){
		handlerMap.put(hand, h);
	}
	public void removeHander(String hand){
		handlerMap.remove(hand);
	}

	public void sendMsg(String hand,Message msg){
		Handler h =  handlerMap.get(hand);
		if(h != null){
			h.sendMessage(msg);
		}else{
			Log.e(TAG, "sendMsg error...find hander failed...hand = "+hand);
		}
	}
	
	public void sendEmptyMsg(String hand,int what){
		Handler h =  handlerMap.get(hand);
		if(h != null){
			h.sendEmptyMessage(what); 
		}else{
			Log.e(TAG, "sendMsg error...find hander failed...hand = "+hand);
		}
	}
	
	public void sendMsgDelay(String hand,Message msg,long delayMillis){
		Handler h =  handlerMap.get(hand);
		if(h != null){
			h.sendMessageDelayed(msg, delayMillis);
		}else{
			Log.e(TAG, "sendMsgDelay error...find hander failed...hand = "+hand);
		}
		
	}
	
	public void postImmediately(Runnable r){
		mHandler.postAtFrontOfQueue(r);
	}

	public void postToNormalHandler(Runnable  r){
		new Handler().post(r);
	}
	
	public void postToNormalHandlerDelay(Runnable  r,long delayMillis){
		new Handler().postDelayed(r, delayMillis);
	}
}
