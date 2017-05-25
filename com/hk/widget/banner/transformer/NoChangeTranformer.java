package com.hk.view.widget.banner.transformer;

import android.view.View;

public class NoChangeTranformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float scale = 1f + Math.abs(position);
		view.setScaleX(1);
		view.setScaleY(1);
		view.setPivotX(view.getWidth() * 0.5f);
		view.setPivotY(view.getHeight() * 0.5f);
		view.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
		if(position == -1){
			view.setTranslationX(view.getWidth() * -1);
		}
	}

}
