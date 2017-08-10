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
    private int mTagPullDistance = 400;

    private float mProgress = 0;

    private int mWidth;
    private int mHeight;

    private int mRadius = 100;

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
        int measureWidth, measureHeight;

        int defWidth = mRadius * 2 + getPaddingLeft() + getPaddingRight();
        int defHeight = mTagPullDistance + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            measureWidth = Math.min(defWidth, widthSize);
        } else {
            measureWidth = defWidth;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
            mTagPullDistance = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            measureHeight = Math.min(defHeight, heightSize);
        } else {
            measureHeight = defHeight;
        }

        setMeasuredDimension(measureWidth, (int) (measureHeight * mProgress));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mWidth >> 1, mTagPullDistance * mProgress - mRadius, mRadius, mCirclePaint);
        canvas.drawPath(mBeziePath, mBeziePaint);
    }

    /**
     * 更新曲线
     */
    private void updatePathLayout() {
        //计算圆的圆心
        int CircleX = mWidth >> 1;
        int CircleY = (int) (mTagPullDistance * mProgress - mRadius);

        mBeziePath.reset();
        
        //左半部分
        {
            //起始点
            mBeginSpotX = mTagBeginSpotX * mProgress;

            //结束点
            float currentAngle = mTagAngle * mProgress;
            mEndSpotX = (float) (CircleX - Math.sin((currentAngle / 180) * Math.PI) * mRadius);
            mEndSpotY = (float) (CircleY + Math.cos((currentAngle / 180) * Math.PI) * mRadius);

            //控制点
            double a = (mEndSpotY) / Math.tan((currentAngle / 180) * Math.PI);
            mControllerSpotX = (float) (mEndSpotX - a);

            mBeziePath.moveTo(mBeginSpotX, mBeginSpotY);
            mBeziePath.quadTo(mControllerSpotX, mControllerSpotY, mEndSpotX, mEndSpotY);
        }
        //右半部分
        {
            //结束点
            float currentAngle = mTagAngle * mProgress;
            mEndSpotX = (float) (CircleX + Math.sin((currentAngle / 180) * Math.PI) * mRadius);
            mEndSpotY = (float) (CircleY + Math.cos((currentAngle / 180) * Math.PI) * mRadius);

            //控制点
            double a = (mEndSpotY) / Math.tan((currentAngle / 180) * Math.PI);
            mControllerSpotX = (float) (mEndSpotX + a);

            //起始点
            mBeginSpotX = mWidth - mTagBeginSpotX * mProgress;

            mBeziePath.lineTo(mEndSpotX, mEndSpotY);
            mBeziePath.quadTo(mControllerSpotX, mControllerSpotY, mBeginSpotX, mBeginSpotY);
        }
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
