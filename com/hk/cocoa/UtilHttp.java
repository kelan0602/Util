package com.ivs.sdk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import com.ivs.sdk.logs.LogsManager;
import com.ivs.sdk.param.Parameter;

import android.text.TextUtils;
import android.util.Log;


public class UtilHttp {
	final static String TAG = "UtilHttp";
	
	public static final int TIME_OUT = 0;
	public static final int ERROR_EXCEPTION = 1;
    private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时x秒钟
    private static final int SO_TIMEOUT = 5*1000;  //设置等待数据超时时间x秒钟
    public static final String NO_DATA = "nodata";

    public static class ResponseData{
    	public int StatusData = 111;
    	public String MsgData = "";
    	public String SuccessXml = "";
    	public void init(){
        	StatusData = 111;
        	MsgData = "";
        	SuccessXml = "";
    	}
    	
    }
    
    
   
    /**
     * 添加请求超时时间和等待时间
     */
    public static HttpClient getHttpClient(){
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

    public static String executeGet(String url, List<BasicHeader> dataList) {
    	//防止回溯
    	if(TextUtils.isEmpty(url)){
    		return "";
    	}

    	StringBuffer sbResult = new StringBuffer();
		HttpClient client =  getHttpClient();
		String ret = null;
		try {
			if(dataList != null && dataList.size() > 0) {
				url+="?";
				for(BasicHeader h:dataList){
					url+=h.getName()+"="+h.getValue()+"&";
				}
			}
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("User-Agent","sunniwell@tvb");  
			HttpResponse response = client.execute(httpGet);
			if(response == null){
				Log.d(TAG,"executeGet url = "+url +"response = "+response);
				LogsManager.getInstance().sendLoopCommonInfoLog("executeGet url = "+url +"response = "+response);
				ret= null;
			}else{
				sbResult = getResponse(sbResult, response);
				Log.d(TAG,"executeGet url = "+url + "  StatusCode:"+response.getStatusLine().getStatusCode());
				if( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ){
					LogsManager.getInstance().sendLoopCommonInfoLog("executeGet error! StatusCode = "+response.getStatusLine().getStatusCode());
					ret= null;
				}else{
					ret= sbResult.toString();
					if(TextUtils.isEmpty(ret) ){//200 OK but no data...
						Log.d(TAG,"url = "+url+"  no data....");
						ret = NO_DATA; 
						LogsManager.getInstance().sendLoopCommonInfoLog("executeGet 200 OK but no data! url = "+url);
					}else{
						LogsManager.getInstance().sendLoopCommonInfoLog("executeGet OK !url = "+url);
					}
				}
			}
		}catch (TimeoutException e) {
			Log.d(TAG,"executeGet TimeoutException..url = "+url);
			LogsManager.getInstance().sendLoopCommonInfoLog("executeGet TimeoutException..url = "+url);
		}catch (SocketTimeoutException e) {
			Log.d(TAG,"executeGet SocketTimeoutException..url = "+url);
			LogsManager.getInstance().sendLoopCommonInfoLog("executeGet SocketTimeoutException..url = "+url);
		}catch (ConnectTimeoutException e) {
			Log.d(TAG,"executeGet ConnectTimeoutException..url= "+url);
			LogsManager.getInstance().sendLoopCommonInfoLog("executeGet ConnectTimeoutException..url= "+url);
		}catch(Exception e){
			LogsManager.getInstance().sendLoopCommonInfoLog("executeGet got Exception."+e.getClass().getName()+" url= "+url);
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		
		return ret;
	}

    
	public static boolean isServerOk(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}

		StringBuffer sbResult = new StringBuffer();
		HttpClient client = getHttpClient();
		boolean ret =false;
		try {

			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("User-Agent", "sunniwell@tvb");
			HttpResponse response = client.execute(httpGet);
			if (response == null) {
				Log.d(TAG, "executeGet url = " + url + "response = " + response);
			} else {
				sbResult = getResponse(sbResult, response);
				Log.d(TAG, "isServerOk url = " + url + "  StatusCode:" + response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					ret =  true;
				}
			}
		} catch (Exception e) {
			
		} finally {
			client.getConnectionManager().shutdown();
		}
		return ret;
	}
	
	public static String executePost(String url, List<BasicNameValuePair> dataList) {
		StringBuffer sbResult = new StringBuffer();
		HttpClient client =  getHttpClient();
		String ret = null;
		try {
			
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("User-Agent","sunniwell@tvb");  
			HttpEntity entity = new UrlEncodedFormEntity(dataList, "UTF-8");
			
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			sbResult = getResponse(sbResult, response);
			Log.d(TAG,"executePost url = "+url + "StatusCode:"+response.getStatusLine().getStatusCode());
			if( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ){
				ret = null;
			}else{
				ret = sbResult.toString();
				if(TextUtils.isEmpty(ret) ){//200 OK but no data...
					ret = NO_DATA; 
				}
			}
			Log.d(TAG,"executePost ret = "+ret);
		}catch (TimeoutException e) {
			Log.d(TAG,"execute TimeoutException..url = "+url);
		}catch (SocketTimeoutException e) {
			Log.d(TAG,"execute SocketTimeoutException..url = "+url);
		}catch (ConnectTimeoutException e) {
			Log.d(TAG,"execute ConnectTimeoutException..url = "+url);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		return ret;
	}
	
	public static HttpResponse executePost(String url, String json) {
		try {
			StringBuffer sbResult = new StringBuffer();
			HttpClient client =  getHttpClient();
			HttpPost httpPost = new HttpPost(url);
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");
			httpPost.setEntity(s);
			HttpResponse response = client.execute(httpPost);
			//sbResult = getResponse(sbResult, response);
			Log.d(TAG,"executejson url = "+url + " StatusCode:"+response.getStatusLine().getStatusCode());
			if( response == null)
				return null;
			return response;
		}catch (SocketTimeoutException e) {
			Log.d(TAG,"execute SocketTimeoutException..");
		}catch (ConnectTimeoutException e) {
			Log.d(TAG,"execute ConnectTimeoutException..");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static String executePost(String url, JSONObject json) {
		try {
			StringBuffer sbResult = new StringBuffer();
			HttpClient client =  getHttpClient();
			HttpPost httpPost = new HttpPost(url);
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");
			httpPost.setEntity(s);
			HttpResponse response = client.execute(httpPost);
			sbResult = getResponse(sbResult, response);
			Log.d(TAG,"executejson url = "+url + "StatusCode:"+response.getStatusLine().getStatusCode());
			if( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK )
				return null;
			return sbResult.toString();
		}catch (TimeoutException e) {
			Log.d(TAG,"execute TimeoutException..");
		}catch (SocketTimeoutException e) {
			Log.d(TAG,"execute SocketTimeoutException..");
		}catch (ConnectTimeoutException e) {
			Log.d(TAG,"execute ConnectTimeoutException..");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static boolean executeGetSaveFile(String url, File saveFile) {
		try {
			BasicHttpParams httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 5000);
			DefaultHttpClient client = new DefaultHttpClient(httpParams);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200){
				saveFile.deleteOnExit();
				saveFile.createNewFile();
				BufferedReader inputStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
				OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(saveFile),"utf-8");
	            String buffer = "";
	            while ((buffer = inputStream.readLine()) != null) {
	                outputStream.write(buffer);
	            }
	            inputStream.close();
	            outputStream.close();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static StringBuffer getResponse(StringBuffer sbResult,
			HttpResponse response) throws Exception {
		InputStream inputStream = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, "utf-8"));
		String data = "";
		if (sbResult.toString().equalsIgnoreCase("")) {
			while ((data = br.readLine()) != null) {
				sbResult.append(data+"\r\n");
			}
		}
		inputStream.close();
		return sbResult;

	}
	
	public static StringBuffer getResponseGBK(StringBuffer sbResult,
			HttpResponse response) throws Exception {
		InputStream inputStream = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, "GBK"));//获得真实播放地址时是GBK编码的xml
		String data = "";
		if (sbResult.toString().equalsIgnoreCase("")) {
			while ((data = br.readLine()) != null) {
				sbResult.append(data+"\r\n");
			}
		}
		inputStream.close();
		return sbResult;

	}

}
