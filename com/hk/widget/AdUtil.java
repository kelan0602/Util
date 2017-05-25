package com.hk.view.widget;

import com.tvb.hk.anywhere.bean.AdContent;
import com.tvb.hk.anywhere.player.PlayerUtil;
import com.tvb.hk.anywhere.util.Util;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class AdUtil {

	
	private static final String TAG = "AdUtil";

	public static void clickAd(Context c,View rootView,AdContent ac){
		
		if(c == null || rootView == null || ac == null){
			Log.e(TAG,"clickAd Parameter error c= "+c+" rootView = "+rootView+" ac = "+Util.toJsonString(ac));
			return;
		}
		
		if(ac.clickmeta == AdContent.AD_CLICK_TYPE_VIDEO){
			if(Util.isEmpty(ac.click)){
				return;
			}else if(ac.click.startsWith("ams://")){
				PlayerUtil.getInstance().startPlay(ac.prefix+ac.click.substring(6), ac.title);
			}else{
				PlayerUtil.getInstance().startPlay(ac.click, ac.title);
			}
		}else if(ac.clickmeta == AdContent.AD_CLICK_TYPE_WEBURL){
			Util.startBrowser(c, ac.click);
		}else if(ac.clickmeta == AdContent.AD_CLICK_TYPE_IMAGE){
			
		}else if(ac.clickmeta == AdContent.AD_CLICK_TYPE_TEXT){
			
			PopwindowUtil.showMeidaDetialPopwindow(c, rootView, ac.item);
			
		}else if(ac.clickmeta == AdContent.AD_CLICK_TYPE_V3_MEDIA){
			
		}
	}
	
}
