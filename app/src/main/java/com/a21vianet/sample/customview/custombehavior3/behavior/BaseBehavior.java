package com.a21vianet.sample.customview.custombehavior3.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
    private WeakReference<View> mImageView;
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
        if (dependency != null && dependency.getId() == R.id.layout_image) {
            mImageView = new WeakReference<>(dependency);
            mDefImageHeight = (int) getImgHeight();
        }
        if (dependency != null && dependency.getId() == R.id.layout_bar) {
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
            mMinHeight = (int) getDependencyView().getMeasuredHeight();
            mDefImageHeight = layoutImgView.getMeasuredHeight();

            getDependencyView().setTranslationY(getImgHeight());
            child.layout(0, 0, parent.getWidth(), parent.getHeight() - mMinHeight);
            child.setTranslationY(getDependencyView().getMeasuredHeight() + getDependencyView().getTranslationY());
            initChildTouchListener();
        }
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
            LinearLayout ImageLayout = (LinearLayout) getImageView();
            ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();
            mCurrentImageHeight = imageLayoutLayoutParams.height;

            float measuredHeight = getDependencyView().getTranslationY();
            if (measuredHeight - dy >= 0) {
                getDependencyView().setTranslationY(measuredHeight - dy);
                if (measuredHeight > getImgHeight()) {
                    imageLayoutLayoutParams.height -= dy;
                    mCurrentImageHeight = imageLayoutLayoutParams.height;
                    getImageView().setLayoutParams(imageLayoutLayoutParams);
                }
                consumed[1] = dy;
            } else {
                getDependencyView().setTranslationY(0);
                imageLayoutLayoutParams.height = mDefImageHeight;
                getImageView().setLayoutParams(imageLayoutLayoutParams);
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        downSlide(dyUnconsumed);
    }

    private void downSlide(int dy) {
        if (dy < 0) {
            LinearLayout ImageLayout = (LinearLayout) getImageView();
            ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();

            float measuredHeight = getDependencyView().getTranslationY();
            getDependencyView().setTranslationY(measuredHeight - dy);

            if (measuredHeight - dy > getImgHeight()) {
                imageLayoutLayoutParams.height -= dy;
                ImageLayout.setLayoutParams(imageLayoutLayoutParams);
            }

            mCurrentImageHeight = imageLayoutLayoutParams.height;
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
        final LinearLayout ImageLayout = (LinearLayout) getImageView();
        final ViewGroup.LayoutParams imageLayoutLayoutParams = ImageLayout.getLayoutParams();
        final float translationY = getDependencyView().getTranslationY();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                imageLayoutLayoutParams.height = (int) (mCurrentImageHeight - diffHeight * animatedValue);
                ImageLayout.setLayoutParams(imageLayoutLayoutParams);
                getDependencyView().setTranslationY(translationY - diffHeight * animatedValue);
            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        return onUserStopDragging(velocityY);
    }

    private boolean onUserStopDragging(float velocity) {
        ViewGroup dependencyView = getDependencyView();
        float translateY = dependencyView.getTranslationY();
        if (translateY == 0) {
            return false;
        }
        if (velocity > 1000) {
            mScroller.startScroll(0, (int) translateY, 0, (int) -getDependencyView().getTranslationY(), (int) (1000000 / Math.abs(velocity)));
            mHandler.post(flingRunnable);
            return true;
        } else if (velocity < 1000) {
            if (translateY >= getImgHeight()) {
                return false;
            }
            mScroller.startScroll(0, (int) translateY, 0, (int) (getImageView().getMeasuredHeight() - getDependencyView().getTranslationY()), (int) (1000000 / Math.abs(velocity)));
            mHandler.post(flingRunnable);
            return true;
        } else {
            return false;
        }
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

    private View getImageView() {
        return mImageView.get();
    }

    private View getView() {
        return mView.get();
    }

    private float getImgHeight() {
        return getImageView().getResources().getDimension(R.dimen.image_height);
    }
}
