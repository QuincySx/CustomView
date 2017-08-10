package com.a21vianet.sample.customview.pullview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by quincysx on 2017/8/8.
 */

public class PullView extends View {
    /**
     * 拉动距离
     */
    private int mTagPullDistance = 500;

    private float mProgress = 0;

    private int mWidth;
    private int mHeight;

    private int mRadius = 80;

    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mBeginSpotX, mBeginSpotY = 0;

    private float mTagBeginSpotX = 400;

    private float mControllerSpotX = 0;
    private float mControllerSpotY = 0;

    private float mEndSpotX, mEndSpotY;

    private float mTagAngle = 120;

    private Path mBeziePath = new Path();
    private Paint mBeziePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PullView(Context context) {
        super(context);
        init();
    }

    public PullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        mCirclePaint.setStyle(Paint.Style.FILL);

        mBeziePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        updatePathLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = getPaddingLeft() + mRadius * 2 + getPaddingRight();
        int measureHeight = (int) (mProgress * mTagPullDistance + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(measureHanlder(widthMeasureSpec, measureWidth), measureHanlder(heightMeasureSpec, measureHeight));
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mWidth >> 1, mTagPullDistance * mProgress - mRadius, mRadius, mCirclePaint);
        canvas.drawPath(mBeziePath, mBeziePaint);
    }

    /**
     * 计算 View 所占距离
     *
     * @param measureSpec
     * @return
     */
    private int measureHanlder(int measureSpec, int defaultSize) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    /**
     * 更新曲线
     */
    private void updatePathLayout() {
        //起始点
        mBeginSpotX = mTagBeginSpotX * mProgress;
        //控制点

        //结束点
        int CircleX = mWidth >> 1;
        int CircleY = (int) (mTagPullDistance * mProgress - mRadius);

        float currentAngle = mTagAngle * mProgress;
        mEndSpotX = (float) (CircleX - Math.sin(currentAngle) * mRadius);
        mEndSpotY = (float) (CircleY + Math.cos(currentAngle) * mRadius);

        mBeziePath.reset();
        mBeziePath.moveTo(mBeginSpotX, mBeginSpotY);
        mBeziePath.quadTo(mControllerSpotX, mControllerSpotY, mEndSpotX, mEndSpotY);
//        for (int i = 0; i < 1000; i++) {
//            Pair<Float, Float> floatFloatPair = BezierUtiltiy.computeBezier(0 / 1000f, new float[]{mBeginSpotX, mBeginSpotY}, new float[]{mControllerSpotX, mControllerSpotY}, new float[]{mEndSpotX, mEndSpotY});
//            mBeziePath.moveTo(floatFloatPair.first, floatFloatPair.second);
//        }
        mBeziePath.close();
    }

    public void setProgress(float value) {
        mProgress = value;
        requestLayout();
    }

    public int getTagPullDistance() {
        return mTagPullDistance;
    }
}
