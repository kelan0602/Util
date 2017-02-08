package com.base.util;

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
import android.widget.TextView;

public class ViewAdjust {

	private static final String TAG = "ViewAdjust";
	private static final float DESIGIN_WIDTH = 380.0f;//设计图整屏幕的宽高像素
	private static final float DESIGIN_HEIGTH = 633.0f;

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
		
//		Log.d(TAG," wpx = " +wPx +" ---->  " +(wPx * mScreenWidthPx / DESIGIN_WIDTH));
		return (int) (wPx * mScreenWidthPx / DESIGIN_WIDTH);
	}

	public static int getHeightPxAdjust(Context c, int hPx) {
		if(mScreenHeightPx <= 0){
			mScreenHeightPx = getScreenHeightPx(c);
		}
		//Log.d(TAG," hPx = " +hPx +" ---->  " +(hPx * mScreenHeightPx / DESIGIN_HEIGTH));
		return (int) (hPx * mScreenHeightPx / DESIGIN_HEIGTH);
		
	}
	
	public static int getWidthPxAdjust(Activity c, int wPx) {
		if(mScreenWidthPx <= 0){
			mScreenWidthPx = getScreenWidthPx(c);
		}
		
		//Log.d(TAG," wpx = " +wPx +" ---->  " +(wPx * mScreenWidthPx / DESIGIN_WIDTH));
		return (int) (wPx * mScreenWidthPx / DESIGIN_WIDTH);
	}

	public static int getHeightPxAdjust(Activity c, int hPx) {
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
			
			if(view instanceof TextView ){
				TextView tv = (TextView)view;
				tv.setPadding(getWidthPxAdjust(a,tv.getPaddingLeft()),
						getWidthPxAdjust(a,tv.getPaddingTop()),
						getWidthPxAdjust(a,tv.getPaddingRight()), 
						getWidthPxAdjust(a,tv.getPaddingBottom()));
				tv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)tv.getTextSize())));
			}else if(view instanceof Button){
				Button bv = (Button)view;
				bv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)bv.getTextSize())));
			}
		}

	}
	
	public static void adjustLayoutParams(Context a, View parent) {

		if (a == null || parent == null) {
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
		}else{
			Log.e(TAG,"adjustLayoutParams error..id error...id = "+Integer.toHexString(view.getId()));
		}
		
		if (view instanceof TextView) {
			TextView tv = (TextView) view;
			tv.setPadding(getWidthPxAdjust(a, tv.getPaddingLeft()), getWidthPxAdjust(a, tv.getPaddingTop()), getWidthPxAdjust(a, tv.getPaddingRight()),
					getWidthPxAdjust(a, tv.getPaddingBottom()));
			tv.setTextSize(adapterTextSize(a, getHeightPxAdjust(a, (int) tv.getTextSize())));
		} else if (view instanceof Button) {
			Button bv = (Button) view;
			bv.setTextSize(adapterTextSize(a, getHeightPxAdjust(a, (int) bv.getTextSize())));
		}

		if (parent instanceof ViewGroup) {
			ViewGroup root = (ViewGroup) parent;
			for (int i = 0; i < root.getChildCount(); i++) {
				adjustLayoutParams(a, root.getChildAt(i));
			}
		}
	}
	
	public static void adjustLayoutParamsView(Activity a,View parent,int... ids) {
		
		if (a == null || parent ==  null) {
			return;
		}
		View view;
		for (int i = 0; ids != null && i < ids.length; i++) {
			view = (View)parent.findViewById(ids[i]);
			if(view == null){
				Log.e(TAG,"adjustLayoutParamsView error..id error...id = "+Integer.toHexString(ids[i]));
				//i=i/0;
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
			
			if(view instanceof TextView ){
				TextView tv = (TextView)view;
				tv.setPadding(getWidthPxAdjust(a,tv.getPaddingLeft()),
						getWidthPxAdjust(a,tv.getPaddingTop()),
						getWidthPxAdjust(a,tv.getPaddingRight()), 
						getWidthPxAdjust(a,tv.getPaddingBottom()));
				tv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)tv.getTextSize())));
			}else if(view instanceof Button){
				Button bv = (Button)view;
				bv.setTextSize(adapterTextSize(a,getHeightPxAdjust(a,(int)bv.getTextSize())));
			}
		}

	}
	
	public static void adjustLayoutParams(Activity a, View parent) {

		if (a == null || parent == null) {
			return;
		}
		View view = parent;
		android.view.ViewGroup.MarginLayoutParams params = (android.view.ViewGroup.MarginLayoutParams) view.getLayoutParams();
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
		}else{
			if(view.getId() != 0xffffffff){
				Log.e(TAG,"adjustLayoutParams error..id error...id = "+Integer.toHexString(view.getId()));
			}
		}
		
		if (view instanceof TextView) {
			TextView tv = (TextView) view;
			tv.setPadding(getWidthPxAdjust(a, tv.getPaddingLeft()), getWidthPxAdjust(a, tv.getPaddingTop()), getWidthPxAdjust(a, tv.getPaddingRight()),
					getWidthPxAdjust(a, tv.getPaddingBottom()));
			tv.setTextSize(adapterTextSize(a, getHeightPxAdjust(a, (int) tv.getTextSize())));
		} else if (view instanceof Button) {
			Button bv = (Button) view;
			bv.setTextSize(adapterTextSize(a, getHeightPxAdjust(a, (int) bv.getTextSize())));
		}

		if (parent instanceof ViewGroup) {
			ViewGroup root = (ViewGroup) parent;
			for (int i = 0; i < root.getChildCount(); i++) {
				adjustLayoutParams(a, root.getChildAt(i));
			}
		}
	}
	
	public static void adjustLayoutParamsViewHorizon(Context a,View parent,int... ids) {
		
		if (a == null || parent ==  null) {
			return;
		}
		View view;
		for (int i = 0; ids != null && i < ids.length; i++) {
			view = (View)parent.findViewById(ids[i]);
			if(view == null){
				Log.e(TAG,"adjustLayoutParamsView error..id error...id = "+Integer.toHexString(ids[i]));
				//i=i/0;
				continue;
			}
			android.view.ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
			params.leftMargin = getHeightPxAdjust(a, params.leftMargin);
			params.topMargin = getWidthPxAdjust(a, params.topMargin);
			params.rightMargin = getHeightPxAdjust(a, params.rightMargin);
			params.bottomMargin= getWidthPxAdjust(a, params.bottomMargin);
			if (params.width > 0) {
				params.width  = getHeightPxAdjust(a, params.width);
			}
			if (params.height > 0) {
				params.height = getWidthPxAdjust(a, params.height);
			}
			
			if(view instanceof TextView ){
				TextView tv = (TextView)view;
				tv.setPadding(getWidthPxAdjust(a,tv.getPaddingLeft()),
						getWidthPxAdjust(a,tv.getPaddingTop()),
						getWidthPxAdjust(a,tv.getPaddingRight()), 
						getWidthPxAdjust(a,tv.getPaddingBottom()));
				tv.setTextSize(adapterTextSize(a,getWidthPxAdjust(a,(int)tv.getTextSize())));
			}else if(view instanceof Button){
				Button bv = (Button)view;
				bv.setTextSize(adapterTextSize(a,getWidthPxAdjust(a,(int)bv.getTextSize())));
			}
			view.setLayoutParams(params);
		}

	}

}
