package com.tvb.hk.anywhere.view;

import org.greenrobot.eventbus.EventBus;

import com.hk.view.widget.HListView;
import com.tvb.hk.anywhere.events.Event;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.GridView;
import android.widget.ListView;

public class FocusManager {

	protected static final String TAG = "FocusManager";

	/*** !!!注意：使用了这个接口的view则不能再设置onKey监听，否则先设置的会被覆盖，无法起作用，后设置的起作用 ***/
	// nextXXView != null 则强制转移到nextXXView  FocusActionListener ,
	// nextxxView == null && forceXX == true 则强制焦点不转移
	// nextxxView == null && forceXX == false 则焦点不强制处理，交给系统处理
	public static boolean forceNextFoucs(final View v, 
			final View nextUpView, final boolean forceUp,
			final View nextDownView, final boolean forceDown, 
			final View nextLeftView, final boolean forceLeft,
			final View nextRightView, final boolean forceRight) {
		return forceNextFoucsListener(v,
				nextUpView,forceUp,null,
				nextDownView,forceDown,null,
				nextLeftView,forceLeft,null,
				nextRightView,forceRight,null);
	}

	
	public static boolean forceNextFoucsListener(final View v, 
			final View nextUpView,   final boolean forceUp,  final FocusActionListener upActionListener,
			final View nextDownView, final boolean forceDown, final FocusActionListener downActionListener,
			final View nextLeftView, final boolean forceLeft, final FocusActionListener leftActionListener,
			final View nextRightView, final boolean forceRight,final FocusActionListener rightActionListener) {

		if (v == null) {
			return false;
		}
		v.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					return false;
				}
				boolean ret = false;
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					if (v instanceof GridView) {
						GridView gv = (GridView) v;
						if (isFirstLine(gv)) {
							if (nextUpView != null) {
								requestFocus(nextUpView);
								ret = true;
							}else if (!forceUp) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else if (v instanceof ListView) {
						ListView lv = (ListView) v;
						if (isFirstLine(lv)) {
							if (nextUpView != null) {
								requestFocus(nextUpView);
								ret = true;
							}else if (!forceUp) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else {
						if (nextUpView != null) {
							requestFocus(nextUpView);
							ret = true;
						}else if (!forceUp) {
							ret = false;
						} else {
							ret = true;
						}
					}
					if(upActionListener != null){
						upActionListener.onAction();
					}
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					if (v instanceof GridView) {
						GridView gv = (GridView) v;
						if (isLastLine(gv)) {
							if (nextDownView != null) {
								requestFocus(nextDownView);
								ret = true;
							} else if (!forceDown) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else if (v instanceof ListView) {
						ListView lv = (ListView) v;
						if (isLastLine(lv)) {
							if (nextDownView != null) {
								requestFocus(nextDownView);
								ret = true;
							} else if (!forceDown) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else {
						if (nextDownView != null) {
							requestFocus(nextDownView);
							ret = true;
						} else if (!forceDown) {
							ret = false;
						} else {
							ret = true;
						}
					}
					if(downActionListener != null){
						downActionListener.onAction();
					}
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:

					if (v instanceof GridView) {
						GridView gv = (GridView) v;
						if (isFirstRow(gv)) {
							if (nextLeftView != null) {
								requestFocus(nextLeftView);
								ret = true;
							} else if (gv.getSelectedItemPosition() > 0) {
								gv.setSelection(gv.getSelectedItemPosition() - 1);
								ret = true;
							} else if (!forceLeft) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else if (v instanceof HListView) {
						HListView lv = (HListView) v;
						if (lv.getSelectedItemPosition() == 0) {
							if (nextLeftView != null) {
								requestFocus(nextLeftView);
								ret = true;
							} else if (!forceLeft) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else {
						if (nextLeftView != null) {
							requestFocus(nextLeftView);
							ret = true;
						} else if (!forceLeft) {
							ret = false;
						} else {
							ret = true;
						}
					}
					if(leftActionListener != null){
						leftActionListener.onAction();
					}
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:

					if (v instanceof GridView) {
						GridView gv = (GridView) v;
						if (isLastRow(gv)) {
							if (nextRightView != null) {
								requestFocus(nextRightView);
								ret = true;
							} else if (gv.getSelectedItemPosition() + 1 < gv.getCount()) {
								gv.setSelection(gv.getSelectedItemPosition() + 1);
								ret = true;
							} else if (!forceRight) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else if (v instanceof HListView) {
						HListView lv = (HListView) v;
						if (lv.getSelectedItemPosition() + 1 == lv.getCount()) {
							if (nextRightView != null) {
								requestFocus(nextRightView);
								ret = true;
							} else if (!forceRight) {
								ret = false;
							} else {
								ret = true;
							}
						}
					} else {
						if (nextRightView != null) {
							requestFocus(nextRightView);
							ret = true;
						} else if (!forceRight) {
							ret = false;
						} else {
							ret = true;
						}
					}
					if(rightActionListener != null){
						rightActionListener.onAction();
					}
					break;
				default:
					break;
				}
				Log.d(TAG, " v id = "+Integer.toHexString(v.getId())
				        +" keycode =  " + getKeycodeString(keyCode) 
						+" forceUp ="+forceUp+" forceDown = "+forceDown+" forceLeft ="+forceLeft+" forceRight="+forceRight
				        + " takeoverFocus = (" + ret+")");
				return ret;
			}
		});
		return false;
	}
	
	public static class MainMenuFocusActionListener implements FocusActionListener{

		@Override
		public void onAction() {
			EventBus.getDefault().post(new Event.MainMenuFocusEvent());
		}
		
	}
	
	public static String getKeycodeString(int code){
		String ret = code+"";
		if(code == KeyEvent.KEYCODE_DPAD_UP){
			ret =" UP ";
		}else if(code == KeyEvent.KEYCODE_DPAD_DOWN){
			ret =" DOWN ";
		}else if(code == KeyEvent.KEYCODE_DPAD_LEFT){
			ret =" LEFT ";
		}else if(code == KeyEvent.KEYCODE_DPAD_RIGHT){
			ret =" RIGHT ";
		}else if(code == KeyEvent.KEYCODE_DPAD_CENTER){
			ret =" OK ";
		}else if(code == KeyEvent.KEYCODE_BACK){
			ret =" BACK ";
		}else if(code == KeyEvent.KEYCODE_MENU){
			ret =" MENU ";
		}
		return ret;
	}

	private static void requestFocus(View v) {
		if (v == null ) {
			return;
		}
		if(v.getVisibility() == View.INVISIBLE){
			Log.w(TAG,"nextView requestFocus v = " +Integer.toHexString(v.getId())+ "  INVISIBLE ");
			return;
		}
		
		Log.d(TAG,"nextView requestFocus v = " +Integer.toHexString(v.getId()) );
		if (v instanceof GridView) {
			GridView gv = (GridView) v;
			if (gv.getCount() > 0) {
				gv.setSelection(0);
				gv.requestFocus();
			}
		} else if (v instanceof ListView) {
			ListView lv = (ListView) v;
			if (lv.getCount() > 0) {
				//lv.setSelection(0);
				lv.requestFocus();
			}
		} else if (v instanceof HListView) {
			HListView hlv = (HListView) v;
			//hlv.setSelection(0);
			if(hlv.getCount() > 0){
				hlv.requestFocus();
			}
			
		} else {
			v.requestFocus();
		}
	}

	public static boolean isFirstLine(ListView v) {
		if (v == null || v.getCount() <= 0) {
			return false;
		}
		return v.getSelectedItemPosition() == 0;
	}

	public static boolean isLastLine(ListView v) {
		if (v == null || v.getCount() <= 0) {
			return false;
		}

		return (v.getSelectedItemPosition()+1) == v.getCount();
	}

	public static boolean isFirstLine(GridView v) {
		if (v == null || v.getCount() <= 0 || v.getNumColumns() == 0) {
			return false;
		}
		return v.getSelectedItemPosition() < v.getNumColumns();
	}

	public static boolean isLastLine(GridView v) {
		if (v == null || v.getCount() <= 0 || v.getNumColumns() == 0) {
			return false;
		}
		int mod = (v.getSelectedItemPosition() + 1) % v.getNumColumns();
		int selectedLine = (v.getSelectedItemPosition() + 1) / v.getNumColumns() + (mod > 0 ? 1 : 0);

		int mod1 = v.getCount() % v.getNumColumns();
		int allLine = v.getCount() / v.getNumColumns() + (mod1 > 0 ? 1 : 0);

		if (selectedLine >= allLine) {
			return true;
		}
		return false;
	}

	public static boolean isLastRow(GridView v) {
		if (v == null || v.getCount() <= 0 || v.getNumColumns() == 0) {
			return false;
		}
		int mod = (v.getSelectedItemPosition() + 1) % v.getNumColumns();

		if (mod == 0 || v.getSelectedItemPosition() == v.getCount()-1) {
			return true;
		}
		return false;
	}

	public static boolean isFirstRow(GridView v) {
		if (v == null || v.getCount() <= 0 || v.getNumColumns() == 0) {
			return false;
		}
		int mod = v.getSelectedItemPosition() % v.getNumColumns();

		if (mod == 0) {
			return true;
		}
		return false;
	}
}
