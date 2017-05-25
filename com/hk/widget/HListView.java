package com.hk.view.widget;

import java.util.Stack;

import com.tvb.hk.anywhere.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.OverScroller;
import android.widget.ScrollView;

public class HListView extends AdapterView<ListAdapter> {

    private static final String TAG = "HListView";

    private final GestureDetector mGestureDetector;

    private ListAdapter mAdapter;

    private OverScroller mScroller;

    // 实际布局到当前视图的第一个 item 位置
    private int mFirstPosition;
    // 当前选中位置
    private int mSelectedPosition;
    
    // 回收站
    private static final RecycleBin mRecycler = new RecycleBin();

    // 子视图间距
    private int mSpacing;

    // 相对布局位置
    private int mGravity;

    // 靠上布局
    private static final int GRAVITY_TOP = 0;

    // 居中布局
    private static final int GRAVITY_CENTER = 1;

    // 靠底布局
    private static final int GRAVITY_BOTTOM = 2;

    // item 宽度
    private int mItemWidth;

    // item 高度
    private int mItemHeight;

    // 左侧溢出范围
    private int mLeftOffSet;

    // 右侧溢出范围
    private int mRightOffSet;

    // 子视图上边界
    private int mLayoutTop;

    // Adapter.getCount() 数量
    private int mItemCount;

    // adapter 监听
    private InnerDataSetObserver mDataSetObserver = new InnerDataSetObserver();

    // 控件总长度 (包括不可见的部分)
    private int mFillInWidth = 0;

    // 最后滚动时间戳
    private long mLastScrollTimeStamp;

    // 最后长按时间戳
    private long mLastLongPress;

    // item 缩放因子
    private float mItemScaleFactor;

    // item 缩放时常
    private int mItemScaleDuration;


    public HListView(Context context) {
        this(context, null);
    }

    public HListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.HListViewStyle);
    }

    public HListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HListView, defStyleAttr, 0);
        mItemWidth = ta.getDimensionPixelSize(R.styleable.HListView_item_width, 200);
        mItemHeight = ta.getDimensionPixelSize(R.styleable.HListView_item_height, 200);
        mItemScaleFactor = ta.getFloat(R.styleable.HListView_item_scale_factor, 1.1f);
        mItemScaleDuration = ta.getInteger(R.styleable.HListView_item_scale_duration, 300);
        mSpacing = ta.getDimensionPixelOffset(R.styleable.HListView_spacing, 20);
        mLeftOffSet = ta.getDimensionPixelOffset(R.styleable.HListView_left_offset, 20);
        mRightOffSet = ta.getDimensionPixelOffset(R.styleable.HListView_right_offset, 20);
        mGravity = 1;//ta.getInt(R.styleable.HListView_gravity, 1)
       // int color = ta.getColor(R.styleable.HListView_bg_color, 0x80ffffff);
        ta.recycle();

        //setBackgroundColor(color);
        setPadding(0, 0, 0, 0);

        mScroller = new OverScroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), getOnGestureListener());

        setFocusable(true);

    }
    
    //控件可见部分的总长度 getWidth()方法获取的不对，获取的是父类的宽度
    private int getWidthInner(){
    	MarginLayoutParams params = (MarginLayoutParams)getLayoutParams();
    	return params.width;
    }
    //控件最多可以显示多少个item
    private int getChildCountInner(){
    	int ret = getChildCount();//(getWidthInner()-mLeftOffSet-mRightOffSet)/(mItemWidth+mSpacing);//
    	Log.d(TAG,"getChildCountInner = "+ret);
    	return ret;
    }
 
    
    public void setAtrr(int itemW,int itemH,int space,int leftOffset,int rightOffset,float scale,int scaleDur){
    	
    	mItemWidth =itemW;
        mItemHeight = itemH;
        mItemScaleFactor = scale;
        mItemScaleDuration = scaleDur;
        mSpacing = space;
        mLeftOffSet = leftOffset;
        mRightOffSet = rightOffset;
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && !mAdapter.isEmpty()) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;

        mRecycler.clear();
        if (mAdapter == null || mAdapter.isEmpty()) {
            setFocusable(false);
            mDataSetObserver.onEmpty();
        } else {
            setFocusable(true);
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mFirstPosition = 0;
            mDataSetObserver.onChanged();
            setSelection(mFirstPosition);
        }
    }
    
    public int getCount(){
    	if(mAdapter != null){
    		return mAdapter.getCount();
    	}
    	return 0;
    }

    private class InnerDataSetObserver extends DataSetObserver {

        boolean dataChanged;

        @Override
        public void onChanged() {
        	mItemCount = getAdapter().getCount();
        	mFirstPosition = 0;
            mFillInWidth = mLeftOffSet + mRightOffSet + (mItemCount - 1) * mSpacing + mItemCount * mItemWidth;
            dataChanged = true;
            Log.d(TAG, "onChanged mItemCount= "+mItemCount +" mItemWidth = "+mItemWidth+" mFillInWidth = "+mFillInWidth);
            removeAllViewsInLayout();
            scrollTo(0, 0);
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            // nothing
        }

        public boolean isDataChanged() {
            boolean changed = dataChanged;
            dataChanged = false;
            Log.d(TAG, "isDataChanged changed = "+changed);
            return changed;
        }

        public void onEmpty() {
        	
        	Log.d(TAG, "onEmpty "); 
            mItemCount = 0;
            mFillInWidth = 0;
            dataChanged = true;
            removeAllViewsInLayout();
            scrollTo(0, 0);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    	Log.d(TAG, "onFocusChanged mSelectedPosition= "+mSelectedPosition);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setSelection(mSelectedPosition);
    }

    @Override
    public View getSelectedView() {

    	Log.d(TAG, "getSelectedView getChildAt "+(mSelectedPosition - mFirstPosition)+" mItemCount = "+mItemCount+
    			" mSelectedPosition = "+mSelectedPosition+" mFirstPosition = "+mFirstPosition);
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - mFirstPosition);
        } else {
        	Log.d(TAG, "getSelectedView null ");
            return null;
        }
    }

    @Override
    public int getSelectedItemPosition() {
        return mSelectedPosition;
    }

//   private int getChildCountInner1(){
//    	
//    	int ret = (getWidthInner()-mLeftOffSet-mRightOffSet)/(mItemWidth+mSpacing);
//    	Log.d(TAG,"getChildCountInner1 = "+ret);
//    	return ret;
//    }
//   
//    private boolean needScrool(int pos){
//    	if(mItemCount <= getChildCountInner()){
//    		Log.d(TAG,"needScrool1 short === false");
//    		return false;
//    	}
//    	if(pos < mFirstPosition ){
//    		Log.d(TAG,"needScrool2 <---- === true");
//    		return true;
//    	}else if(getChildCountInner1() <= pos-mFirstPosition){
//    		Log.d(TAG,"needScrool3 ----> === true");
//    		return true;
//    	}
//    	Log.d(TAG,"needScrool4 false mFirstPosition = "+mFirstPosition+" pos = "+pos +" mItemCount = "+mItemCount);
//    	return false;
//    }
    
    @Override
    public void setSelection(int position) {
    	
    	Log.d(TAG, "===================================================");
    	Log.d(TAG, "setSelection IN position = "+position +" mFirstPosition = "+mFirstPosition+" line = "+getTag()+" mItemCount = "+mItemCount);

    	if(position < 0){
    		position = 0;
    	}
    	if(position >= mItemCount){
    		position = mItemCount-1;
    	}
        // 1.unFocus pre view
        onItemFocusChange(false);
        
//        if(mSelectedPosition != position && needScrool(position)){
//        	smoothScrollBy((position-mSelectedPosition)*(mItemWidth+mSpacing),0);
//        }        
        // 3.reSet selectPosition
        mSelectedPosition = position;
        
        if(mFirstPosition > mSelectedPosition){
        	Log.e(TAG,"ERROR!!!! setSelection mFirstPosition > mSelectedPosition "+" mFirstPosition = "+mFirstPosition+
        			" mSelectedPosition = "+mSelectedPosition);
        	mFirstPosition = mSelectedPosition-1;
        }
        
        // 2.scroll to x  当前要移动到哪个位置,获取到该位置的x坐标
        int areaStartX = getScrollXByPosition(position);

        //layoutChildren(areaStartX, areaStartX + getWidthInner());
        
        smoothScrollTo(areaStartX, 0);
       
        
        // 4.focus cur view
        onItemFocusChange(hasFocus());
        Log.d(TAG, "setSelection OUT position = "+position+" hasFocus = "+hasFocus() + " line = "+getTag());
        Log.d(TAG, "===================================================");
    }
    
    public void reset(){
    	
    	onItemFocusChange(false);
    	mSelectedPosition = 0;
    	mFirstPosition = 0;
    	setFocusable(true);
    	if(!mAdapter.isEmpty()){
    		mDataSetObserver.onChanged();
            setSelection(0);
    	}
    }

 // 锚点位置
    private int getScrollXByPosition(int position) {
    	
    	int left = mLeftOffSet + position * (mSpacing + mItemWidth);
        int right = left + mItemWidth;
        int dstRight = right + mRightOffSet;
        int dstLeft = left - mLeftOffSet;
        int x = getScrollX();//child scrolled pos
        int ret = 0;
        if (right > x + getWidthInner() - mRightOffSet) {
        	ret =  dstRight - getWidthInner();
        } else if (left < x + mLeftOffSet) {
        	ret =  dstLeft;
        } else {
        	ret= getScrollX();
        }
        Log.d(TAG,"getScrollXByPosition ret = "+ret +" position = "+position +" getScrollX() = "+getScrollX());
        return ret;
    }
    
    // 锚点位置
    private int getScrollXByPosition1(int position) {
    	
        int left = mLeftOffSet + position * (mSpacing + mItemWidth);
        int right = left + mItemWidth;
        int dstRight = right + mRightOffSet;
        int dstLeft = left - mLeftOffSet;
        int x = getScrollX();
        int ret = 0;
        if (right > x + getWidthInner() - mRightOffSet) {
        	ret =  dstRight - getWidthInner();
        } else if (left < x + mLeftOffSet) {
        	ret =  dstLeft;
        } else {
        	ret= getScrollX();
        }
        Log.d(TAG,"getScrollXByPosition ret = "+ret +" position = "+position +" getScrollX() = "+getScrollX());
        return ret;
    }

    private int getCurrLine(){
    	if(getTag() != null){
    		return (Integer)getTag();
    	}else{
    		return 0;
    	}
    	
    }
    
    private void itemClearFocus(int pos){
    	View view = (View)mAdapter.getItem(pos);
    	view.setSelected(false);
    	zoomOut(view);
    }
    
    private void itemGetFocus(int pos){
    	View view = (View)mAdapter.getItem(pos);
    	view.setSelected(true);
    	zoomIn(view);
    	OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
        if (onItemSelectedListener != null ){
        	Log.d(TAG, " onItemSelected pos = "+pos);
        	 onItemSelectedListener.onItemSelected(this, view, pos, 0);
        }
    }
    
    public void onItemFocusChange(boolean focused) {

        // anim focused itemView
        View selected = getSelectedView();
        if (null != selected) {
            selected.setSelected(focused);
            if (focused) {
            	Log.d(TAG, "onItemFocusChange zoom BIG line = "+getCurrLine()+" offsetPos = "+(mSelectedPosition - mFirstPosition));
                zoomIn(selected);
            } else {
            	Log.d(TAG, "onItemFocusChange zoom SMALL line = "+getCurrLine()+" offsetPos = "+(mSelectedPosition - mFirstPosition));
                zoomOut(selected);
            }
        }

        // call back
        OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
        if (onItemSelectedListener != null && focused) {
            if (selected != null) {
            	 Log.d(TAG, "onItemFocusChange--->onItemSelected pos = "+mSelectedPosition);
                onItemSelectedListener.onItemSelected(this, selected, mSelectedPosition, 0);
            } else {
                onItemSelectedListener.onNothingSelected(this);
            }
        }
    }

    // 放大
    private void zoomIn(View view) {
        if (view != null) {
        	Log.d(TAG,"zoom BIG");
            view.animate().scaleX(mItemScaleFactor).scaleY(mItemScaleFactor).setDuration(mItemScaleDuration).start();
        }
    }

    // 缩小
    private void zoomOut(View view) {
        if (view != null) {
        	Log.d(TAG,"zoom SMALL");
            view.animate().scaleX(1).scaleY(1).setDuration(mItemScaleDuration).start();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
    	Log.d(TAG, "scrollTo x= "+x +" mFillInWidth = "+mFillInWidth +" getWidthInner = "+getWidthInner()
    	+" mScroller.getStartX() = "+mScroller.getStartX());
        if (x < 0) {
            x = 0;
        }else if(getWidthInner() > mFillInWidth){
        	x = 0;
        }else if (x > mFillInWidth - getWidthInner()) {
            x = mFillInWidth - getWidthInner();
        }
        layoutChildren(x, x + getWidthInner());
        super.scrollTo(x, y);
    }

    private final void smoothScrollTo(int toX, int toY) {
        smoothScrollBy(toX - getScrollX(), 0);
    }

    /**
     * 参照 Like {@link ScrollView#scrollBy}
     * Scroll smoothly instead of immediately.
     */
    private final void smoothScrollBy(int dx, int dy) {
    	
    	Log.d(TAG, "===================================================");
    	Log.d(TAG, "smoothScrollBy IN dx = "+dx +" startX = "+getScrollX());
        if (getChildCountInner() == 0) {
            return;
        }
        final int scrollX = getScrollX();
        int minX = 0;
        int maxX = mFillInWidth - getWidthInner() + minX;
        if (scrollX + dx < minX) {
            dx = minX - scrollX;
        } else if (scrollX + dx > maxX) {
            dx = maxX - scrollX;
        }
      
        
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScrollTimeStamp;
        if (duration > 250) {
        	Log.d(TAG, "smoothScrollBy Animation timeout startScroll scrollX = "+scrollX);
        	if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mScroller.startScroll(scrollX, getScrollY(), dx, 0, 250);
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            Log.d(TAG, "smoothScrollBy scrollBy dx = "+dx);
            scrollBy(dx, dy);
        }
        
        mLastScrollTimeStamp = AnimationUtils.currentAnimationTimeMillis();
        Log.d(TAG, "smoothScrollBy OUT dx = "+dx);
        Log.d(TAG, "===================================================");
    }
    
   
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw mScroller finished = "+!mScroller.computeScrollOffset()); 
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            Log.d(TAG, "computeScroll finished = "+!mScroller.computeScrollOffset() +" getCurrX = "+x); 
            scrollTo(x, y);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        switch (mGravity) {
            case GRAVITY_TOP:
                mLayoutTop = 0;
                break;
            case GRAVITY_CENTER:
                mLayoutTop = (getMeasuredHeight() - mItemHeight) / 2;
                break;
            case GRAVITY_BOTTOM:
                mLayoutTop = getMeasuredHeight() - mItemHeight;
                break;
        }
        Log.d(TAG, "onMeasure mLayoutTop = "+mLayoutTop +" mGravity = "+mGravity); 
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	 Log.d(TAG, "onLayout changed = "+changed+" mDataSetObserver.isDataChanged() = "+mDataSetObserver.isDataChanged()
    	 +"  "+left+" ----> "+right+" w = "+(right-left));
        if (changed || mDataSetObserver.isDataChanged()) {
            super.onLayout(changed, left, top, right, bottom);
            removeAllViewsInLayout();
            layoutChildren(0, getWidthInner());
            setSelection(mSelectedPosition);
        }
    }

    private void layoutChildren1(int areaStartX, int areaEndX) {
    	
        // 1. ensure start & end position
        final int startPosition = (areaStartX - mLeftOffSet) / (mItemWidth + mSpacing);
        final int endPosition = (areaEndX - mLeftOffSet) / (mItemWidth + mSpacing) + 1;
        final int count = endPosition - startPosition;
//        Log.d(TAG, "layoutChildren start mFirstDrawPosition = "+mFirstPosition +" layoutWith = "+(areaEndX - areaStartX)
//        		+" draw item count = "+count +" selctPos = "+mSelectedPosition);
        
        final int oldCount = getChildCountInner();
        if (startPosition == mFirstPosition && oldCount == count) {
        	//Log.d(TAG, "layoutChildren no need draw..");
            return;
        }

        final int start = Math.max(Math.min(mFirstPosition, startPosition), 0);

        final int end = Math.min(Math.max(mFirstPosition + oldCount, startPosition + count), mItemCount);
 //       Log.d(TAG, "layoutChildren startIndex ="+start+" endIndex = "+end);
       
        // layout  views
        for (int position = start; position < end && position < mItemCount; position++) {
        	View child = (View)mAdapter.getItem(position);
            boolean rightFlow = position >= mFirstPosition;
            layoutChild(child, position, rightFlow);
        }
        mFirstPosition = startPosition;
        Log.d(TAG, "after layoutChildren  mFirstPosition = "+mFirstPosition);
    }

    private void layoutChildren(int areaStartX, int areaEndX) {
    	
        // 1. ensure start & end position
    	Log.d(TAG, "layoutChildren areaStartX = [ "+areaStartX+" ----- "+areaEndX +" ] mFirstPosition = "+mFirstPosition);    	
        int startPosition = (areaStartX - mLeftOffSet) / (mItemWidth + mSpacing);
        final int endPosition = (areaEndX - mLeftOffSet) / (mItemWidth + mSpacing) + 1;
        final int count = endPosition - startPosition;
        if(startPosition < 0){
        	startPosition = 0;
        }
//        Log.d(TAG, "layoutChildren start mFirstDrawPosition = "+mFirstPosition +" layoutWith = "+(areaEndX - areaStartX)
//        		+" draw item count = "+count +" selctPos = "+mSelectedPosition);
         final int oldCount = getChildCountInner();
        if (startPosition == mFirstPosition && oldCount == count) {
        	//Log.d(TAG, "layoutChildren no need draw..");
            return;
        }

        final int start = Math.max(Math.min(mFirstPosition, startPosition), 0);

        final int end = Math.min(Math.max(mFirstPosition + oldCount, startPosition + count), mItemCount);
  //      Log.d(TAG, "layoutChildren startIndex ="+start+" endIndex = "+end);
        // 2.ensure scrap views
        Stack<View> removed = new Stack<View>();

        for (int i = start; i < end && i < mItemCount; i++) {
            boolean isNew = i >= startPosition && i < startPosition + count;
            boolean isOld = i >= mFirstPosition && i < mFirstPosition + oldCount;
            if (isOld && !isNew) {
                View child = getChildAt(i - mFirstPosition);
                if (child != null) {
                    removed.push(child);
                }
            }
        }

        // 3.remove views into scrap recycler
        while (!removed.empty()) {
            View child = removed.pop();
            if (child != null) {
                child.setSelected(false);
                child.clearAnimation();
                removeViewInLayout(child);
                mRecycler.addScrapView(child);
            }
        }

        // 4.layout scrap views
        for (int position = start; position < end && position < mItemCount; position++) {
            boolean isNew = position >= startPosition && position < startPosition + count;
            boolean isOld = position >= mFirstPosition && position < mFirstPosition + oldCount;
            if (!isOld && isNew) {
                View child = mRecycler.getScrapView();
                child = mAdapter.getView(position, child, this);
                boolean rightFlow = position >= mFirstPosition;
                layoutChild(child, position, rightFlow);
            }
        }
        mFirstPosition = startPosition;
        Log.d(TAG, "after layoutChildren  mFirstPosition = "+mFirstPosition);
    }

    private void layoutChild(View view, int position, boolean rightFlow) {
        // 1. measure
        measureItem(view);

        // 2. add in layout
        int relativeIndex = rightFlow ? -1 : 0;
        addViewInLayout(view, relativeIndex, null, true);

        // 3. layout
        int left = mLeftOffSet + (position) * (mItemWidth + mSpacing);
        int top = mLayoutTop;
        view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
    }

    private void measureItem(View view) {
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(mItemWidth, mItemHeight);
        } else {
            params.width = mItemWidth;
            params.height = mItemHeight;
        }
        view.setLayoutParams(params);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	Log.d(TAG,"onKeyDown start  keyCode = "+getKeycodeString(keyCode));
        boolean handled = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                handled = moveLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                handled = moveRight();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_ENTER:
                event.startTracking();
                handled = true;
                break;
        }
        Log.d(TAG,"onKeyDown  end = "+getKeycodeString(keyCode) +" handled = "+handled);
        return handled || super.onKeyDown(keyCode, event);
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
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean handled = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_ENTER:
                long duration = event.getEventTime() - event.getDownTime();
                if (event.isTracking() && event.getDownTime() > mLastLongPress && duration < ViewConfiguration.getLongPressTimeout()) {
                    performItemClick(getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
                }
                handled = true;
                break;
        }

        return handled || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean handled = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_ENTER:
                mLastLongPress = event.getEventTime();
                performItemLongClick(getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
                handled = true;
                break;
        }

        return handled || super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (view != null) {
        	Log.d(TAG,"performItemClick position = "+position);
            return super.performItemClick(view, position, id);
        }
        return false;
    }

    public boolean performItemLongClick(View view, int position, long id) {
        OnItemLongClickListener l = getOnItemLongClickListener();
        if (l != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                l.onItemLongClick(this, view, position, id);
            }
            return true;
        }

        return false;
    }

    // 焦点右移
    private boolean moveRight() {
        int selection = mSelectedPosition;
        selection++;
        if (selection >= mItemCount) {
            return false;
        }
        setSelection(selection);
        return true;
    }

    // 焦点左移
    private boolean moveLeft() {
        int selection = mSelectedPosition;
        selection--;
        if (selection < 0) {
            return false;
        }
        setSelection(selection);
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // 手势监听
    private GestureDetector.OnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {

            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                int position = getPositionByXY((int) e.getX(), (int) e.getY());
                if (position >= 0 && position < mItemCount) {
                    performItemClick(getChildAt(position - mFirstPosition), position, 0);
                }
                return true;
            }

            public void onLongPress(MotionEvent e) {
                int position = getPositionByXY((int) e.getX(), (int) e.getY());
                if (position >= 0 && position < mItemCount) {
                    performItemLongClick(getChildAt(position - mFirstPosition), position, 0);
                }
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int) distanceX, 0);
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                fling((int) -velocityX);
                return true;
            }

            private int getPositionByXY(int x, int y) {
                int position = -1;
                for (int i = 0; i < getChildCountInner(); i++) {
                    View view = getChildAt(i);
                    Region region = new Region(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    if (region.contains(x, y)) {
                        return i + mFirstPosition;
                    }
                }
                return position;
            }
        };
    }

    // 甩动手势
    private void fling(int velocityX) {
        if (getChildCountInner() > 0) {
            int minX = 0;
            int maxX = mFillInWidth - getWidthInner() + minX;
            int overX = getWidthInner() / 4;
            mScroller.fling(getScrollX(), getScrollY(), velocityX, 0, minX, maxX, 0, 0, overX, 0);
            invalidate();
        }
    }

    // 回收站
    private static class RecycleBin {

        private final Stack<View> mScrapViews;

        public RecycleBin() {
            mScrapViews = new Stack<View>();
        }

        public View getScrapView() {
            if (!mScrapViews.empty()) {
                return mScrapViews.pop();
            }
            return null;
        }

        public void addScrapView(View v) {
            mScrapViews.push(v);
        }

        public void clear() {
            mScrapViews.clear();
        }
    }
}
