package com.a21vianet.sample.customview.custombehavior2.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a21vianet.sample.customview.R;

import java.lang.ref.WeakReference;

/**
 * Created by wang.rongqiang on 2017/7/12.
 */

public class BaseBehavior extends CoordinatorLayout.Behavior {
    private WeakReference<View> mDependency;

    private int mMinHeight = -1;
    private int mMaxHeight = -1;

    private int mCurrentImageHeight;
    private int mDefImageHeight;

    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1)
            .setDuration(50);

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
            mMaxHeight = mDependency.get().getMeasuredHeight();
            mMinHeight = mMaxHeight - layoutImgeView.getMeasuredHeight();
            mDefImageHeight = layoutImgeView.getMeasuredHeight();
        }

        mDependency.get().setTranslationY(0);

        child.layout(0, 0, parent.getWidth(), parent.getHeight() - mMinHeight);
        child.setTranslationY(mDependency.get().getMeasuredHeight() + mDependency.get().getTranslationY());

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        zoom();
                        break;
                }
                return false;
            }
        });
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
            } else {
                mDependency.get().setTranslationY(0);

                LinearLayout ImageLayout = (LinearLayout) ((ViewGroup) mDependency.get()).getChildAt(0);
                ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();
                imageLayoutLayoutParams.height -= dyUnconsumed;
                mCurrentImageHeight = imageLayoutLayoutParams.height;
                ImageLayout.setLayoutParams(imageLayoutLayoutParams);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                zoom();
                break;
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    private void zoom() {
        if (mCurrentImageHeight <= mDefImageHeight) {
            return;
        }
        final int diffHeight = mCurrentImageHeight - mDefImageHeight;
        final LinearLayout ImageLayout = (LinearLayout) ((ViewGroup) mDependency.get()).getChildAt(0);
        final ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                imageLayoutLayoutParams.height = (int) (mCurrentImageHeight - diffHeight * animatedValue);
                ImageLayout.setLayoutParams(imageLayoutLayoutParams);
            }
        });
        valueAnimator.start();
    }
}
