package com.base.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class UtilFile {
	
	private static final String TAG = "UtilFile";
	private static final long TIMEOUT = 6*60*60;//6h 单位s
	//取得缓存目录
	public static String getCacheDir() {
		
		//不使用静态变量来省时间，因为一开始可能sdcard没有mount上，但开机一会后才mount上，所以。。。
		if ( Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			// 获取根目录 /mnt/sdcard
			File sdDir = null;
			sdDir = Environment.getExternalStorageDirectory();
			try {
				File dirTest = new File(sdDir.toString() + "/sunniwell/tmp/");
				if(!dirTest.exists()){
					dirTest.mkdirs();
					if(dirTest.exists())
					{
						return sdDir.toString() + "/sunniwell/tmp/";
					}
				}else{
					return sdDir.toString() + "/sunniwell/tmp/";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String tmp = "/data/sunniwell/";
		try {
			File dirTest = new File(tmp);
			if(!dirTest.exists()){
				dirTest.mkdirs();
				if(dirTest.exists())
				{
					return tmp;
				}
			}else{
				return tmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/tmp/";
	}
	
	public static String getAdCacheFileDir(){
		
	   File adDir =  new File(getCacheDir()+"adCache/");
	   if(!adDir.exists()){
		   adDir.mkdirs();
		}
	   Log.d(TAG,"getAdCacheFile = "+adDir.toString());
	   return adDir.toString()+"/";
	}

	//如果目录不存在则创建
	public static boolean makeDirExist(String dirName){
		File dir = new File(dirName);
		if(!dir.exists())//先判断目录是否存在，不存在，先创建目录
			dir.mkdirs();
		return true;
	}

	//检查getCacheDir 下的fileName的缓存文件是否存在
	public static boolean isCacheFileExist(String fileName){
		String dirName = getCacheDir();
        File file = new File(dirName,fileName);
        if(!file.exists() || file.length() == 0)
        {
        	return false;
        }
        return true;
	}
	//读取getCacheDir 下的缓存文件
    public static String readCacheFile(String fileName) {
    	String dirName = getCacheDir();
        File file = new File(dirName,fileName);
        String ret = "";

//		Log.d("UtilFile", "readCacheFile(" + dirName+"  "+fileName + ")");	

		if(!file.exists() || file.length() == 0)
		{
			Log.d("UtilFile", "readCacheFile(" + dirName+"  "+fileName + ")"+" file not exists...length = "+file.length()
					+" exists = "+file.exists()+" can Read = "+file.canRead());	
        	return null;
		}

    	try {
            FileInputStream inputStream = new FileInputStream(file);
    		BufferedReader br = new BufferedReader(new InputStreamReader(
    				inputStream, "utf-8"));

            while (true) {
            	String tmp = br.readLine();
            	if(tmp == null)
            		break;
            	ret+=tmp;
            }
            inputStream.close();
            if(ret.equals("")){
            	Log.e(TAG,"getCache error fileName ="+(dirName+fileName));
            	return null;
            }
            Log.d(TAG,"getCache  dirName="+dirName+" fileName ="+(dirName+fileName)+" length = "+file.length());	
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    //读取自定义路径下的文件
    public static String readFile(String dirName, String fileName) {
        File file = new File(dirName,fileName);
        String ret = "";

		Log.d("UtilFile", "readFile(" + fileName + ")");	

		if(!file.exists() || file.length() == 0)
        	return null;

    	try {
            // 一次读多个字符
            byte[] temp = new byte[8192];
            int byte_read = 0;
            FileInputStream in = new FileInputStream(file);
            while (( byte_read= in.read(temp)) != -1) {
            	ret+=new String(temp,0,byte_read);
            }
            in.close();
            if(ret.equals("")){
            	Log.e(TAG,"readFile error fileName ="+(dirName+fileName));
            	return null;
            }
            Log.d(TAG,"readFile  dirName="+dirName+" fileName ="+fileName+" ret = "+ret);	
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

	//存储内容到指定文件
	public static boolean saveCacheFile(String fileName, String content) {
		if(content == null)
			return false;

		Log.d(TAG, "saveCacheFile(" + fileName + ")");	

		String dirName = getCacheDir();
		File dir = new File(dirName);
		if(!dir.exists())//先判断目录是否存在，不存在，先创建目录
		{
			dir.mkdirs();
			try {
//				ShellUtils.execCommand("chmod 777 "+dir.toString());
			} catch (Exception e) {
			}
		}

		File file = new File(dir, fileName);
		if(file.exists())
			file.delete();//删除旧的

		if (file != null) {
			try {
				FileOutputStream output = new FileOutputStream(file);
				OutputStreamWriter outputStream = new OutputStreamWriter(output, "utf-8");
				outputStream.write(content);
				outputStream.close();
				output.flush();
				output.close();
//				ShellUtils.execCommand("chmod 777 "+file.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG,"saveCacheFile ok....file.length = "+file.length()+" file = "+(dirName+fileName)
				+" readable = "+file.canRead());
		return true;
	}
	
	//删除getCacheDir下的指定文件
	public static void deleteCacheFile(String fileName){
		String dir = getCacheDir();
		File file =  new File(dir,fileName);
		if (file != null) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void deleteFile(String dir,String fileName){
		
		File file = new File(dir,fileName);
		if (file != null) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//删除getCacheDir的文件夹 大动作，会删除所有缓存文件，慎重
	public static boolean deleteCacheDir() {
		try {
			String dirName = getCacheDir();
			File dir = new File(dirName);
			File[] files = dir.listFiles();
			if(null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile())
						files[i].delete();
				}
			}
			dir.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
		}
		return false;
	}

	public static File getCacheFile(String fileName) {
		try {
			File file = new File(getCacheDir(), fileName);
			return file;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int[] getTandR(StatFs sf){
		int availableBlocks = sf.getAvailableBlocks();
	    int blockCount = sf.getBlockCount();
		int size = sf.getBlockSize();

		BigInteger totalSizeBig= ( new BigInteger(blockCount+"")).multiply( new BigInteger(size+""));
		int totalSize = Util.safeIntegerParse(totalSizeBig.divide(new BigInteger(1024*1024+"")).toString(10));
		BigInteger availableSizeBig= ( new BigInteger(availableBlocks+"")).multiply( new BigInteger(size+""));
		int availableSize = Util.safeIntegerParse(availableSizeBig.divide(new BigInteger(1024*1024+"")).toString(10));

		int[] a=new int[2];
		a[0]=totalSize;//单位M
		a[1]=availableSize;//单位M
		return a;
	}
	
	/*文件失效或者文件不存在返回true，否则返回false*/
	public static boolean isFileTimeOut(String fileName){
		boolean res = false;
		
		if(fileName ==  null || fileName.equals("")){
			return true;
		}
		String dirName = UtilFile.getCacheDir();
        File file = new File(dirName,fileName);
		if(!file.exists()){
			return true;
		}
		
		long timeGap = System.currentTimeMillis() - file.lastModified();
		if(timeGap/1000 > TIMEOUT ){
			file.delete();
			return true;
		}
		return res;
	}
	
	/*文件失效或者文件不存在返回true，否则返回false*/
	public static boolean isFileTimeOutDel(String fileName,boolean del){
		boolean res = false;
		
		if(fileName ==  null || fileName.equals("")){
			return true;
		}
		String dirName = UtilFile.getCacheDir();
        File file = new File(dirName,fileName);
		if(!file.exists()){
			return true;
		}
		
		long timeGap = System.currentTimeMillis() - file.lastModified();
		if((timeGap/1000 > TIMEOUT) || del ){
			file.delete();
			return true;
		}
		return res;
	}
	
	//删除getCacheDir下满足过滤条件的file
	public static void clearCacheFileByfilter(String filter){
		
		File dir = new File(getCacheDir());
		if(!dir.isDirectory() || !dir.exists() || filter== null || filter.equals("")){
			return ;
		}
		File flist[] = dir.listFiles();
		if(null != flist) {
			for(int i=0;i< flist.length;i++){
				if(flist[i].getName().contains(filter))
				{
					flist[i].delete();
				}
			}
		}
	}
	//取得指定文件夹当前目录下的文件列表,不包括子目录下的文件,可按条件过滤(满足过滤条件的才返回)，为空不过滤
	public static ArrayList<File> getDirFile(String dirStr,String filter){
		ArrayList<File> list = new ArrayList<File>();
		File dir = new File(dirStr);
		if(!dir.isDirectory() || !dir.exists()){
			return null;
		}
		File flist[] = dir.listFiles();
		if(null != flist) {
			for(int i=0;i< flist.length;i++){
				if(filter != null && !filter.equals("")){
					if(flist[i].getName().endsWith(filter)){
						list.add(flist[i]);	
					}
				}else{
					list.add(flist[i]);
				}
			}
		}
		return list;
	}
	
	public static int dirFreeSize( String cacheDir ){
		try{
			File dir = new File(cacheDir);
			StatFs sdstate = new StatFs(dir.getPath());
			int []sizeArr = UtilFile.getTandR(sdstate);
	
	//		int totalSize = sizeArr[0];//总存储空间 单位M
			int emptySize = sizeArr[1];//剩余空间  单位M
			return emptySize;
		}catch(Exception e){}
		return 0;
	}
	
	//清理缓存空间，删除最老的文件直到空间足够，返回删除文件个数
	public static int removeOldFile( String cacheDir ){
		int cnt = 0;
		int totalSize = 0;
		int emptySize = 0;

		try{
			File dir = new File(cacheDir);
			if(!dir.exists()) return 0;
			StatFs sdstate = new StatFs(dir.getPath());
			int []sizeArr = getTandR(sdstate);

			totalSize = sizeArr[0];//sd卡总存储空间 单位M
			emptySize = sizeArr[1];//sd卡剩余空间  单位M
			int totalFileCount =0;
			
			//取得指定文件夹当前目录下的文件列表,不包括子目录下的文件
			double dirSize = 0;//缓存目录的文件总大小
			File flist[] = dir.listFiles();
	    	ArrayList<File> list = new ArrayList<File>();
			if (flist == null)
				return 0;
		    for (int i=0; i<flist.length; i++) {
		        if (!flist[i].isDirectory()) {		        	
		        	dirSize += flist[i].length();
		        	list.add(flist[i]);
		        	totalFileCount ++;
		        }
		    }
		    dirSize =  dirSize/1024/1024;
		    Log.d(TAG,"BitmapManager total=" + totalSize + "M  empty=" + emptySize + "M"
		    		+" dirSize = "+dirSize+" M"+" fileCnt "+totalFileCount);
			//按文件最后修改时间升序
			Comparator<File> comparator = new Comparator<File>() {
				@Override
				public int compare(File arg0, File arg1) {
					if(arg0.lastModified() - arg1.lastModified() > 0)
						return 1;
					else if(arg0.lastModified() - arg1.lastModified() < 0)
						return -1;
					else
						return 0;
				}
			};
			Collections.sort(list, comparator);

//			if(list.size()  > 0){
//				for(File f:list){
//					Log.d(TAG,"file time = "+UtilTime.getFormatString(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
//				}	
//			}
			
			//获取的列表是按最后修改时间升序排列，即很久不用的老的文件排在前面,
			//不停的实时清理比较耗费cpu，每访问一次文件都需要修改此文件的最后使用时间，就需要重新删除文件在列表中的项
			//然后再插入排序，这个访问很频繁,所以只开机时候开始清理一次，
			while(dirSize > totalSize*2/3 || emptySize < 100 || totalFileCount > 10000){
				//大于总大小的2/3 或者剩余空间小于100M 或者文件个数大于1w个， 开始清理
				if (list.size() <= 0)
					break;
				File file = list.get(0);
				double length = file.length();

				Log.d(TAG, "delete file(" + file.getName() +")");

				dirSize -= length/1024/1024;
				emptySize += length/1024/1024;

				list.get(0).delete();
				list.remove(0);
				cnt++;
				totalFileCount--;
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cnt;
	}
	
	/**
	 * 修改文件权限
	 * 
	 * @param file
	 */
	public static void modifyFile(File file) {
		Process process = null;
		try {
			String command = "chmod -R 777 " + file.getAbsolutePath();
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(command);
			process.waitFor();
		} catch (Exception e) {

		}
	}
	
	/**
	 * 下载文件到指定目录保存成指定文件名
	 * 1.先判断文件是否存在，不存在则下载
	 * 2.已经存在，则判断文件是否完整，完整则不下载
	 * 3.不完整则断点续传,(怕断点续传文件不对了- -)
	 * **/
	public static void downloadFile(String httpUrl,String dir,String fileName){
	
		long pos = 0;
		Log.d(TAG,"downloadFile httpUrl...dir = "+dir+" fileName = "+fileName);
		//首先判断文件是否存在， 如存在则说明之前下载过， 则从上次下载点开始下载
		File f = new File(dir,fileName);
		if(f.exists() && f.isFile()){
			pos = f.length();
		}else {//如果不存在则创建之
			try {
				f.createNewFile();
			} catch (IOException e) {
				Log.e(TAG,"downloadFile IOException... ");
				e.printStackTrace();
				return;
			}
		}
		long fileLen = getFileLength(httpUrl);
		if(pos == fileLen){ //说明已经下载完毕
			Log.d(TAG,"downloadFile already downloaded! httpUrl = "+httpUrl);
			return;
		}
		Log.d(TAG,"downloadFile pos = "+pos+" fileLen = "+fileLen);
		String savePath = dir+fileName;
		if(httpUrl.startsWith("http://")){
			httpDownload(httpUrl, savePath, pos,fileLen);
		}else {
			Log.e(TAG, "downloadFile unsurport download protocal!");
		}
		
	}
	
	public static boolean isLocalAdTsFileCompelete(String fileName,long fileLen){
		File f = new File(getAdCacheFileDir(),fileName);
		long pos = 0;
		if(f.exists() && f.isFile()){
			pos = f.length();
		}else{
			return false;
		}
		if(pos > 0 && pos == fileLen){
			return true;
		}
		return false;
	}
	
	/*
	 * http 下载， 支持断点续传
	 */
	private static boolean httpDownload(String url, String savePath, long pos,long fileLen){
		Log.i(TAG, "start httpDownload url="+url+" pos="+pos+" savePath = "+savePath);
		//向前seek 1M字节开始下载，避免严格从当前断点开始， 看能否解决MD5校验失败的问题
		pos -= 1024*1024;
		if(pos < 0){
			pos = 0;
		}
		Log.i(TAG, "back seek pos to "+pos);
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection)httpUrl.openConnection();
			httpConnection.setConnectTimeout(1*60*1000); //连接超时1分钟
			httpConnection.setReadTimeout(60*1000); //读数据超时30秒钟
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			if(pos > 0){
				httpConnection.setRequestProperty("RANGE","bytes="+pos+"-");
			}
			httpConnection.connect();
			InputStream input = httpConnection.getInputStream();
			RandomAccessFile file = new RandomAccessFile(savePath, "rw");
			file.seek(pos);
			byte[] b = new byte[1024*1024];
			int nRead;
			long downSize;
			double percent;
			int progress;
			while((nRead=input.read(b,0,1024*1024)) > 0){
				file.write(b, 0, nRead);
				//更新下载进度
				downSize = file.length();
				percent = Math.floor(downSize * 100 / fileLen);
				progress = (int) percent;
				Log.d(TAG,"httpDownload progress = "+progress + " nRead= "+nRead);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			downSize = file.length();
			input.close();
			file.close();
			if(downSize == fileLen){//下载完成
				Log.d(TAG,"downloadFile  success!!!");
				return true;
			}else {
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static long getFileLength(String url){
		if(url.startsWith("http://")){
			return getRemoteFileSize(url);
		}else if(url.startsWith("file://")){
			String path = url.substring(7);
			File f = new File(path);
			if(f.exists() && f.isFile()){
				return f.length();
			}
		}
		return 0;
	}
	
	// 获取远程文件的大小
	public static long getRemoteFileSize(String url) {
		Log.d(TAG, "...getRemoteFileSize()..");
		long size = 0;
		try {
			HttpGet httpGet = new HttpGet(url);
			org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
			HttpResponse response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200)
				size = response.getEntity().getContentLength();
		} catch (Exception e) {
			Log.d(TAG,
					"&&&&....getRemoteFileSize()..&&&&&&&&&&&&&&="
							+ e.toString());
		}
		Log.d(TAG, "...getRemoteFileSize() size= " + size);
		return size;
	}
	
	
}
