package com.tvb.hk.anywhere.sdk.bos;

import java.util.ArrayList;

import com.tvb.hk.anywhere.util.Util;
import com.tvb.hk.anywhere.util.UtilHttp;

public class HttpUtils {

	
	public static Object mSyncObj =  new Object();
	private static final String TAG = "HttpUtils";


	public static String post(String url, String content) {
		
		//synchronized (mSyncObj) 
		{
			
			if(Util.isEmpty(url) || !url.startsWith("http")){
				return "";
			}
			//return UtilHttp.executePostJson(url, content);
			return OkHttpUtil.executePostJson(url, content);
		}
		
	}

	
	public static String get(String url,ArrayList<KeyValue> params) {
		
		//synchronized (mSyncObj) 
		{
			if(Util.isEmpty(url) || !url.startsWith("http")){
				return "";
			}
			if(!Util.isEmpty(params)) {
				if(url.contains("?")){
					url+="&";
				}else{
					url+="?";
				}
				for(KeyValue kv:params){
					url+=kv.key+"="+kv.value+"&";
				}
			}
			//return UtilHttp.executeGet(url, null);
			return OkHttpUtil.executeGet(url, null);
		}
		
	}


}