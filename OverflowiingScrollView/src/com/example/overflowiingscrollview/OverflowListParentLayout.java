package com.example.overflowiingscrollview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class OverflowListParentLayout extends RelativeLayout {

	private static final int SPACER_VIEW_ID = 7363;
	private ListView overflowList;
	private View spacerHeaderView;

	public OverflowListParentLayout(Context context) {
		super(context);
	}

	public OverflowListParentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OverflowListParentLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (overflowList == null || findViewById(R.id.spacerView) == null) {
			return super.onInterceptTouchEvent(ev);
		} else {

			Rect hitRect = new Rect();
			spacerHeaderView.getHitRect(hitRect);

			if (hitRect.contains((int) ev.getX(), (int) ev.getY())) {
				if (ev.getAction() != MotionEvent.ACTION_MOVE) {
					findViewById(R.id.spacerView).dispatchTouchEvent(ev);
				} else {
					overflowList.setEnabled(false);
				}
				return false;
			}

			overflowList.getHitRect(hitRect);

			if (hitRect.contains((int) ev.getX(), (int) ev.getY())) {
				overflowList.setEnabled(true);
				overflowList.dispatchTouchEvent(ev);
				ev.setAction(MotionEvent.ACTION_CANCEL);
				findViewById(R.id.spacerView).dispatchTouchEvent(ev);
			} else {
				ev.setAction(MotionEvent.ACTION_CANCEL);
				findViewById(R.id.spacerView).dispatchTouchEvent(ev);
			}
			return false;
		}

	}

	public void setOverflowList(ListView overflowList) {
		if (overflowList != null && overflowList.getAdapter() == null) {
			this.overflowList = overflowList;
			spacerHeaderView = new View(getContext());
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources()
							.getDimension(R.dimen.interactive_height));
			spacerHeaderView.setId(SPACER_VIEW_ID);
			spacerHeaderView.setLayoutParams(layoutParams);

			overflowList.addHeaderView(spacerHeaderView, null, false);
		} else {
			throw new IllegalStateException(
					"overflow list must not be null, and should not have adapter set");
		}
	}

}
