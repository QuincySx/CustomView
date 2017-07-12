package com.a21vianet.sample.customview.custombehavior1.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.a21vianet.sample.customview.R;

import java.lang.ref.WeakReference;

/**
 * Created by wang.rongqiang on 2017/7/12.
 */

public class BaseBehavior extends CoordinatorLayout.Behavior {
    private WeakReference<View> mDependency;
    private WeakReference<View> mSearchDependency;

    private int mMinHeight = -1;
    private int mMaxHeight = -1;

    public BaseBehavior() {
    }

    public BaseBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.layout_title) {
            mDependency = new WeakReference(dependency);
            return true;
        }
        if (dependency != null && dependency.getId() == R.id.layout_search) {
            mSearchDependency = new WeakReference(dependency);
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        if (mMaxHeight == -1) {
            View layoutImgeView = ((LinearLayout) mDependency.get()).getChildAt(0);
            mMaxHeight = mDependency.get().getMeasuredHeight() + mSearchDependency.get().getMeasuredHeight();
            mMinHeight = mMaxHeight - layoutImgeView.getMeasuredHeight();
        }
        child.layout(0, 0, parent.getWidth(), parent.getHeight() - mMinHeight);
        child.setTranslationY(parent.getHeight() + parent.getTranslationY());
        mDependency.get().setTranslationY(mSearchDependency.get().getMeasuredHeight());
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0) {
            float measuredHeight = mDependency.get().getTranslationY();
            if ((measuredHeight - dy) + mDependency.get().getMeasuredHeight() >= mMinHeight) {
                mDependency.get().setTranslationY(measuredHeight - dy);
                consumed[1] = dy;
            } else {
                mDependency.get().setTranslationY(mMinHeight - mDependency.get().getMeasuredHeight());
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            float measuredHeight = mDependency.get().getTranslationY();
            if ((measuredHeight - dyUnconsumed) + mDependency.get().getMeasuredHeight() <= mMaxHeight) {
                mDependency.get().setTranslationY(measuredHeight - dyUnconsumed);
            }else{
                mDependency.get().setTranslationY(mSearchDependency.get().getMeasuredHeight());
            }
        }
    }
}
