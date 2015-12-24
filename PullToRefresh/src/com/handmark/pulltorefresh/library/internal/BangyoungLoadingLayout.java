/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

@SuppressLint("ViewConstructor")
public class BangyoungLoadingLayout extends LoadingLayout {

	private AnimationDrawable mAnimation;
	private final int RELEASE_RESOURCE_ID,REFRESH_RESOURCE_ID;
	public BangyoungLoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs,LoadingLayout.TYPE_NORMAL);
		if(mode == Mode.PULL_FROM_START){
			REFRESH_RESOURCE_ID = R.drawable.refresh_arrow_down;
			RELEASE_RESOURCE_ID = R.drawable.refresh_arrow_up;
		} else {
			REFRESH_RESOURCE_ID = R.drawable.refresh_arrow_down;
			RELEASE_RESOURCE_ID = R.drawable.refresh_arrow_down;
		}
		mHeaderImage.setScaleType(ScaleType.CENTER_INSIDE);

		mHeaderImage.setVisibility(silent ? View.INVISIBLE : View.VISIBLE);
		mHeaderText.setVisibility(View.GONE);
		mSubHeaderText.setVisibility(View.GONE);
		mAnimation = (AnimationDrawable)getResources().getDrawable(R.drawable.anim_catch_loading);
		mAnimation.setOneShot(false);
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			final int dHeight = imageDrawable.getIntrinsicHeight();
			final int dWidth = imageDrawable.getIntrinsicWidth();

			/**
			 * We need to set the width/height of the ImageView so that it is
			 * square with each side the size of the largest drawable dimension.
			 * This is so that it doesn't clip when rotated.
			 */
			ViewGroup.LayoutParams lp = mHeaderImage.getLayoutParams();
			lp.width = lp.height = Math.max(dHeight, dWidth);
			mHeaderImage.requestLayout();

			/**
			 * We now rotate the Drawable so that is at the correct rotation,
			 * and is centered.
			 */
		}
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		// NO-OP
	}

	@Override
	protected void pullToRefreshImpl() {
		if(!silent) {
			if (mAnimation.isRunning()) mAnimation.stop();
			mHeaderImage.setImageResource(REFRESH_RESOURCE_ID);
		}
	}

	@Override
	protected void refreshingImpl() {
		if(!silent) {
			mHeaderImage.setImageDrawable(mAnimation);
			mAnimation.start();
		}
	}

	@Override
	protected void releaseToRefreshImpl() {
		if(!silent) {
			if (mAnimation.isRunning()) mAnimation.stop();
			mHeaderImage.setImageResource(RELEASE_RESOURCE_ID);
		}
	}

	@Override
	protected void resetImpl() {
		if(!silent) {
			mHeaderImage.clearAnimation();
			mHeaderProgress.setVisibility(View.GONE);
			mHeaderImage.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.refresh_arrow_down;
	}

}
