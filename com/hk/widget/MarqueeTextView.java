package com.hk.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class MarqueeTextView extends TextView{

	private boolean mScrollAuto = true;
	
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MarqueeTextView(Context arg0) {
		super(arg0);
	}

	public void setSrollAuto(boolean scroll){
		mScrollAuto = scroll;
	}
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return mScrollAuto;
	}
	
	public void setText(String text){

		if(!mScrollAuto){
			super.setText(text);
			return;
		}
		int w = this.getLayoutParams().width;
		if(w<=0){
			w = 1280;
		}
		Log.i("sky", "0 w:"+w);
		float scale = this.getResources().getDisplayMetrics().density;
		w = (int)(w*scale);
		Paint paint = new Paint();
		paint.setTextSize(this.getTextSize());
		String str = text;
		int cnt = 200;
		int textWidth = (int)paint.measureText(str);
		Log.i("sky", "1 w:"+w+" textw:"+textWidth);
		w+=60;
		while(textWidth <= w && cnt-->0){
			str += "          ";
			textWidth = (int)paint.measureText(str);
		}
		Log.i("sky", "2 w:"+w+" textw:"+textWidth);
		super.setText(str);
	}
}
