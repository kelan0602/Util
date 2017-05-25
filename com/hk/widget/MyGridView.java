package com.hk.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView{
	
	private int position=0;

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
	}
	
	@Override
	protected void setChildrenDrawingOrderEnabled(boolean enabled) {
		super.setChildrenDrawingOrderEnabled(enabled);
	}
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		position = getSelectedItemPosition() - getFirstVisiblePosition();
		if(position < 0){
			return i;
		}else{
			if(i == childCount - 1){//这是最后一个需要刷新的item
				if(position>i){
					position=i;
				}
	            return position;
	        }
			if(i == position){//这是原本要在最后一个刷新的item
				return childCount - 1;
			}
		}
		return i;
	}
}
