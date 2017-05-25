package com.hk.view.widget;

import java.util.ArrayList;

import com.tvb.hk.anywhere.R;
import com.tvb.hk.anywhere.bean.AuthRetBean;
import com.tvb.hk.anywhere.data.DataUtil;
import com.tvb.hk.anywhere.dialog.BaseDialog;
import com.tvb.hk.anywhere.dialog.LoadingDialog;
import com.tvb.hk.anywhere.fragment.BaseFragment;
import com.tvb.hk.anywhere.fragmentV3.ContentFragment;
import com.tvb.hk.anywhere.fragmentV3.DramaDetailFragment;
import com.tvb.hk.anywhere.player.PlayerUtil;
import com.tvb.hk.anywhere.sdk.bos.BOSClient;
import com.tvb.hk.anywhere.sdk.media.MediaBean;
import com.tvb.hk.anywhere.util.UIHandler;
import com.tvb.hk.anywhere.util.Util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class UtilFragment {

	private static final String TAG = "UtilFragment";

	public static void showFragment(Fragment ctx,int container_id,BaseFragment fragment,String tag) {
        Log.d(TAG, "addFragment  fragment tag = " + tag );
        if (fragment != null){
        	FragmentTransaction fragmentTransaction = ctx.getFragmentManager().beginTransaction();
//        	fragmentTransaction.setCustomAnimations(
//        			R.anim.push_left_in,R.anim.push_left_in,R.anim.back_left_in,R.anim.back_right_out);
        	fragmentTransaction.add(container_id, fragment, tag);
        	fragmentTransaction.show(fragment);
        	fragmentTransaction.commitAllowingStateLoss();        	
        }
    }

	public static  void removeFragment(Fragment ctx,String tag) {
        Log.d(TAG, "removeFragment  tag = " + tag);
        FragmentTransaction fragmentTransaction = ctx.getFragmentManager().beginTransaction();
        Fragment fragment = ctx.getFragmentManager().findFragmentByTag(tag);
        if (fragment != null){
        	fragmentTransaction.remove(fragment);
        	fragmentTransaction.commitAllowingStateLoss();
        }
    }
	
	public static Fragment getFramnet(Fragment ctx,String tag){
       // FragmentTransaction fragmentTransaction = ctx.getFragmentManager().beginTransaction();
        Fragment fragment = ctx.getFragmentManager().findFragmentByTag(tag);
        Log.d(TAG, "getFramnet  tag = " + tag +" ret = "+fragment);
        return fragment;
	}

	public static void showDetialFragment(final Fragment ctx,final int contain_id,final MediaBean b,
			final String whereFrom){
    	
    	if(ctx == null || b== null){
    		return;
    	}
    	if(MediaBean.isLiveChannel(b)){
    		PlayerUtil.getInstance().startPlay(b, -1);
    		return;
    	}
    	final BaseDialog dialog = LoadingDialog.show(ctx.getContext());
    	new Thread(new Runnable() {

			@Override
			public void run() {
				
				final MediaBean detialBean =DataUtil.detail(b.getPid(),b.getMid());
				
				UIHandler.getInstance().post(new Runnable() {

					@Override
					public void run() {
						
						dialog.dismiss();
						
						if(detialBean != null && detialBean.getAuthRet() == null){
							UIHandler.getInstance().showToast(ctx.getContext(),
									ctx.getContext().getString(R.string.get_auth_ret_failed));
							return;
						}
						if (detialBean != null) {
							Bundle bundle = new Bundle();
							bundle.putSerializable(DramaDetailFragment.KEY_DETIAL_BEAN, detialBean);
							bundle.putString(DramaDetailFragment.KEY_WHERE_FROM, whereFrom);
							DramaDetailFragment fragment = new DramaDetailFragment();
							fragment.setArguments(bundle);
							UtilFragment.showFragment(ctx, contain_id,
									fragment,DramaDetailFragment.class.getSimpleName());
						} else {
							UIHandler.getInstance().showToast(ctx.getContext(),
									ctx.getContext().getString(R.string.get_vod_detial_failed));
						}
					}
				});

			}
		}).start();
    }

	public static void showFinalDetialFragment1(final Fragment ctx,final int contain_id,final MediaBean b,
			final String whereFrom){
    	
    	if(ctx == null || b== null){
    		return;
    	}
    	
    	final BaseDialog dialog =  LoadingDialog.show(ctx.getContext());
    	new Thread(new Runnable() {

			@Override
			public void run() {

				final AuthRetBean authRet = BOSClient.getInstance().auth(b);
				
				UIHandler.getInstance().post(new Runnable() {

					@Override
					public void run() {
						
						dialog.dismiss();
						if(authRet == null){
							UIHandler.getInstance().showToast(ctx.getContext(), ctx.getString(R.string.get_auth_ret_failed));
						}else{
							MediaBean  finalDeatilBean = (MediaBean)b.clone();
							finalDeatilBean.setAuthRet(authRet);
							Bundle bundle = new Bundle();
							bundle.putSerializable(DramaDetailFragment.KEY_DETIAL_BEAN, (MediaBean)finalDeatilBean.clone());
							bundle.putString(DramaDetailFragment.KEY_WHERE_FROM, whereFrom);
							DramaDetailFragment fragment = new DramaDetailFragment();
							fragment.setArguments(bundle);
							UtilFragment.showFragment(ctx, contain_id,fragment,DramaDetailFragment.class.getSimpleName());
						
						}
					}
				});
			}
    	}).start();
    	
    	
    }
	
	public static void showPackageDetail(final Context c, final int cotainer_id, final Fragment fg, final MediaBean mb,
			final String whreFrom) {

		if (c == null || fg == null || mb == null) {
			UIHandler.getInstance().showToast(c, "parameter error !!");
			return;
		}
		
		final BaseDialog dialog =  LoadingDialog.show(c);
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				final MediaBean detailBean = DataUtil.detail(mb.getPid(), mb.getMid());

				UIHandler.getInstance().post(new Runnable() {

					@Override
					public void run() {
						
						dialog.dismiss();
						if (detailBean == null || detailBean.getPackages() == null) {
							UIHandler.getInstance().showToast(c, "獲取產品包詳情失敗，請稍後重試！");
						} else{
							
							ContentFragment fragment = new ContentFragment();
							Bundle budle = new Bundle();
							budle.putString(ContentFragment.KEY_WHERE_FROM, whreFrom);
							budle.putString(ContentFragment.KEY_TITLE,
									c.getString(R.string.personal_purchased_package_title) + mb.getTitleStr());
							budle.putSerializable(ContentFragment.KEY_MEDIA_LIST, detailBean.getPackages());

							fragment.setArguments(budle);
							UtilFragment.showFragment(fg, cotainer_id, fragment, ContentFragment.class.getSimpleName());
						}

					}
				});
			}
		}).start();
	}
	
	public static void showPackageDetial(Fragment fg, final int cotainer_id, String packageTitle,
			ArrayList<MediaBean> list, String whreFrom) {

		if (fg == null || Util.isEmpty(list)) {
			Log.e(TAG, "showPackageDetial error ");
			return;
		}
		ContentFragment fragment = new ContentFragment();
		Bundle budle = new Bundle();
		budle.putString(ContentFragment.KEY_WHERE_FROM, whreFrom);
		budle.putString(ContentFragment.KEY_TITLE,
				fg.getContext().getString(R.string.personal_purchased_package_title) + packageTitle);
		budle.putSerializable(ContentFragment.KEY_MEDIA_LIST, list);

		fragment.setArguments(budle);
		UtilFragment.showFragment(fg, cotainer_id, fragment, ContentFragment.class.getSimpleName());
	}
}
