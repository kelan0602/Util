package com.tvb.hk.anywhere.view;

import com.hk.view.widget.CheckRadioButton;
import com.tvb.hk.anywhere.sdk.param.DefaultParam;
import com.tvb.hk.anywhere.util.SystemParamUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class ViewAdjust {

	private static final String TAG = "ViewAdjust";
	private static final float DESIGIN_WIDTH = 1640.0f;//设计图整屏幕的宽高像素
	private static final float DESIGIN_HEIGTH = 922.0f;

	public static float getDensityDpi(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.densityDpi;
	}

	public static int getScreenWidthDip(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return (int) (dm.widthPixels / dm.density);
	}
	
	public static float getScreenDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
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
	
	public static int mStatusBarHeight= 0;
	public static int getScreenStatusBarHeight(Context context){
//		if(mStatusBarHeight == 0){
//			android.view.Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
//			DisplayMetrics dm = new DisplayMetrics();
//			d.getMetrics(dm);
//			
//			Rect rect= new Rect();  
//			((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
//			mStatusBarHeight= (dm.heightPixels -rect.height());
//			Log.d(TAG,"getScreenStatusBarHeight "+mStatusBarHeight +" "+dm.heightPixels+" "+rect.height());
//			return mStatusBarHeight;
//		}
		return ViewAdjust.getWidthPxAdjust(context, 40);
		
		
	}
	public static int getScreenMinsStatusBarHeightPx(Context context){
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Rect rect= new Rect();  
		((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
		//Log.d(TAG,"rect.height() = "+rect.height()+" getScreenHeightPx(context)-rect.top = "+(getScreenHeightPx(context)-rect.top));
		
		String brand = SystemParamUtil.getSysProp("ro.product.brand");
		//Log.d(TAG," ro.product.brand  = "+brand);
		if(!TextUtils.isEmpty(brand) && brand.toLowerCase().contains("meizu")){
			//Log.d(TAG,"meizu visibile height = "+(rect.height()- 48*dm.density));
			return (int) (rect.height()- 48*dm.density);//减去smartbar高度
		}
		
		return rect.height();//getScreenHeightPx(context)-rect.top
	}

	public static int mScreenWidthPx = 0;
	public static int mScreenHeightPx = 0;
	/*
	 * 根据设计图中的像素宽高，经过设计图宽高 换算 返回当前设备下的像素值
	 */
	public static int getWidthPxAdjust(Context c, int wPx) {
		if(mScreenWidthPx <= 0){
			mScreenWidthPx = getScreenWidthPx(c);
		}
		
		//Log.d(TAG," wpx = " +wPx +" ---->  " +(wPx * mScreenWidthPx / DESIGIN_WIDTH));
		return (int) (wPx * mScreenWidthPx / DESIGIN_WIDTH);
	}

	public static int getHeightPxAdjust(Context c, int hPx) {
		if(mScreenHeightPx <= 0){
			mScreenHeightPx = getScreenHeightPx(c);
		}
		//Log.d(TAG," hPx = " +hPx +" ---->  " +(hPx * mScreenHeightPx / DESIGIN_HEIGTH));
		return (int) (hPx * mScreenHeightPx / DESIGIN_HEIGTH);
		
	}
	
	public static int getScreenWidthPx(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.widthPixels;
	}

	public static int getScreenHeightPx(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm.heightPixels;
	}

	public static float adapterTextSize(Context c,float f){
		return (f/(c.getResources().getDisplayMetrics().scaledDensity));
	}
	
	private static void specialViewAdjust(Context a,View view){
		if(view instanceof TextView ){
			TextView tv = (TextView)view;
			tv.setPadding(getWidthPxAdjust(a,tv.getPaddingLeft()),
					getWidthPxAdjust(a,tv.getPaddingTop()),
					getWidthPxAdjust(a,tv.getPaddingRight()), 
					getWidthPxAdjust(a,tv.getPaddingBottom()));
			tv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)tv.getTextSize())));
		}else if(view instanceof Button){
			Button bv = (Button)view;
			bv.setPadding(getWidthPxAdjust(a,bv.getPaddingLeft()),
					getWidthPxAdjust(a,bv.getPaddingTop()),
					getWidthPxAdjust(a,bv.getPaddingRight()), 
					getWidthPxAdjust(a,bv.getPaddingBottom()));
			bv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)bv.getTextSize())));
		}else if(view instanceof RadioButton){
			RadioButton rbv= (RadioButton) view;
			rbv.setPadding(getWidthPxAdjust(a,rbv.getPaddingLeft()),
					getWidthPxAdjust(a,rbv.getPaddingTop()),
					getWidthPxAdjust(a,rbv.getPaddingRight()), 
					getWidthPxAdjust(a,rbv.getPaddingBottom()));
			rbv.setTextSize(adapterTextSize(a, getHeightPxAdjust(a, (int) rbv.getTextSize())));
		}
	}
	
	public static void adjustLayoutParamsView(Context a,View parent,int... ids) {
		
		if (a == null || parent ==  null) {
			return;
		}
		View view;
		for (int i = 0; ids != null && i < ids.length; i++) {
			view = (View)parent.findViewById(ids[i]);
			if(view == null){
				Log.e(TAG,"adjustLayoutParamsView error..id error...id = "+Integer.toHexString(ids[i]));
				i=i/0;				
				continue;
			}
			android.view.ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
			params.leftMargin = getWidthPxAdjust(a, params.leftMargin);
			params.topMargin = getHeightPxAdjust(a, params.topMargin);
			params.rightMargin = getWidthPxAdjust(a, params.rightMargin);
			params.bottomMargin= getHeightPxAdjust(a, params.bottomMargin);
			if (params.width > 0) {
				params.width  = getWidthPxAdjust(a, params.width);
			}
			if (params.height > 0) {
				params.height = getHeightPxAdjust(a, params.height);
			}
			specialViewAdjust(a,view);
			
		}

	}
	
	public static void adjustLayoutParams(Context a, View parent) {

		if (a == null || parent == null) {
			Log.e(TAG, "adjustLayoutParams..error..");
			int i=0;
			i=i/0;
			return;
		}
		View view = parent;
		android.view.ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
		if(params != null ){
			params.leftMargin = getWidthPxAdjust(a, params.leftMargin);
			params.topMargin = getHeightPxAdjust(a, params.topMargin);
			params.rightMargin = getWidthPxAdjust(a, params.rightMargin);
			params.bottomMargin = getHeightPxAdjust(a, params.bottomMargin);
			if (params.width > 0) {
				params.width = getWidthPxAdjust(a, params.width);
			}
			if (params.height > 0) {
				params.height = getHeightPxAdjust(a, params.height);
			}
			specialViewAdjust(a,view);
		}else{
			Log.e(TAG,"adjustLayoutParams error..id error...id = "+Integer.toHexString(view.getId()));
		}

		if (parent instanceof ViewGroup) {
			ViewGroup root = (ViewGroup) parent;
			for (int i = 0; i < root.getChildCount(); i++) {
				adjustLayoutParams(a, root.getChildAt(i));
			}
		}
		
	}
	
	//phone Horizon adjust
//	public static void adjustLayoutParamsViewHorizon(Context a,View parent,int... ids) {
//		
//		if (a == null || parent ==  null) {
//			return;
//		}
//		View view;
//		for (int i = 0; ids != null && i < ids.length; i++) {
//			view = (View)parent.findViewById(ids[i]);
//			if(view == null){
//				Log.e(TAG,"adjustLayoutParamsView error..id error...id = "+Integer.toHexString(ids[i]));
//				//i=i/0;
//				continue;
//			}
//			android.view.ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
//			params.leftMargin = getHeightPxAdjust(a, params.leftMargin);
//			params.topMargin = getWidthPxAdjust(a, params.topMargin);
//			params.rightMargin = getHeightPxAdjust(a, params.rightMargin);
//			params.bottomMargin= getWidthPxAdjust(a, params.bottomMargin);
//			if (params.width > 0) {
//				params.width  = getHeightPxAdjust(a, params.width);
//			}
//			if (params.height > 0) {
//				params.height = getWidthPxAdjust(a, params.height);
//			}
//			specialViewAdjust(a,view);
//			view.setLayoutParams(params);
//		}
//
//	}

}
