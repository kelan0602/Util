package com.tvb.hk.anywhere.sdk.bos;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import com.tvb.hk.anywhere.sdk.logs.LogsManager;
import com.tvb.hk.anywhere.sdk.param.Parameter;
import com.tvb.hk.anywhere.util.Util;

import android.text.TextUtils;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

	private static final String TAG = "OkHttpUtil";
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static String executePostJson(String url, String content) {

		try {
			if(Util.isEmpty(url) || !url.startsWith("http")){
				Log.d(TAG, "executePostJson parameter error url = "+url);
				return "";
			}
			OkHttpClient client = getOkHttpClient();
			
			RequestBody body = RequestBody.create(JSON, content);
			Request request = new Request.Builder().url(url).post(body).build();
			Response response = client.newCall(request).execute();
			
			String ret=  response.body().string();
			String logInfo = "executePostJson  OK . url = " + url +" content= "+content;
			
			if(!url.contains(Parameter.getLogs()) && !url.contains("/bos/login")){
				LogsManager.getInstance().sendActionAPI(url, "executePostJson  OK! content = "+content);
				Log.d(TAG, logInfo);
			}
			if(!url.contains("/bos/login")){
				if (TextUtils.isEmpty(ret)) {
					return "true";
				}
			}
			return ret;
			
		} catch (Exception e) {
			String errorInfo = "executePostJson ERROR!!! url = " + url
					+ " catch Exception e = " + e.getClass().getSimpleName();
			if(!url.contains(Parameter.getLogs())){
				LogsManager.getInstance().sendActionAPI(url, errorInfo);
				Log.e(TAG, errorInfo);
			}
			e.printStackTrace();
		}
		return null;
	}

	public static String executeGet(String url,ArrayList<KeyValue> params) {

		try {
			if(Util.isEmpty(url) || !url.startsWith("http")){
				Log.d(TAG, "executeGet parameter error url = "+url);
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
			
			OkHttpClient client = getOkHttpClient();

			client.sslSocketFactory();
			
			Request request = new Request.Builder().url(url).build();

			Response response = client.newCall(request).execute();
			
			String ret= response.body().string();
			String logInfo = "executeGet  OK . url = " + url;
			
			if(!url.contains(Parameter.getLogs())){
				LogsManager.getInstance().sendActionAPI(url, "executeGet  OK");
				Log.d(TAG, logInfo);
			}
			return ret;

		} catch (Exception e) {
			String errorInfo = "executeGet ERROR!!! url = " + url
					+ " catch Exception e = " + e.getClass().getSimpleName();
			if(!url.contains(Parameter.getLogs())){
				LogsManager.getInstance().sendActionAPI(url, errorInfo);
				Log.e(TAG, errorInfo);
			}
			
			e.printStackTrace();
		}
		return null;
	}
	

	public static OkHttpClient getOkHttpClient() {
		
        OkHttpClient.Builder builder = new OkHttpClient.Builder() .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
        if(true){
        	SSLUtils.SSLParams sslParams = SSLUtils.getSslSocketFactory(null, null, null);
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        }else{
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                try {
                    sslContext.init(null, null, null);
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            SSLSocketFactory socketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
            builder.sslSocketFactory(socketFactory,new SSLUtils.UnSafeTrustManager());
            
        }
        return builder.build();
    }
	
	 
}
