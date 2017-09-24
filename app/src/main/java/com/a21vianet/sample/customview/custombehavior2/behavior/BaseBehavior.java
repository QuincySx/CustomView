package com.a21vianet.sample.customview.custombehavior2.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewParentCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.a21vianet.sample.customview.R;

import java.lang.ref.WeakReference;

/**
 * Created by wang.rongqiang on 2017/7/12.
 */

public class BaseBehavior extends CoordinatorLayout.Behavior {
    private WeakReference<View> mDependency;
    private WeakReference<View> mView;

    private int mMinHeight = -1;
    private int mMaxHeight = -1;

    private int mCurrentImageHeight;
    private int mDefImageHeight;

    private boolean isScrolling = false;

    private Scroller mScroller;
    private Handler mHandler;

    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1)
            .setDuration(50);

    public BaseBehavior() {
    }

    public BaseBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mHandler = new Handler();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.layout_title) {
            mDependency = new WeakReference<>(dependency);
            mView = new WeakReference<>(child);
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
            View layoutImgView = getDependencyView().getChildAt(0);
            mMaxHeight = mDependency.get().getMeasuredHeight();
            mMinHeight = mMaxHeight - layoutImgView.getMeasuredHeight();
            mDefImageHeight = layoutImgView.getMeasuredHeight();
        }

        getDependencyView().setTranslationY(0);

        child.layout(0, 0, parent.getWidth(), parent.getHeight() - mMinHeight);
        child.setTranslationY(getDependencyView().getMeasuredHeight() + getDependencyView().getTranslationY());

        initChildTouchListener();
        return true;
    }

    private void initChildTouchListener() {
        getView().setOnTouchListener(new View.OnTouchListener() {
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
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        upSlide(dy, consumed);
    }

    private void upSlide(int dy, int[] consumed) {
        if (dy > 0) {
            LinearLayout ImageLayout = (LinearLayout) getDependencyView().getChildAt(0);
            ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();
            mCurrentImageHeight = imageLayoutLayoutParams.height;

            float measuredHeight = getDependencyView().getTranslationY();
            if ((measuredHeight - dy) + getDependencyView().getMeasuredHeight() >= mMinHeight) {
                getDependencyView().setTranslationY(measuredHeight - dy);
                consumed[1] = dy;
            } else {
                getDependencyView().setTranslationY(mMinHeight - getDependencyView().getMeasuredHeight());
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        downSlide(dyUnconsumed);
    }

    private void downSlide(int dy) {
        if (dy < 0) {
            LinearLayout ImageLayout = (LinearLayout) getDependencyView().getChildAt(0);
            ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();

            float measuredHeight = getDependencyView().getTranslationY();
            if ((measuredHeight - dy) + getDependencyView().getMeasuredHeight() <= mMaxHeight) {
                getDependencyView().setTranslationY(measuredHeight - dy);
                mCurrentImageHeight = imageLayoutLayoutParams.height;
            } else {
                getDependencyView().setTranslationY(0);
                imageLayoutLayoutParams.height -= dy;
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
        if (isScrolling) {
            return;
        }

        if (mCurrentImageHeight <= mDefImageHeight) {
            return;
        }

        final int diffHeight = mCurrentImageHeight - mDefImageHeight;
        final LinearLayout ImageLayout = (LinearLayout) getDependencyView().getChildAt(0);
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

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        ViewParentCompat.onStartNestedScroll(coordinatorLayout, child, target, (int) velocityY);
        return onUserStopDragging(velocityY);
    }

    private boolean onUserStopDragging(float velocity) {
        ViewGroup dependencyView = getDependencyView();
        float translateY = dependencyView.getTranslationY();
        float minHeaderTranslate = -(mMaxHeight - dependencyView.getChildAt(1).getMeasuredHeight());

        if (translateY == 0 || translateY == minHeaderTranslate) {
            return false;
        }

        boolean targetState; // Flag indicates whether to expand the content.
        if (Math.abs(velocity) <= 800) {
            if (Math.abs(translateY) < Math.abs(translateY - minHeaderTranslate)) {
                targetState = false;
            } else {
                targetState = true;
            }
            velocity = 800; // Limit velocity's minimum value.
        } else {
            if (velocity > 0) {
                targetState = true;
            } else {
                targetState = false;
            }
        }
        float targetTranslateY = targetState ? minHeaderTranslate : 0;
        mScroller.startScroll(0, (int) translateY, 0, (int) (targetTranslateY - translateY), (int) (1000000 / Math.abs(velocity)));
        mHandler.post(flingRunnable);
        isScrolling = true;
        return true;
    }

    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                getDependencyView().setTranslationY(mScroller.getCurrY());
                mHandler.post(this);
            } else {
                isScrolling = false;
            }
        }
    };

    private ViewGroup getDependencyView() {
        return (ViewGroup) mDependency.get();
    }

    private View getView() {
        return mView.get();
    }
}
