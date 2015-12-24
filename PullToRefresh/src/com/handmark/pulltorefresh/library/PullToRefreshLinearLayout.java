package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import uk.co.androidalliance.edgeeffectoverride.ContextWrapperEdgeEffect;

/**
 * Created by ProgrammerYuan on 15/8/25.
 */
public class PullToRefreshLinearLayout extends PullToRefreshBase<LinearLayout>{

	public static final int PULL_FROM_NOT_READY = 0;
	public static final int PULL_FROM_START_READY = 1;
	public static final int PULL_FROM_END_READY = 2;
	public static final int PULL_FROM_BOTH = 3;

	int mode;

	public PullToRefreshLinearLayout(Context context) {
		super(context);
	}

	public PullToRefreshLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshLinearLayout(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshLinearLayout(Context context, Mode mode, AnimationStyle animStyle) {
		super(context, mode, animStyle);
	}

	@Override
	public Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected LinearLayout createRefreshableView(Context context, AttributeSet attrs) {
		LinearLayout linearLayout;

		linearLayout = new LinearLayout(new ContextWrapperEdgeEffect(context), attrs);
		linearLayout.setId(R.id.linearlayout);
		return linearLayout;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return mode == PULL_FROM_END_READY || mode == PULL_FROM_BOTH;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mode == PULL_FROM_START_READY || mode == PULL_FROM_BOTH;
	}
}
