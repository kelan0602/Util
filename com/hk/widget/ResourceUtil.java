package com.hk.view.widget;

import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.util.Util;

public class ResourceUtil {

	public static int getTvBgResId(int position){
		
		int mod = position%10;
		
		if(mod == 0){
			return	R.drawable.tv_logo_bg1;
		}else if(mod == 1){
			return	R.drawable.tv_logo_bg2;
		}else if(mod == 2){
			return	R.drawable.tv_logo_bg3;
		}else if(mod == 3){
			return	R.drawable.tv_logo_bg4;
		}else if(mod == 4){
			return	R.drawable.tv_logo_bg5;
		}else if(mod == 5){
			return	R.drawable.tv_logo_bg6;
		}else if(mod == 6){
			return	R.drawable.tv_logo_bg7;
		}else if(mod == 7){
			return	R.drawable.tv_logo_bg8;
		}else if(mod == 8){
			return	R.drawable.tv_logo_bg9;
		}else if(mod == 9){
			return	R.drawable.tv_logo_bg10;
		}
		return R.drawable.tv_logo_bg1;
	}
	
	/*
	public static int getTvLogoResId(String channel_id){
		
		int retId = -1;
		if(Util.isEmpty(channel_id)){
			return retId;
		}
		
		if(channel_id.equals("Jade_hk_hevc")){
			retId = R.drawable.logo_jade;
		}else if(channel_id.equals("Kungfu_hevc")){
			retId = R.drawable.logo_kongfu;
		}else if(channel_id.equals("TVBRadio_hevc")){
			retId = R.drawable.logo_radio;
		}else if(channel_id.equals("TVBlive_hevc")){
			retId = R.drawable.logo_tvb_live;
		}else if(channel_id.equals("I-News_hevc")){
			retId = R.drawable.logo_news;
		}else if(channel_id.equals("E-News_hevc")){
			retId = R.drawable.logo_enews;
		}else if(channel_id.equals("tvbj_aus_hevc")){
			retId = R.drawable.logo_tvbj;
		}
		return retId;
		
	}*/
	
}
