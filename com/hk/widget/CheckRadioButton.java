package com.hk.view.widget;


import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.view.ViewAdjust;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 一个ImageView和TextView的组合控件，可模拟RadioButton,可对图片和文字间距进行设置 使用时应对命名空间进行定义。
 * 例：xmlns:sw ="http://schemas.android.com/apk/res/net.sunniwell.app.swsettings"
 * 属性设定为 sw:xxx=
 * 
 * 目前使用固定的组件布局：check_radio_button.xml，进一步优化可改为动态添加组件
 * 
 * 添加自定义属性
 * 
 * styleable name="CheckRadioButton" name="text1(2)" format="string" ---显示文本
 * name="text_color1(2)" format="reference|color" ----文本状态颜色 name="text_size1(2)"
 * format="dimension" ----文字大小 name="text_marginLeft1(2)" format="integer"
 * ----文本与图片边距 name="hasImage" format="boolean" ----该组件是否需要图片
 * name="image_marginLeft" format="integer" ----图片与组件左侧边距
 * name="image_src_unChecked" format="reference" ---未选 中状态下的图片
 * name="image_src_checked" format="reference" ----选中状态下的图片 name="image_bg"
 * format="reference" ----图片框的背景 name="image_alpha" format="integer" ----图片透明度
 * name="image_height" format="dimension" ----图片高度 name="image_width"
 * format="dimension" ----图片宽度 name="isChecked" format="boolean" ----默认是否选中
 * name="check_model" enum name="radio" value="0" enum name="checkbox"
 * value="1"-----组件工作模式
 * 
 * 
 * @author KL
 * 
 */
public class CheckRadioButton extends LinearLayout implements android.view.View.OnFocusChangeListener{
	
	private boolean isChecked = false, hasImage = true,canUse=true;
	private Context ctx;
	private int checkModel;
	public static final int RADIO=0,CHECKBOX=1;
	private static final String TAG = "CheckRadioButton";
	private TextView textView1,textView2;
	// Action_down发生
	private boolean actionDown = false;
	private ImageView imageView;
	private Drawable unCheckImage, checkedImage;
	private OnCheckedChangeListener mOnCheckedChangeListener;

	public CheckRadioButton(Context context) {
		super(context);
		initViews(context);
	}

	public CheckRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}
	
	private void initViews(Context context){
		
		ctx = context;
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = layoutInflater.inflate(R.layout.check_radio_button, this);
//		ViewAdjust.adjustLayoutParamsView(context, rootView.findViewById(R.id.inner_root),
//				R.id.button_textView,R.id.button_textView2,R.id.button_imageview);

		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, " has changed ="+hasFocus);
				if(hasFocus){
					textView1.setTextColor(Color.WHITE);
					textView2.setTextColor(Color.WHITE);
				}else if(canUse){
					textView1.setTextColor(ColorUtil.FOCUS_TEXT_COLOR);
					textView2.setTextColor(ColorUtil.FOCUS_TEXT_COLOR);
				}else{
					textView1.setTextColor(Color.GRAY);
					textView2.setTextColor(Color.GRAY);
				}
				CheckRadioButton.this.setSelected(hasFocus);
			}
		});
		checkModel = 1;
		textView1 = (TextView) findViewById(R.id.button_textView);
		textView2 = (TextView) findViewById(R.id.button_textView2);
		imageView = (ImageView) findViewById(R.id.button_imageview);
		int ucimageid = R.drawable.check_unchecked_normal;
		setUnCheckImage(ctx.getResources().getDrawable(ucimageid));
		int cimageid =R.drawable.check_checked_normal;
		setCheckedImage(ctx.getResources().getDrawable(cimageid));
		setCheckedState(false);
		Log.d(TAG,"CheckRadioButton 111 "+textView1.getTextSize());
	}

	public void setText1(String text) {
		textView1.setText(text);
	}

	public void setText2(String text) {
		textView2.setText(text);
	}

	/**
	 * 获取未选择状态下的图片
	 * 
	 * @return 未选择状态下的图片
	 */
	public Drawable getUnCheckImage() {
		return unCheckImage;
	}

	/**
	 * 设定未选择状态下的图片
	 * 
	 * @param unCheckImage
	 *            未选择时要显示的图片
	 */
	public void setUnCheckImage(Drawable unCheckImage) {
		this.unCheckImage = unCheckImage;
	}

	/**
	 * 获取选择状态下的图片
	 * 
	 * @return 选择状态下的图片
	 */
	public Drawable getCheckedImage() {
		return checkedImage;
	}

	/**
	 * 设定选择状态下的图片
	 * 
	 * @param CheckedImage
	 *            选择状态下要显示的图片
	 */
	public void setCheckedImage(Drawable checkedImage) {
		this.checkedImage = checkedImage;
	}

	/**
	 * 当前选择状态
	 * 
	 * @return true 已选中，false 未选中
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * 设置该组件是否被选中
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked) {

		if (checked) {
			if (hasImage) {
				imageView.setBackgroundDrawable(checkedImage);
			}
			//this.requestFocus();
		} else {
			if (hasImage) {
				imageView.setBackgroundDrawable(unCheckImage);
			}
		}
		this.isChecked = checked;
		if (mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(this, isChecked);
		}
	}

	/**
	 * 设置该组件是否选中的图片标记
	 * 
	 * @param checked
	 */
	public void setCheckedState(boolean checked)
	{

		if (checked) {
			if (hasImage) {
				imageView.setBackgroundDrawable(checkedImage);
			}
		} else {
			if (hasImage) {
				imageView.setBackgroundDrawable(unCheckImage);
			}
		}
		this.isChecked = checked;
	}
	/**
	 * 切换控件的可用性，
	 * 
	 * @param canUse
	 *            是否可用
	 */
	public void setViewState(boolean canUse) {
		this.setFocusable(canUse);
		this.setEnabled(canUse);
		this.setFocusableInTouchMode(canUse);
		
		this.canUse=canUse;
		if(this.isFocused()){
			textView1.setTextColor(Color.WHITE);
			textView2.setTextColor(Color.WHITE);
		}else if(!canUse){
			textView1.setTextColor(Color.GRAY);
			textView2.setTextColor(Color.GRAY);
		}else{
			textView1.setTextColor(ColorUtil.FOCUS_TEXT_COLOR);
			textView2.setTextColor(ColorUtil.FOCUS_TEXT_COLOR);
		}
	}

	
	public void setCanchecked(boolean canUse) {
		this.canUse=canUse;
	}

	/**
	 * 切换状态
	 */
	public void toggle() {
		setChecked(!isChecked);
	}

	/**
	 * 
	 * 响应触摸事件，radio模式下，触摸时设置选中,checkbox模式下，触摸切换
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!canUse)
			return true;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.d(VIEW_LOG_TAG, "down.....");
			actionDown = true;
			this.requestFocus();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (event.getX() < 0 || event.getY() < 0 || event.getX() > this.getWidth() || event.getY() > this.getHeight()) {
				actionDown = false;
				return true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			if (actionDown) {
				switch (checkModel) {
				case 0:
					setChecked(true);
					break;
				case 1:
					toggle();
					break;
				}
			} else {
				this.clearFocus();
			}
			actionDown = false;
		}
		return false;
	}

	/**
	 * 响应按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
			actionDown = true;
			this.requestFocus();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) && actionDown) {
			actionDown = false;
			if(!canUse)
				return true;
			switch (checkModel) {
			case 0:
				setChecked(true);
				break;
			case 1:
				toggle();
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获取该组件的选中状态改变监听器
	 * 
	 * @return CheckRadioButton.OnCheckedChangeListener
	 */
	public OnCheckedChangeListener getOnCheckedChangeListener() {
		return mOnCheckedChangeListener;
	}

	/**
	 * 为这个组件注册一个选中状态改变监听器
	 * 
	 * @param mOnCheckedChangeListener
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
		this.mOnCheckedChangeListener = mOnCheckedChangeListener;
	}

	public boolean isCanUse() {
		return canUse;
	}
	/**
	 * 立即更改图片
	 */
	public void invalidat(){
		setCheckedState(isChecked);
	}
	public static interface OnCheckedChangeListener {
		void onCheckedChanged(CheckRadioButton buttonView, boolean isCheck);
	}
	public int getCheckModel() {
		return checkModel;
	}

	public void setCheckModel(int checkModel) {
		this.checkModel = checkModel;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.e("CheckRadioButton", "This custom view's focus has changed ="+hasFocus);
		this.dispatchSetSelected(hasFocus);
	}
}
