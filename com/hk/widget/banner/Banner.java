package com.hk.view.widget.banner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.util.ImageUtil;
import com.tvb.hk.anywhere.util.UIHandler;
import com.tvb.hk.anywhere.util.Util;
import com.tvb.hk.anywhere.view.ViewAdjust;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Banner extends FrameLayout implements OnPageChangeListener{
	
	public String TAG = "Banner";
	private int mIndicatorMargin = 5;
	private int mIndicatorWidth = 20;
	private int mIndicatorHeight = 20;
	private int mDefaultImageResId = R.drawable.blank;
	private int mDefaultBackgroundResId = -1;
	private int bannerStyle = BannerConfig.CIRCLE_INDICATOR;
	private int delayTime = BannerConfig.DELAYTIME;
	private int scrollTime = BannerConfig.DURATION;
	private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
	private boolean isScroll = BannerConfig.IS_SCROLL;
	private int mIndicatorSelectedResId = R.drawable.white_radius;
	private int mIndicatorUnselectedResId = R.drawable.gray_radius;
	private int titleHeight = BannerConfig.TITLE_HEIGHT;
	private int titleBackground = BannerConfig.TITLE_BACKGROUND;
	private int titleTextColor = BannerConfig.TITLE_TEXT_COLOR;
	private int titleTextSize = BannerConfig.TITLE_TEXT_SIZE;
	private int count = 0;
	private int currentItem = 1;
	private int gravity = Gravity.CENTER;
	private int lastPosition = 0;
	private int scaleType = 6;
	private List<String> titles;
	private ArrayList<String> imageUrls;
	private List<ImageView> imageViews;
	private List<ImageView> indicatorImages;
	private Context context;
	private BannerViewPager viewPager;
	private TextView bannerTitle, numIndicatorInside, numIndicator;
	private LinearLayout indicator, indicatorInside, titleView;
	private BannerPagerAdapter adapter;
	private OnPageChangeListener mOnPageChangeListener;
	private BannerScroller mScroller;
	private OnBannerListener listener;
	private ImageView mFocusImage;
	

	public Banner(Context context) {
		this(context, null);
	}

	public Banner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Banner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		titles = new ArrayList<>();
		imageUrls = new ArrayList<>();
		imageViews = new ArrayList<>();
		indicatorImages = new ArrayList<>();
		initView(context, attrs);
	}

	private void initView(Context context, AttributeSet attrs) {
		imageViews.clear();

		View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
		mFocusImage = (ImageView) view.findViewById(R.id.focus);
		viewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
		titleView = (LinearLayout) view.findViewById(R.id.titleView);
		indicator = (LinearLayout) view.findViewById(R.id.circleIndicator);
		indicatorInside = (LinearLayout) view.findViewById(R.id.indicatorInside);
		bannerTitle = (TextView) view.findViewById(R.id.bannerTitle);
		numIndicator = (TextView) view.findViewById(R.id.numIndicator);
		numIndicatorInside = (TextView) view.findViewById(R.id.numIndicatorInside);
		setViewPagerScrollDuration(scrollTime);
		setBannerAnimation(Transformer.noChange);
		setOffscreenPageLimit(5);
		
		mFocusImage.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					return false;
				}
				if (keycode == KeyEvent.KEYCODE_DPAD_LEFT) {
					setPreItemSelected();
					return true;
				} else if (keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					setNextItemSelected();
					return true;
				}
				return false;
			}
		});
		mFocusImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAutoPlay();
				if(listener != null){
					listener.OnBannerClick(toRealPosition(viewPager.getCurrentItem()));
				}
			}
		});

	}	
	
	public View getFocusView(){
		return mFocusImage;
	}

	public boolean requestBannerFocus() {
		mFocusImage.requestFocus();
		return true;
	}

	public void clearBannerFocus() {
		mFocusImage.clearFocus();
	}
	
	public void setViewPagerScrollDuration(int dur) {
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new BannerScroller(viewPager.getContext());
			mScroller.setDuration(dur);			
			mField.set(viewPager, mScroller);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public Banner isAutoPlay(boolean isAutoPlay) {
		this.isAutoPlay = isAutoPlay;
		return this;
	}

	public Banner setDelayTime(int delayTime) {
		this.delayTime = delayTime;
		return this;
	}

	public Banner setIndicatorGravity(int type) {
		switch (type) {
		case BannerConfig.LEFT:
			this.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			break;
		case BannerConfig.CENTER:
			this.gravity = Gravity.CENTER;
			break;
		case BannerConfig.RIGHT:
			this.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
			break;
		}
		return this;
	}

	public Banner setBannerAnimation(Class<? extends PageTransformer> transformer) {
		try {
			setPageTransformer(true, transformer.newInstance());
		} catch (Exception e) {
			Log.e(TAG, "Please set the PageTransformer class");
		}
		return this;
	}

	/**
	 * Set the number of pages that should be retained to either side of the
	 * current page in the view hierarchy in an idle state. Pages beyond this
	 * limit will be recreated from the adapter when needed.
	 *
	 * @param limit
	 *            How many pages will be kept offscreen in an idle state.
	 * @return Banner
	 */
	public Banner setOffscreenPageLimit(int limit) {
		if (viewPager != null) {
			viewPager.setOffscreenPageLimit(limit);
		}
		return this;
	}

	/**
	 * Set a {@link PageTransformer} that will be called for each attached page
	 * whenever the scroll position is changed. This allows the application to
	 * apply custom property transformations to each page, overriding the
	 * default sliding look and feel.
	 *
	 * @param reverseDrawingOrder
	 *            true if the supplied PageTransformer requires page views to be
	 *            drawn from last to first instead of first to last.
	 * @param transformer
	 *            PageTransformer that will modify each page's animation
	 *            properties
	 * @return Banner
	 */
	public Banner setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
		viewPager.setPageTransformer(reverseDrawingOrder, transformer);
		return this;
	}

	public Banner setBannerStyle(int bannerStyle) {
		this.bannerStyle = bannerStyle;
		return this;
	}

	public Banner setViewPagerIsScroll(boolean isScroll) {
		this.isScroll = isScroll;
		return this;
	}
	
	public void setImageScaleType(int type){
		this.scaleType = type;
	}

	public Banner setImages(ArrayList<String> imageUrls) {
		if(imageUrls != null){
			this.imageUrls = imageUrls;
			this.count = imageUrls.size();
		}else{
			this.imageUrls = null;
			this.count = 0;
		}
		return this;
	}
	
	public Banner setImagesAndTitles(ArrayList<String> imageUrls,ArrayList<String> titles) {
		
		if(imageUrls != null && titles != null && imageUrls.size() == titles.size()){
			this.imageUrls = imageUrls;
			this.count = imageUrls.size();
			this.titles = titles;
		}
		return this;
	}

	public void update(List<String> imageUrls, List<String> titles,boolean start) {
		
		if(imageUrls != null && titles != null && imageUrls.size() == titles.size()){
			stop();
			this.imageUrls.clear();
			this.titles.clear();
			this.imageUrls.addAll(imageUrls);
			this.titles.addAll(titles);
			this.count = this.imageUrls.size();
			start(start);
		}
	}

	public void update(List<String> imageUrls,boolean isStart) {
		if(imageUrls != null){
			stop();
			this.imageUrls.clear();
			this.imageUrls.addAll(imageUrls);
			this.count = this.imageUrls.size();
			start(isStart);
		}
	}

	public void updateBannerStyle(int bannerStyle,boolean start) {
		stop();
		indicator.setVisibility(GONE);
		numIndicator.setVisibility(GONE);
		numIndicatorInside.setVisibility(GONE);
		indicatorInside.setVisibility(GONE);
		bannerTitle.setVisibility(View.GONE);
		titleView.setVisibility(View.GONE);
		this.bannerStyle = bannerStyle;
		start(start);
	}

	public Banner start(boolean start) {
		setBannerStyleUI();
		setImageList(imageUrls);
		setData();
		if (start)
		   startAutoPlay();
		return this;
	}

	private void setTitleStyleUI() {
		
		if (titles.size() != imageUrls.size()) {
			//throw new RuntimeException("[Banner] --> The number of titles and images is different");
			return;
		}
		if (titleBackground != -1) {
			titleView.setBackgroundColor(titleBackground);
		}
		if (titleHeight != -1) {
			titleView
					.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
		}
		if (titleTextColor != -1) {
			bannerTitle.setTextColor(titleTextColor);
		}
		if (titleTextSize != -1) {
			bannerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
		}
		if (titles != null && titles.size() > 0) {
			bannerTitle.setText(titles.get(0));
			bannerTitle.setVisibility(View.VISIBLE);
			titleView.setVisibility(View.VISIBLE);
		}
	}

	private void setBannerStyleUI() {
		int visibility;
		if (count > 1)
			visibility = View.VISIBLE;
		else
			visibility = View.GONE;
		switch (bannerStyle) {
		case BannerConfig.CIRCLE_INDICATOR:
			indicator.setVisibility(visibility);
			break;
		case BannerConfig.NUM_INDICATOR:
			numIndicator.setVisibility(visibility);
			break;
		case BannerConfig.NUM_INDICATOR_TITLE:
			numIndicatorInside.setVisibility(visibility);
			setTitleStyleUI();
			break;
		case BannerConfig.CIRCLE_INDICATOR_TITLE:
			indicator.setVisibility(visibility);
			setTitleStyleUI();
			break;
		case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
			indicatorInside.setVisibility(visibility);
			setTitleStyleUI();
			break;
		}
	}

	private void initImages() {
		imageViews.clear();
		if (bannerStyle == BannerConfig.CIRCLE_INDICATOR || bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE
				|| bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
			createIndicator();
		} else if (bannerStyle == BannerConfig.NUM_INDICATOR_TITLE) {
			numIndicatorInside.setText("1/" + count);
		} else if (bannerStyle == BannerConfig.NUM_INDICATOR) {
			numIndicator.setText("1/" + count);
		}
	}

	public Banner setDefaultImage(int defaultBackgroundImageResId){
		stop();
		this.count = 0;
		imageUrls.clear();
		mDefaultBackgroundResId = defaultBackgroundImageResId;
		initImages();
		ImageView imageView = new ImageView(context);
		imageView.setImageResource(defaultBackgroundImageResId);		
		setScaleType(imageView);
		imageViews.add(imageView);
		return this;
	}
	
	private void setImageList(List<String> imagesUrl) {
		if (imagesUrl == null || imagesUrl.size() <= 0) {
			Log.e(TAG, "Please set the images data.");
			return;
		}
		
		initImages();
		
		if(count <= 0){
			return;
		}
		
		if(count == 1){
			ImageView imageView = new ImageView(context);
			setScaleType(imageView);
			imageViews.add(imageView);
			ImageUtil.getInstance().displayImage(context, imagesUrl.get(0), imageView, mDefaultImageResId);
			return;
		}
		/**
		 * 多加2张图片，避免系统控件从最后一个切换到第一个的时候的快速切换造成视觉效果异常(给一个假的item做动画效果),
		 * 在最前面加最后一个item,在最后面加最前面一个item
		 * 
		 * **/
		for (int i = 0; i <= count + 1; i++) {
			ImageView imageView = new ImageView(context);
			setScaleType(imageView);
			String url = null;
			if (i == 0) {
				url = imagesUrl.get(count - 1);
			} else if (i == count + 1) {
				url = imagesUrl.get(0);
			} else {
				url = imagesUrl.get(i - 1);
			}
			imageViews.add(imageView);
			ImageUtil.getInstance().displayImage(context, url, imageView, mDefaultImageResId);
		}
	}
	
	private void displayImage(int pos){
		
		//Log.d(TAG,"displayImage pos = "+pos +" imageViews.size() = "+imageViews.size() +" mDefaultBackgroundResId = "+mDefaultBackgroundResId);
		if(Util.isEmpty(imageUrls)){
			if(mDefaultBackgroundResId != -1 || Util.checkBoundIsOk(imageViews, pos)){
				Log.d(TAG,"displayImage setImageResource mDefaultBackgroundResId = "+mDefaultBackgroundResId);
				imageViews.get(pos).setImageResource(mDefaultBackgroundResId);
			}
			return;
		}
		//Log.d(TAG, "displayImage imageUrls size = "+imageUrls.size());
		if(!Util.checkBoundIsOk(imageViews, pos) || !Util.checkBoundIsOk(imageUrls, toRealPosition(pos))){			
			Log.e(TAG, "displayImage checkBoundIsOk error....");
			return;
		}
		Log.d(TAG, "displayImage pos = "+toRealPosition(pos)+" url = "+imageUrls.get(toRealPosition(pos)));
		ImageUtil.getInstance().displayImage(context, imageUrls.get(toRealPosition(pos)), imageViews.get(pos), mDefaultImageResId);
		
	}
	private void showAnimation(int last,int pos){
		//Log.d(TAG,"showAnimation lastPosition "+last+"-->"+pos);
		if(Util.checkBoundIsOk(imageViews, last)){
			ImageView iv1 = imageViews.get(last);
			AlphaAnimation outAnimation = new AlphaAnimation( 1f,0.25f);
			outAnimation.setDuration(BannerConfig.ALAPH_DURATION);
			iv1.startAnimation(outAnimation);
		}
		
		if(Util.checkBoundIsOk(imageViews, pos)){
			ImageView iv = imageViews.get(pos);
			AlphaAnimation inAnimation = new AlphaAnimation(0.25f, 1f);
			inAnimation.setDuration(BannerConfig.ALAPH_DURATION);
			iv.startAnimation(inAnimation);
		}
		

	}

	private void setScaleType(View imageView) {
		
		if (imageView instanceof ImageView) {
			ImageView view = ((ImageView) imageView);
			switch (scaleType) {
			case 0:
				view.setScaleType(ScaleType.CENTER);
				break;
			case 1:
				view.setScaleType(ScaleType.CENTER_CROP);
				break;
			case 2:
				view.setScaleType(ScaleType.CENTER_INSIDE);
				break;
			case 3:
				view.setScaleType(ScaleType.FIT_CENTER);
				break;
			case 4:
				view.setScaleType(ScaleType.FIT_END);
				break;
			case 5:
				view.setScaleType(ScaleType.FIT_START);
				break;
			case 6:
				view.setScaleType(ScaleType.FIT_XY);
				break;
			case 7:
				view.setScaleType(ScaleType.MATRIX);
				break;
			default:
				break;
			}

		}
	}

	private void createIndicator() {
		indicatorImages.clear();
		indicator.removeAllViews();
		indicatorInside.removeAllViews();
		for (int i = 0; i < count; i++) {
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ViewAdjust.getWidthPxAdjust(context, mIndicatorWidth), 
					ViewAdjust.getWidthPxAdjust(context, mIndicatorHeight));
			params.leftMargin = ViewAdjust.getWidthPxAdjust(context, mIndicatorMargin);
			params.rightMargin = ViewAdjust.getWidthPxAdjust(context, mIndicatorMargin);
			if (i == 0) {
				imageView.setImageResource(mIndicatorSelectedResId);
			} else {
				imageView.setImageResource(mIndicatorUnselectedResId);
			}
			indicatorImages.add(imageView);
			if (bannerStyle == BannerConfig.CIRCLE_INDICATOR || bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE)
				indicator.addView(imageView, params);
			else if (bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
				indicatorInside.addView(imageView, params);
		}
	}

	private void setData() {
		currentItem = 1;
		if (adapter == null) {
			adapter = new BannerPagerAdapter();
			viewPager.setOnPageChangeListener(this);
		}
		viewPager.setAdapter(adapter);
		viewPager.setFocusable(true);
		viewPager.setCurrentItem(currentItem);
		adapter.notifyDataSetChanged();
		if(mOnPageChangeListener != null){
			mOnPageChangeListener.onPageSelected(toRealPosition(currentItem));
		}
		if (gravity != -1)
			indicator.setGravity(gravity);
		if (isScroll && count > 1) {
			viewPager.setScrollable(true);
		} else {
			viewPager.setScrollable(false);
		}
	}

	public void startAutoPlay() {
		if(isAutoPlay){
			UIHandler.getInstance().remove(task);
			UIHandler.getInstance().postDelay(task, delayTime);
		}
	}

	public void stopAutoPlay() {
		UIHandler.getInstance().remove(task);
	}

	private final Runnable task = new Runnable() {
		@Override
		public void run() {
			setNextItemSelected();
		}
	};
	
	
	public int getCurrPos(){
		return toRealPosition(currentItem);
	}
	
	public void setPreItemSelected(){
		
		UIHandler.getInstance().remove(task);
		
		if (count > 1 ) {
			int preItem = currentItem % (count + 1) - 1;
			if(preItem < 0){
				preItem = count ;
			}
			Log.i(TAG, "setNextItemSelected curr:" + currentItem + "-->"+preItem+" count:" + count);
			
			if (preItem == count) {
				viewPager.setCurrentItem(count, false);//
				if(isAutoPlay){
					UIHandler.getInstance().postDelay(task, delayTime);
				}
			} else {
				showAnimation(currentItem,preItem);
				viewPager.setCurrentItem(preItem);
				if(isAutoPlay){
					UIHandler.getInstance().postDelay(task, delayTime);
				}
			}
			currentItem = preItem;
		}
	}
	public void setNextItemSelected(){
		
		UIHandler.getInstance().remove(task);
		
		if (count > 1) {
			int nextItem = currentItem % (count + 1) + 1;
			Log.i(TAG, "setNextItemSelected curr:" + currentItem + "-->"+nextItem+" count:" + count);
			
			if (nextItem == 1) {
				viewPager.setCurrentItem(1, false);
				if(isAutoPlay){
					UIHandler.getInstance().postDelay(task, delayTime);
				}
			} else {
				showAnimation(currentItem,nextItem);
				viewPager.setCurrentItem(nextItem);
				if(isAutoPlay){
					UIHandler.getInstance().postDelay(task, delayTime);
				}
			}
			currentItem = nextItem;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// Log.i(tag, ev.getAction() + "--" + isAutoPlay);
		if (isAutoPlay) {
			int action = ev.getAction();
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
					|| action == MotionEvent.ACTION_OUTSIDE) {
				startAutoPlay();
			} else if (action == MotionEvent.ACTION_DOWN) {
				stopAutoPlay();
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 返回真实的位置
	 *
	 * @param position
	 * @return 下标从0开始
	 */
	public int toRealPosition(int position) {
		if(count == 0){
			return 0;
		}
		
		int realPosition = (position - 1) % count;
		if (realPosition < 0)
			realPosition += count;
		
		Log.d(TAG,"toRealPosition position = "+position+" count = "+count +" ret = "+realPosition);
		
		return realPosition;
	}

	class BannerPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			
			container.addView(imageViews.get(position));
			View view = imageViews.get(position);
			displayImage(position);
			if (listener != null && view != null) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listener.OnBannerClick(toRealPosition(position));
					}
				});
			}
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
		currentItem = viewPager.getCurrentItem();
		switch (state) {
		case 0:// No operation
			if (currentItem == 0) {
				viewPager.setCurrentItem(count, false);
			} else if (currentItem == count + 1) {
				viewPager.setCurrentItem(1, false);
			}
			break;
		case 1:// start Sliding
			if (currentItem == count + 1) {
				viewPager.setCurrentItem(1, false);
			} else if (currentItem == 0) {
				viewPager.setCurrentItem(count, false);
			}
			break;
		case 2:// end Sliding
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {

		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(toRealPosition(position));
		}
		if (bannerStyle == BannerConfig.CIRCLE_INDICATOR || bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE
				|| bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
			indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
			indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
			lastPosition = position;
		}
		displayImage(position);
		if (position == 0)
			position = count;
		if (position > count)
			position = 1;
		switch (bannerStyle) {
		case BannerConfig.CIRCLE_INDICATOR:
			break;
		case BannerConfig.NUM_INDICATOR:
			numIndicator.setText(position + "/" + count);
			break;
		case BannerConfig.NUM_INDICATOR_TITLE:
			numIndicatorInside.setText(position + "/" + count);
			bannerTitle.setText(titles.get(position - 1));
			break;
		case BannerConfig.CIRCLE_INDICATOR_TITLE:
			bannerTitle.setText(titles.get(position - 1));
			break;
		case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
			bannerTitle.setText(titles.get(position - 1));
			break;
		}

	}

	/**
	 * 下标是从0开始
	 *
	 * @param listener
	 * @return
	 */
	public Banner setOnBannerListener(OnBannerListener listener) {
		this.listener = listener;
		return this;
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		mOnPageChangeListener = onPageChangeListener;
	}

	public void stop() {
		UIHandler.getInstance().remove(task);
	}

	
}
