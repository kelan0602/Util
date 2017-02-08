package com.base.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

	private static final String TAG = "Util";

	public static class IpPort {
		public String ip = "";
		public int port = 0;
	}
	
	/**
	 * 
	 * @param ipport 例如：127.0.0.1:8080
	 * @return
	 */
	public static IpPort decodeIpPort(String ipport) {
		if(TextUtils.isEmpty(ipport)) {
			return null;
		}
		String ipp[] = ipport.split(":");
		if(null != ipp && ipp.length > 0) {
			IpPort ipPort = new IpPort();
			ipPort.ip = ipp[0];
			try{
				ipPort.port = Integer.valueOf(ipp[1]);
			}catch(Exception e) {
				ipPort.port = 0;
				e.printStackTrace();
			}
			return ipPort;
    	}
		return null;
	}
	
	public static boolean isNumeric(String str) {
		if (str == null || str.length() <= 0)
			return false;

		char[] ch = new char[str.length()];
		ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] < 48 || ch[i] > 57) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int safeIntegerParse(String intStr) {

		if (intStr == null || intStr.equals(""))
			return 0;

		try {
			int ret = Integer.parseInt(intStr.trim());
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

	public static long safeLongParse(String intStr) {

		if (intStr == null || intStr.equals(""))
			return 0L;

		try {
			Long ret = Long.parseLong(intStr.trim());
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;

	}
	
	public static boolean contains(String ori,String key){//带分隔符的字符串，默认为逗号分隔符，是否包含某一个item，精确查找，避免模糊查询得到错误的结果
		if(TextUtils.isEmpty(ori) || TextUtils.isEmpty(key)){
			return false;
		}
		String arr[] = ori.split(",");
		for(String s:arr){
			if(s.equals(key)){
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getLocalMacAddress(Context ctx) {
		ArrayList<String> retList = new ArrayList<String>();
		String Mac = null;
		try {

			String path = "sys/class/net/wlan0/address";
			if ((new File(path)).exists()) {
				FileInputStream fis = new FileInputStream(path);
				byte[] buffer = new byte[20];
				int byteCount = fis.read(buffer);
				if (byteCount > 0) {
					Mac = new String(buffer, 0, 17, "utf-8");
					retList.add(Mac);
				}
			}
			Log.i(TAG, "getLocalMacAddress***wifi wlan0**mac** = " + Mac);

			path = "sys/class/net/eth0/address";
			if ((new File(path)).exists()) {
				FileInputStream fis_name = new FileInputStream(path);
				byte[] buffer = new byte[20];
				int byteCount = fis_name.read(buffer);
				if (byteCount > 0) {
					Mac = new String(buffer, 0, 17, "utf-8");
					retList.add(Mac);
				}
			}

			Log.i(TAG, "getLocalMacAddress***eth0**mac** = " + Mac);

		} catch (Exception io) {
			io.printStackTrace();
		}

		if (retList.size() <= 0) {
			WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			if (info.getMacAddress() != null) {
				Log.i(TAG, "WIFI_SERVICE get mac =  " + info.getMacAddress().toString());
				retList.add(info.getMacAddress().toString());
			}
		}
		return retList;
	}

	/** get local ip */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, "WifiPreference IpAddress " + ex.toString());
		}

		return null;
	}

	public static String getDeviceUUID(Context ctx, String macAddr) {
		final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		// androidId = "" + android.provider.Settings.Secure.getString(
		// ctx.getContentResolver(),
		// android.provider.Settings.Secure.ANDROID_ID);
		androidId = "" + macAddr;
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		Log.i(TAG, "tmDevice = " + tmDevice + " tmSerial  = " + tmSerial + " androidId=  " + androidId + " uniqueId =  " + uniqueId);
		return uniqueId;
	}

	/**
	 * 判断远程http服务器上文件是否存在
	 * 
	 * @param URLstr
	 * @return true:文件存在、 false：文件不存在
	 */
	public static boolean fileIsExist(String URLstr) {
		Log.i(TAG, "fileIsExist &&&&&&-URLstr= " + URLstr);
		boolean b = false;
		if (URLstr == null || URLstr.equals(""))
			return false;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
			HttpGet httpGet = new HttpGet(URLstr);
			HttpResponse response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			Log.i(TAG, "&&&&&& response-code=" + code);
			if (code == 200)
				b = true;
		} catch (Exception e) {
			Log.i(TAG, "&&&&&&&&&&&&&&&&--fileIsExist()--&&&&&&&&&&&&=" + e.toString());
		}
		Log.i(TAG, "...fileIsExist() return= " + b);
		return b;
	}

	private static ProgressDialog progressDialog = null;

	// 显示进度条
	public static void showProgressDialog(final Activity context, final String title, final String msg, final boolean cancle_able) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog = ProgressDialog.show(context, title, msg, true, cancle_able);
			}
		});
	}

	public static void updateProgressDialogMessage(final Activity context, final String msg) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog != null && msg != null) {
					progressDialog.setMessage(msg);
				}
			}
		});

	}

	// 隐藏进度条
	public static void hideProgressDialog(final Activity context) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
	}

	public static void showToast(final Activity context, final String msg) {

		context.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static String waitForIp(Context c, int timeout, NetworkType type) {
		long usedTime = 0;
		long start = System.currentTimeMillis();
		while (usedTime < timeout) {
			try {
				if (type == NetworkType.TYPE_NONE) {
					type = getConnectedNetworkType(c);
				}
				Log.i(TAG, "waitForIp ConnectedNetworkType = " + type);
				ArrayList<String> listAddress = getLocalIpAddressByNetworkType(type);
				if (listAddress != null && listAddress.size() > 0) {
					Log.i(TAG, "waitForIp ip = " + listAddress.get(0) + "  listsize " + listAddress.size() + "  " + listAddress);
					return listAddress.get(0);
				}
				Thread.sleep(1000);
				usedTime = (System.currentTimeMillis() - start);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public enum NetworkType {
		TYPE_WIFI, TYPE_ETHERNET, TYPE_MOBILE, TYPE_NONE;
	}

	// 多网卡情形存在多个ip 无ip返回null
	public static ArrayList<String> getLocalIpAddressByNetworkType(NetworkType type) {
		ArrayList<String> ret = null;
		try {
			Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();

			for (; interfaceEnumeration.hasMoreElements();) {
				NetworkInterface iface = interfaceEnumeration.nextElement();

				String netPrefixStr = "";
				if (type == NetworkType.TYPE_WIFI) {
					netPrefixStr = "wlan";
				} else if (type == NetworkType.TYPE_ETHERNET) {
					netPrefixStr = "eth";
				}
				// 无线displayName 为 wlan*（*为数字） 有线为eth*
				Log.i(TAG, "inetAddress  iface.getName() = " + iface.getName());
				if (!netPrefixStr.equals("") && iface.getName().startsWith(netPrefixStr)) {
					if (ret == null) {
						ret = new ArrayList<String>();
					}
					// 可能存在的多网卡情形
					for (Enumeration<InetAddress> enumIpAddr = iface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						Log.i(TAG, "inetAddress hostAddr = " + inetAddress.getHostAddress().toString() + " name = " + inetAddress.getHostName());
						if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
							Log.i(TAG, "isLoopbackAddress and instanceof Inet4Address ipStr= " + inetAddress.getHostAddress().toString());
							ret.add(inetAddress.getHostAddress().toString());
						}
					}
					break;// break位置特别注意啊。。。
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public static NetworkType getConnectedNetworkType(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected()) {
			if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				Log.i(TAG, "TYPE_WIFI connect..isConnected " + activeInfo.isConnected());
				return NetworkType.TYPE_WIFI;
			}
			if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				Log.i(TAG, "TYPE_MOBILE connect..isConnected " + activeInfo.isConnected());
				return NetworkType.TYPE_MOBILE;
			}
			if (activeInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
				Log.i(TAG, "TYPE_ETHERNET connect..isConnected " + activeInfo.isConnected());
				return NetworkType.TYPE_ETHERNET;
			}
		}
		return NetworkType.TYPE_NONE;
	}

	private static AlertDialog mAlertDialog;

	public static AlertDialog showAlertDialog(Context c, String pTitle, String pMessage, DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener, DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(c).setTitle(pTitle).setMessage(pMessage).setPositiveButton(android.R.string.ok, pOkClickListener)
				.setNegativeButton(android.R.string.cancel, pCancelClickListener).show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	public static void intstallApkByPackageName(final Activity context, final String package_name) {

		context.runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = UtilFile.getCacheFile(package_name + ".apk");
				Log.i(TAG, "file = " + file);
				Log.i(TAG, "Uri.fromFile(file) = " + Uri.fromFile(file));
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		});
	}

	public static float getDensity(Context context) {// 1dip 应该乘以像素密度 得到实际的整数值
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

	public static int getScreenWidthDip(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return (int) (dm.widthPixels / dm.density);
	}

	public static int getScreenHeightDip(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return (int) (dm.heightPixels / dm.density);
	}

	public static int getScreenWidthPx(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeightPx(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.heightPixels;
	}

	/*
	 * //注意单位为dip screenWdip 为当前设备的总宽度dip值 wdip 为1280dip下的对应dip值 返回当前设备下的dip值
	 */
	public static int getWidthDipAdjust(int screenWdip, int wdip) {
		return wdip * screenWdip / 1280;
	}

	public static int getHeightDipAdjust(int screenHdip, int hdip) {
		return hdip * screenHdip / 1280;
	}

	// 将整数time,单位秒,的时间,转换为12:34:56格式
	public static String toFormatString(int time) {
		if (time <= 0)
			return "00:00:00";
		String hours, minues, seconds, ret;
		if ((time / 3600) > 9)
			hours = "" + time / 3600;
		else
			hours = "0" + time / 3600;

		if ((time % 3600) / 60 > 9)
			minues = "" + (time % 3600) / 60;
		else
			minues = "0" + (time % 3600) / 60;
		if (time % 60 > 9)
			seconds = "" + time % 60;
		else
			seconds = "0" + time % 60;
		ret = hours + ":" + minues + ":" + seconds;
		return ret;
	}

	/**
	 * 判断Activity是否正在运行
	 */
	public static boolean isActivityRunning(Context ctx, String name) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(20);
		boolean isAppRunning = false;
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getClassName().equals(name) || info.baseActivity.getClassName().equals(name)) {
				isAppRunning = true;
				Log.i(TAG, info.topActivity.getClassName() + " info.baseActivity.getClassName()=" + info.baseActivity.getClassName());
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * 判断Service是否正在运行
	 * 
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 获取系统当前语言
	 */
	public static String getCurrentLanguage() {
		
		String currnentLanguage = "zh_hk";//默认使用繁体
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();
		String country = locale.getCountry();
		if (language.equalsIgnoreCase("zh")) {
			if (country.equalsIgnoreCase("TW") || country.equalsIgnoreCase("HK")) {
				currnentLanguage = "zh_hk";
			} else {
				currnentLanguage = "zh";
			}
		} else if (language.equalsIgnoreCase("en")) {
			currnentLanguage = "en";
		}
		return currnentLanguage;
	}
	
	/**
	 * 获取当前系统设置的时区
	 * @return
	 */
	public static int getTimezone(){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		// 2、取得时区偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		int zone = (zoneOffset + dstOffset)/(1000*3600);
		return zone;
		
	}
	/**
	 * XML只有5个转义符: &lt; &gt;&amp; &quot; &apos;
	 * 分别代表 <  >  &  "  '
	 * ***/
	public static String decodeXMLString(String xmldata){
		
		if(xmldata == null || xmldata.equals("")){
			return xmldata;
		}
		xmldata = replaceString(xmldata, "&amp;", "&");
		xmldata = replaceString(xmldata, "&lt;", "<");
		xmldata = replaceString(xmldata, "&gt;", ">");
        xmldata = replaceString(xmldata, "&apos;", "\'");
        xmldata = replaceString(xmldata, "&quot;", "\"");
        xmldata = replaceString(xmldata, "&#40;", "(");
        xmldata = replaceString(xmldata, "&#41;", ")");
        return xmldata;
		
	}
    public static String encodeXMLDataString(String strData){
    	if(strData == null || strData.equals("")){
			return strData;
		}
        strData = replaceString(strData, "&", "&amp;");
        strData = replaceString(strData, "<", "&lt;");
        strData = replaceString(strData, ">", "&gt;");
        strData = replaceString(strData, "\'", "&apos;");
        strData = replaceString(strData, "\"", "&quot;");
        return strData;
    }
    
    public static String safeStringUtf8(String ori){
    	if(TextUtils.isEmpty(ori)){
    		return "";
    	}
    	try {
			return new String(ori.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return "";
    }
    public static String safeEncodeUTF8(String ori){
    	if(TextUtils.isEmpty(ori)){
    		return "";
    	}
    	try {
			return URLEncoder.encode(ori, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return "";
    }
	   /**
     * 替换一个字符串中的某些指定字符
     * @param strData String 原始字符串
     * @param regex String 要替换的字符串
     * @param replacement String 替代字符串
     * @return String 替换后的字符串
     */
    private static String replaceString(String strData, String regex,String replacement){
        if (strData == null)
        {
            return null;
        }
        int index;
        index = strData.indexOf(regex);
        String strNew = "";
        if (index >= 0)
        {
            while (index >= 0)
            {
                strNew += strData.substring(0, index) + replacement;
                strData = strData.substring(index + regex.length());
                index = strData.indexOf(regex);
            }
            strNew += strData;
            return strNew;
        }
        return strData;
    }
    
    /***将utc时间（单位ms）转换成本地时间显示 **/
	public static String utc2Local(long utc, String localTimePatten) {
		SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(utc);
		return localTime;
	}
	
	
// // 组装自主内容的播放地址
//	public static String mopUrl2RealUrl(String mopUrl) {
//		if(mopUrl == null || mopUrl.equals("") || !mopUrl.startsWith("mop://") || mopUrl.contains("?")){
//			Log.e(TAG,"mopUrl2RealUrl paramter error mopUrl = "+mopUrl);
//			return mopUrl;
//		}
//		String cid = mopUrl.substring(6);
//		String ret = SoapClient.getInstance().getM3u8ParseServer()+"/" + cid + ".m3u8"+"?type=phone&token="+SoapClient.getInstance().getOisToken();
//		if(Parameter.get(Parameter.FIELD_USE_DRM).equalsIgnoreCase("true")) {
//			ret = ret +
//			"&drm=true&ois="+SoapClient.getInstance().getCurOisDomain()+
//			"&port="+SoapClient.getInstance().getCurSoapClientCmdPort()+"&stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+
//			"&user="+Parameter.get(Parameter.FIELD_USER)+
//			"&cid="+cid;
//		} 
//
//		return ret;
//	}
	
//	// 组装自主内容的播放地址
//	public static String mopUrl2Proxy(String mopUrl) {
//		if(mopUrl == null || mopUrl.equals("") || !mopUrl.startsWith("mop://") || mopUrl.contains("?")){
//			Log.e(TAG,"mopUrl2Proxy paramter error mopUrl = "+mopUrl);
//			return mopUrl;
//		}
//		String cid = mopUrl.substring(6);
//		String ret="";
//		if(Parameter.get(Parameter.FIELD_PROX).contains("uls")){
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/uls/"+cid+".m3u8?cid="+cid;
//		}else if(Parameter.get(Parameter.FIELD_PROX).contains("proxh")){
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/hlsProxy/"+cid+".m3u8?cid="+cid;
//		}else if(Parameter.get(Parameter.FIELD_PROX).contains("proxy")){
//			String cdn = SoapClient.getInstance().getCurOisHttpChannelUrl()+"/";
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/tvbProxy/"+SafeBase64.encode(cdn.getBytes())+"/"+cid+".m3u8?cid="+cid;
//		}else{
//			Log.e(TAG,"mopUrl2Proxy ret = "+ret);
//			return mopUrl;
//		}
//		ret += "&stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();
//		return ret;
//	}
	
	//utc 时间单位ms
//	public static String fixLookBackUrl(String mopUrl, long startUtc, long endUtc) {
//		String url = null;
//		String seekUrl = "playseek=" + startUtc / 1000 + "-" + endUtc / 1000; // 单位s，从epg拿到的是ms
//
//		url = Util.mopUrl2RealUrl(mopUrl)+"&"+seekUrl;
//		
//		String cid = mopUrl.substring(6);
//		String ret="";
//		if(Parameter.get(Parameter.FIELD_PROX).contains("uls")){
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/uls/"+cid+".m3u8?cid="+cid;
//		}else if(Parameter.get(Parameter.FIELD_PROX).contains("proxh")){
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/hlsProxy/"+cid+".m3u8?cid="+cid;
//		}else if(Parameter.get(Parameter.FIELD_PROX).contains("proxy")){
//			String cdn = SoapClient.getInstance().getCurOisHttpChannelUrl()+"/";
//			ret = "http://127.0.0.1:"+TVBProxy.GetPort()+"/tvbProxy/"+SafeBase64.encode(cdn.getBytes())+"/"+cid+".m3u8?cid="+cid;
//		}else{
//			Log.e(TAG,"fixLookBackUrl ret = "+url);
//			return url;
//		}
//		ret += "&stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();
//		ret += "&"+seekUrl;
//		//Log.d(TAG,"fixLookBackUrl ret = "+ret);
//		return ret;
//	}
	
	
//	public static String urlAddParameter(String mopUrl,String url){
//		String ret =null;
//		if(mopUrl == null || mopUrl.equals("") || !mopUrl.startsWith("mop://") || mopUrl.contains("?") || url == null){
//			Log.e(TAG,"paramter error mopUrl = "+mopUrl);
//			return null;
//		}
//		
//		try {
//			String cid = mopUrl.substring(6);
//			if(url.contains("?")){
//				ret = url+"&stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();
//			}else{
//				ret = url+"?stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();
//			}
//			if(Parameter.get(Parameter.FIELD_USE_DRM).equalsIgnoreCase("true")) {
//				ret = ret +
//				"&drm=1&ois="+SoapClient.getInstance().getCurOisDomain()+
//				"&port="+SoapClient.getInstance().getCurSoapClientCmdPort()+"&ssl="+SoapClient.getInstance().isUseSSL()+
//				"&user="+Parameter.get(Parameter.FIELD_USER)+
//				"&cid="+cid;
//			}
//			Log.d(TAG, "urlAddParameter mopUrl = "+mopUrl+"\r\n  url =" + url+"\r\n ret = "+ret);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}
	
//	public static String addDrmOptions(String url, String cid){
//		
//		String ret=url;
//		
//		if(url.startsWith("http://127.0.0.1:61564") || cid.equalsIgnoreCase("debug"))
//			return url;
//		
//		if(url.contains("drm="))
//			return url;
//		
//		if(ret.contains("?")){
//			ret = ret+"&stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();	
//		}else{
//			ret = ret+"?stbid="+Parameter.get(Parameter.FIELD_TERMINAL_ID)+"&token="+SoapClient.getInstance().getCdnToken();	
//		}
//		
//		if(Parameter.get(Parameter.FIELD_USE_DRM).equalsIgnoreCase("true")) {
//			ret = ret +
//			"&drm=1&ois="+SoapClient.getInstance().getCurOisDomain()+
//			"&port="+SoapClient.getInstance().getCurSoapClientCmdPort()+"&ssl="+SoapClient.getInstance().isUseSSL()+
//			"&user="+Parameter.get(Parameter.FIELD_USER)+
//			"&cid="+cid;
//		} 
//		return ret;
//	}
	
//	public static String getCidFromURL(String url){
//		String cid = getNameValueFromURL("cid", url);
//		if(TextUtils.isEmpty(cid)){
//			String base64url = getNameValueFromURL("url", url);
//			if(!TextUtils.isEmpty(base64url) && url.contains("127.0.0.1:61564")){
//				String deurl = new String(SafeBase64.decode(base64url));
//				 cid = getNameValueFromURL("cid", deurl);
//			}
//		}
//		if(TextUtils.isEmpty(cid)){
//			int end = url.indexOf(".m3u8");
//			if(end > 0){
//				int start = url.lastIndexOf("/", end);
//				if(start > 0 && start<end){
//					start++;
//					cid = url.substring(start, end);
//				}
//			}
//		}
//		return cid;
//	}
	
	
	
//	public static String getNameValueFromURL2(String name, String url){
//		String value = getNameValueFromURL(name, url);
//		if(TextUtils.isEmpty(value)){
//			String base64url = getNameValueFromURL("url", url);
//			if(!TextUtils.isEmpty(base64url) && url.contains("127.0.0.1:61564")){
//				String deurl = new String(SafeBase64.decode(base64url));
//				 value = getNameValueFromURL(name, deurl);
//			}
//		}
//		return value;
//	}
	
	public static String getNameValueFromURL(String name, String url){
		if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(url) && url.contains(name+"=")){
			//解析url
			int s,e;
			s = url.indexOf(name+"=");
			if( s > 0){
				s += 4;
				e = url.indexOf("&", s);
				if(e < 0){
					e = url.length();
				}
				if(e>0 && s<e){
					return url.substring(s, e);
				}
			}
		}
		
		return "";
	}
    
	public static String getIp(String  host){
        String ip = "";
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            ip = inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return  ip;
        }
        return ip;
    }

	public static int changeToRealSize(Context c,float f){
		return (int) (f/(c.getResources().getDisplayMetrics().scaledDensity));
	}
	
	private static HashMap<String, Long> logPrintMap =  new HashMap<String, Long>();
	public static void printLoopLog(String log,int timeout_ms){
		Long lastPrintTime = logPrintMap.get(log);
		if(lastPrintTime == null  ||
				(lastPrintTime != null && System.currentTimeMillis()-lastPrintTime.longValue() > timeout_ms) ){
			Log.i(TAG, log);
			logPrintMap.put(log, System.currentTimeMillis());
		}
	}
	
	private static HomeWatcherReceiver mHomeKeyReceiver = null;
    public static void registerHomeKeyReceiver(Context context) {
        Log.d(TAG, "registerHomeKeyReceiver");
        try {
        	 if(context != null){
                 mHomeKeyReceiver = new HomeWatcherReceiver();
                 IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                 filter.addAction(Intent.ACTION_SCREEN_ON);
                 filter.addAction(Intent.ACTION_SCREEN_OFF);
                 filter.addAction(Intent.ACTION_USER_PRESENT);
                 context.registerReceiver(mHomeKeyReceiver, filter);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
    public static void unregisterHomeKeyReceiver(Context context) {
        Log.d(TAG,  "unregisterHomeKeyReceiver");
        try {
        	 if (context != null  && null != mHomeKeyReceiver) {
                 context.unregisterReceiver(mHomeKeyReceiver);
                 mHomeKeyReceiver = null;
             }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
