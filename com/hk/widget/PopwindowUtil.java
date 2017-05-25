package com.hk.view.widget;

import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.bean.AdContent;
import com.tvb.hk.anywhere.bean.AdListBean;
import com.tvb.hk.anywhere.bean.AdRule;
import com.tvb.hk.anywhere.data.DataListener.AdDataListener;
import com.tvb.hk.anywhere.data.DataManager;
import com.tvb.hk.anywhere.sdk.bos.BOSClient;
import com.tvb.hk.anywhere.sdk.media.MediaBean;
import com.tvb.hk.anywhere.sdk.param.Parameter;
import com.tvb.hk.anywhere.util.UIHandler;
import com.tvb.hk.anywhere.util.Util;
import com.tvb.hk.anywhere.view.ViewAdjust;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopwindowUtil {
	
	
	protected static final String TAG = "PopwindowUtil";
	public static void showMeidaDetialPopwindow(Context c ,View rootView,String text){
		
		LinearLayout contentView = (LinearLayout)LayoutInflater.from(c).inflate(R.layout.popwindow_show_vod_detial,null);
		
		ViewAdjust.adjustLayoutParamsView(c, contentView, R.id.root_view,R.id.txt,
				R.id.btn_layout,R.id.text,R.id.back_btn);
		
		final TextView textView = (TextView)contentView.findViewById(R.id.text);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		textView.setText(text);
		
		final Button button = (Button)contentView.findViewById(R.id.back_btn);
		
		final PopupWindow popWindow = new PopupWindow(contentView,
				ViewAdjust.getWidthPxAdjust(c, 770),
				ViewAdjust.getHeightPxAdjust(c, 460));
		popWindow.setFocusable(true);
		button.setFocusable(true);
		button.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }

				if(keycode == KeyEvent.KEYCODE_BACK 
						|| keycode == KeyEvent.KEYCODE_DPAD_CENTER){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_UP){
					textView.requestFocus();
				}
				return true;
			}
		});
		textView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }

				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}
				return false;
			}
		});
		popWindow.showAtLocation(rootView,Gravity.CENTER ,0, 0);
		button.requestFocus();
	}

    public static void showPurchasePopwindow(Context c ,View rootView,MediaBean mb,
    		final OnClickListener basicBuyListener,
    		final OnClickListener promotionBuyListener,
    		final OnClickListener singleBuyListener){
		
		LinearLayout contentView = (LinearLayout)LayoutInflater.from(c).inflate(R.layout.popwindow_show_purchase,null);
		
		ViewAdjust.adjustLayoutParamsView(c, contentView,R.id.root_view,R.id.txt, R.id.btn_layout,
				R.id.btn_layout1,R.id.btn_layout2,R.id.btn_layout3,
				R.id.text1,R.id.text2,R.id.text3,
				R.id.basic_buy_btn,R.id.promotion_buy_btn,R.id.single_buy_btn,R.id.back_btn);
	
		final Button basic_buy_btn = (Button)contentView.findViewById(R.id.basic_buy_btn);
		final Button promotion_buy_btn = (Button)contentView.findViewById(R.id.promotion_buy_btn);
		final Button single_buy_btn = (Button)contentView.findViewById(R.id.single_buy_btn);
		final Button backButton = (Button)contentView.findViewById(R.id.back_btn);
		
		final PopupWindow popWindow = new PopupWindow(contentView,
				ViewAdjust.getWidthPxAdjust(c, 770),
				ViewAdjust.getHeightPxAdjust(c, 460));
		popWindow.setFocusable(true);
		basic_buy_btn.setFocusable(true);
		promotion_buy_btn.setFocusable(true);
		single_buy_btn.setFocusable(true);
		backButton.setFocusable(true);
		basic_buy_btn.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER && basicBuyListener!= null){
					basicBuyListener.onClick(null);
					popWindow.dismiss();
				}
				return false;
			}
		});
		promotion_buy_btn.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }

				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER && promotionBuyListener != null){
					promotionBuyListener.onClick(null);
					popWindow.dismiss();
				}
				return false;
			}
		});
		single_buy_btn.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }

				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER && singleBuyListener != null){
					singleBuyListener.onClick(null);
					popWindow.dismiss();
				}
				return false;
			}
		});
		backButton.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
					popWindow.dismiss();
				}
				return false;
			}
		});
		
		popWindow.showAtLocation(rootView,Gravity.CENTER ,0, 0);
		basic_buy_btn.requestFocus();
	}
    
    public static PopupWindow showCommonPopwindow(Context c ,View rootView,String textTip,
    		final OnClickListener okClickListener){
    	return showCommonPopwindow(c,rootView,textTip,okClickListener,"",null,"");
    }
    
    public static PopupWindow showCommonPopwindow(Context c ,View rootView,String textTip,
    		final OnClickListener okClickListener,String okButonText,
    		final OnClickListener cancleClickListener,String canButonText){
		
		LinearLayout contentView = (LinearLayout)LayoutInflater.from(c).inflate(R.layout.popwindow_common,null);
		
		ViewAdjust.adjustLayoutParamsView(c, contentView,R.id.root_view, R.id.txt,R.id.btn_layout,R.id.ok_btn,R.id.cancel_btn);
	
		TextView textView = (TextView)contentView.findViewById(R.id.txt);
		textView.setText(textTip);
		final Button okButton = (Button)contentView.findViewById(R.id.ok_btn);
		if(!Util.isEmpty(okButonText)){
			okButton.setText(okButonText);
		}
		final Button cancelButton = (Button)contentView.findViewById(R.id.cancel_btn);
		if(!Util.isEmpty(canButonText)){
			cancelButton.setText(canButonText);
		}
		final PopupWindow popWindow = new PopupWindow(contentView,
				ViewAdjust.getWidthPxAdjust(c, 765),
				ViewAdjust.getHeightPxAdjust(c, 435));
		popWindow.setFocusable(true);
		okButton.setFocusable(true);
		cancelButton.setFocusable(true);
		cancelButton.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
					if(cancleClickListener != null){
						cancleClickListener.onClick(null);
					}
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT){
					okButton.requestFocus();
				}
				return true;
			}
		});
		okButton.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }

				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
					if(okClickListener != null){
						okClickListener.onClick(null);
					}
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_LEFT){
					cancelButton.requestFocus();
				}
				return true;
			}
		});
		popWindow.showAtLocation(rootView,Gravity.CENTER ,0, 0);
		okButton.requestFocus();
		return popWindow;
	}
	
    public static void showPurchaseTipPopwindow(Context c ,View rootView,final OnClickListener listener){
    	
    	LinearLayout contentView = (LinearLayout)LayoutInflater.from(c).inflate(R.layout.popwindow_show_purchase_tip,null);
		
		ViewAdjust.adjustLayoutParamsView(c, contentView,R.id.root_view, R.id.txt,R.id.btn_layout
				,R.id.text1,R.id.text2,R.id.back_btn);
	
		
		final PopupWindow popWindow = new PopupWindow(contentView,
				ViewAdjust.getWidthPxAdjust(c, 770),
				ViewAdjust.getHeightPxAdjust(c, 460));
		popWindow.setFocusable(true);
		Button cancelButton = (Button)contentView.findViewById(R.id.back_btn);
		cancelButton.setFocusable(true);
		cancelButton.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
				if(keycode == KeyEvent.KEYCODE_BACK ){
					popWindow.dismiss();
				}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
					if(listener == null){
						popWindow.dismiss();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		if(listener != null){
			cancelButton.setOnClickListener(listener);
		}		
		popWindow.showAtLocation(rootView,Gravity.CENTER ,0, 0);
		cancelButton.requestFocus();
    	
    }
    public static void showPurchaseTipPopwindow(Context c ,View rootView){
		
    	showPurchaseTipPopwindow(c,rootView,null);
		
	}
    private static  PopupWindow mMessagePopWindow = null;
	public static void showMessagePopwindow(Context c, View rootView, String text,int dur,boolean scroll) {

		if(Util.isEmpty(text)){
			return;
		}
		dur = dur <= 10 ? 10: dur;
		try {
			if (mMessagePopWindow != null && mMessagePopWindow.isShowing()) {
				((MarqueeTextView)mMessagePopWindow.getContentView().findViewById(R.id.scroll_text)).setSrollAuto(scroll);
				((MarqueeTextView)mMessagePopWindow.getContentView().findViewById(R.id.scroll_text)).setText(text);
				mMessagePopWindow.update();
				UIHandler.getInstance().remove(dissMissTask);
				UIHandler.getInstance().postDelay(dissMissTask,dur*1000);
				return;
			}
			
			if(mMessagePopWindow == null){
				LinearLayout contentView = (LinearLayout) LayoutInflater.from(c).inflate(
						R.layout.popwindow_show_roll_message,null);
				mMessagePopWindow = new PopupWindow(contentView,
						ViewAdjust.getScreenWidthPx(c)-50,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				mMessagePopWindow.setFocusable(false);			
				mMessagePopWindow.showAtLocation(rootView, Gravity.BOTTOM|Gravity.LEFT, 20, 5);		
			}
				
			((MarqueeTextView)mMessagePopWindow.getContentView().findViewById(R.id.scroll_text)).setSrollAuto(scroll);
			((MarqueeTextView)mMessagePopWindow.getContentView().findViewById(R.id.scroll_text)).setText(text);
			mMessagePopWindow.update();
			UIHandler.getInstance().remove(dissMissTask);
			UIHandler.getInstance().postDelay(dissMissTask,dur*1000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private static Runnable dissMissTask =  new Runnable() {
		
		@Override
		public void run() {
			if(mMessagePopWindow != null && mMessagePopWindow.isShowing()){
				Log.d(TAG,"dissMissTask...");
				mMessagePopWindow.dismiss();
			}
		}
	};
	
   private static  PopupWindow mTCPopWindow = null;

	public static void showTermsAndConditons(final Context c, final View rootView) {

		if (c == null) {
			return;
		}
		if (mTCPopWindow != null && mTCPopWindow.isShowing()) {
			return;
		}
		DataManager.getInstance().setAdDataListener(AdRule.RULE_TC, new AdDataListener() {

			@Override
			public void onAdDataChanged(AdListBean alb) {

				AdContent ac = AdListBean.getAdContent(alb, -1, Parameter.getLanguage());
				
				LinearLayout contentView = (LinearLayout) LayoutInflater.from(c).inflate(R.layout.popwindow_show_tc,
						null);

				ViewAdjust.adjustLayoutParamsView(c, contentView, R.id.root_view, R.id.txt,
						R.id.btn_layout, R.id.text,R.id.confirm_btn);

				final TextView textView = (TextView) contentView.findViewById(R.id.text);
				textView.setMovementMethod(ScrollingMovementMethod.getInstance());
				textView.setText(ac.item);

				final Button button = (Button) contentView.findViewById(R.id.confirm_btn);

				mTCPopWindow = new PopupWindow(contentView, ViewAdjust.getWidthPxAdjust(c, 1540),
						ViewAdjust.getHeightPxAdjust(c, 870));
				mTCPopWindow.setFocusable(true);				
				button.setFocusable(true);
				button.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(View arg0, int keycode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_UP) {
							return false;
						}

						if (keycode == KeyEvent.KEYCODE_BACK) {
							return true;
						} else if (keycode == KeyEvent.KEYCODE_DPAD_UP) {
							textView.requestFocus();
						}else if(keycode == KeyEvent.KEYCODE_DPAD_CENTER){
							mTCPopWindow.dismiss();
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									int retCode = BOSClient.getInstance().activite();
									if(retCode == 0){
										UIHandler.getInstance().showToast(c, c.getString(R.string.tc_ok));
									}else{
										UIHandler.getInstance().showToast(c, c.getString(R.string.tc_failed,retCode,Parameter.getTerminalId()));
									}
									
								}
							}).start();
						}
						return true;
					}
				});
				textView.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(View arg0, int keycode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_UP) {
							return false;
						}

						if (keycode == KeyEvent.KEYCODE_BACK) {
							//popWindow.dismiss();
							return true;
						}
						return false;
					}
				});
				mTCPopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
				textView.requestFocus();
			}
		});
		
	}
}
