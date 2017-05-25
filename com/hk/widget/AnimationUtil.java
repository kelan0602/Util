package com.hk.view.widget;

import com.tvb.hk.anywhere.R;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationUtil {

	public static void bottomPushIn(Context c,View v){
		Animation animation = AnimationUtils.loadAnimation(c, R.anim.push_bottom_in);		
		v.startAnimation(animation);
	}
	public static void bottomPushOut(Context c,View v){
		Animation animation = AnimationUtils.loadAnimation(c, R.anim.push_bottom_out);
		v.startAnimation(animation);
	}
	
	public static void zoomIn(Context c,View v){
		Animation animation = AnimationUtils.loadAnimation(c, R.anim.zoom_in);		
		animation.setFillAfter(true);
		animation.setFillBefore(false);
		animation.setFillEnabled(true);
		v.startAnimation(animation);
	}
	public static void zoomOut(Context c,View v){
		Animation animation = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
		animation.setFillAfter(true);
		animation.setFillBefore(true);
		animation.setFillEnabled(true);
		v.startAnimation(animation);
	}
	
}
