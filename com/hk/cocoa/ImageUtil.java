package com.tvb.hk.anywhere.util;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.sdk.param.Parameter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.ImageView;

public class ImageUtil {

	protected static final String TAG = "ImageUtil";
	private static ImageUtil mInstance = null;
	
	private ImageUtil(){
		
	}
	
	public static ImageUtil getInstance(){
		if(mInstance == null){
			mInstance = new ImageUtil();
		}
		return mInstance;
	}

	/***
	 * 建议传入activity 或者 fragment 参数实例，glide会根据2者的生命周期更好的管理图片缓存
	 * 
	 * ***/
	public  void displayImage(Activity a,String url,ImageView iv,int defaultImageResourceId){
		
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(Util.isEmpty(url)){
			if(iv != null){
				iv.setImageResource(R.drawable.vod_image_empty_url);
			}
			return;
		}
		if(!Parameter.getBpsUseHttps()){
			url = url.replace("https://", "http://");
		}
		Glide.with(a)
       .load(url)
       .diskCacheStrategy(DiskCacheStrategy.SOURCE)
       .crossFade()
       .thumbnail(0.5f)
       .placeholder(defaultImageResourceId)
       .into(iv);
	}
	public  void displayImage(Fragment f,String url,ImageView iv,int defaultImageResourceId){
		
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(Util.isEmpty(url)){
			if(iv != null){
				iv.setImageResource(R.drawable.vod_image_empty_url);
			}
			return;
		}
		if(!Parameter.getBpsUseHttps()){
			url = url.replace("https://", "http://");
		}
		
		Glide.with(f)
       .load(url)
       .diskCacheStrategy(DiskCacheStrategy.SOURCE)
       .crossFade()
       .thumbnail(0.5f)
       .placeholder(defaultImageResourceId)
       .into(iv);
	}
	
	public  void displayImage(Context c,String url,ImageView iv,int defaultImageResourceId){
		
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(Util.isEmpty(url)){
			if(iv != null){
				iv.setImageResource(R.drawable.vod_image_empty_url);
			}
			return;
		}
		if(!Parameter.getBpsUseHttps()){
			url = url.replace("https://", "http://");
		}
		
		Glide.with(c)
        .load(Util.encodeURL(url))        
        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
        .crossFade()
        .thumbnail(0.5f)
        .placeholder(defaultImageResourceId)        
        .into(iv);
	}

	
	
	
}
