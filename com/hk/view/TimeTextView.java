package com.tvb.hk.anywhere.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.greenrobot.eventbus.EventBus;

import com.tvb.hk.anywhere.events.Event.HalfHourEvent;
import com.tvb.hk.anywhere.events.Event.NewDayEvent;
import com.tvb.hk.anywhere.util.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TimeTextView extends TextView{

	private static final String TAG = "TimeTextView";
	private boolean isRunning = true;
	private String mLastDateString = "";
	private int mLastTimeZone = 0;
	private Context mContext;
	private long mLastHalfHourChangeTime = 0;
	
	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "TimeTextView..");
		mContext = context;
		mHandler.sendEmptyMessage(0);
		isRunning =true;
		mLastTimeZone = Util.getTimezone();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(!isRunning){
				return;
			}
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				SimpleDateFormat dateFormate = null;
				boolean is24HourFormat = DateFormat.is24HourFormat(mContext);
				if(is24HourFormat){
					dateFormate = new SimpleDateFormat("yyyy/MM/dd\nHH:mm");
				}else{
					dateFormate = new SimpleDateFormat("yyyy/MM/dd\nHH:mm aa");
				}
				
				long nowUtcMs = Util.getUtcMs();
				String sysTimeStr= dateFormate.format(new Date(nowUtcMs));
				setText(sysTimeStr);
				if(mLastDateString.equals("")){
					mLastDateString = sysTimeStr.split("\n")[0];
				}else if(!mLastDateString.equals(sysTimeStr.split("\n")[0])){
					//开机过了一天，需要更新epg界面
					Log.d(TAG, "post NewDayEvent dat echange "+mLastDateString+"----->"+sysTimeStr.split("\n")[0]);
					mLastDateString = sysTimeStr.split("\n")[0];
					EventBus.getDefault().post(new NewDayEvent());
				}
				int currTimezone = Util.getTimezone();
				if(mLastTimeZone != currTimezone){
					mLastTimeZone = currTimezone;
					mLastHalfHourChangeTime =0;
					mLastDateString="";
				    //修改了时区
					Log.d(TAG, "post NewDayEvent Timezone changed..");
					EventBus.getDefault().post(new NewDayEvent());
				}

				if(mLastHalfHourChangeTime == 0){
					mLastHalfHourChangeTime = nowUtcMs;
				}else if(nowUtcMs -  mLastHalfHourChangeTime >= 30*60*1000){
					Log.d(TAG, "post HalfHourEvent ");
					mLastHalfHourChangeTime = nowUtcMs;
					EventBus.getDefault().post(new HalfHourEvent());
				}
				invalidate();
				mHandler.sendEmptyMessageDelayed(0, 10000);
				break;

			default:
				break;
			}
		}
	};
}
