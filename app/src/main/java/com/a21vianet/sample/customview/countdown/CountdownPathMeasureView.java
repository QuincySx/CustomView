package com.a21vianet.sample.customview.countdown;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.a21vianet.sample.customview.R;
import com.a21vianet.sample.customview.utilitly.DensityUtils;

/**
 * Created by wang.rongqiang on 2017/4/20.
 */

public class CountdownPathMeasureView extends View {
    private Path mPath = new Path();
    private Path mPathDst = new Path();
    private Paint mAnnulusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSmallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PathMeasure mPathMeasure = new PathMeasure();

    private ValueAnimator mValueAnimator;

    private OnStatusListener mOnStatusListener;

    private float mTotalWidth;
    private float mTotalHieght;
    private float mTotalMin;

    private float mCurrentValue;

    private float mAnnulusWidth;
    private float mAnnulusPadding;

    @ColorInt
    private int mCircleColor = 0X60CCCCCC;
    @ColorInt
    private int mAnnulusColor = 0X60CCCCCC;
    @ColorInt
    private int mHintColor = 0XFF000000;

    private float mSmallTextSize;
    private float mHintTextSize;

    private int mDurationTime = 3;

    private String mHint = "跳过";

    public CountdownPathMeasureView(Context context) {
        this(context, null);
    }

    public CountdownPathMeasureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownPathMeasureView(Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .CountdownPathMeasureView);

        mHintColor = typedArray.getColor(R.styleable.CountdownPathMeasureView_HintColor,
                mHintColor);
        mAnnulusPadding = typedArray.getDimension(R.styleable
                .CountdownPathMeasureView_AnnulusPadding, DensityUtils.dp2px(context, 1));
        mAnnulusWidth = typedArray.getDimension(R.styleable
                .CountdownPathMeasureView_AnnulusWidth, DensityUtils.dp2px(context, 3));
        mHintTextSize = typedArray.getDimension(R.styleable
                .CountdownPathMeasureView_HintTextSize, DensityUtils.sp2px(context, 12));
        mSmallTextSize = typedArray.getDimension(R.styleable
                .CountdownPathMeasureView_SmallTextSize, DensityUtils.sp2px(context, 10));
        mCircleColor = typedArray.getColor(R.styleable.CountdownPathMeasureView_CircleColor,
                mCircleColor);
        mAnnulusColor = typedArray.getColor(R.styleable.CountdownPathMeasureView_AnnulusColor,
                mAnnulusColor);
        mDurationTime = typedArray.getInt(R.styleable.CountdownPathMeasureView_DurationTime,
                mDurationTime);

        if (typedArray.getString(R.styleable.CountdownPathMeasureView_CircleHint) != null) {
            mHint = typedArray.getString(R.styleable.CountdownPathMeasureView_CircleHint);
        }

        typedArray.recycle();

        init();
    }

    private void init() {
        mAnnulusPaint.setColor(mAnnulusColor);
        mAnnulusPaint.setStrokeWidth(mAnnulusWidth);
        mAnnulusPaint.setStyle(Paint.Style.STROKE);

        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(mHintColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mHintTextSize);

        mSmallTextPaint.setColor(mHintColor);
        mSmallTextPaint.setTextAlign(Paint.Align.CENTER);
        mSmallTextPaint.setTextSize(mSmallTextSize);

        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(mDurationTime * 1000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mOnStatusListener != null) {
                    mOnStatusListener.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnStatusListener != null) {
                    mOnStatusListener.end();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHieght = h;

        mTotalMin = Math.min(w, h);

        float radius = mTotalMin / 2 - mAnnulusWidth / 2;
        mPath.addCircle(mTotalWidth / 2, mTotalHieght / 2, radius, Path.Direction.CW);
        mPathMeasure.setPath(mPath, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenterCircle(canvas);
        drawHintText(canvas);
        drawCountdown(canvas);
        drawCountdownAnnulus(canvas);
    }

    /**
     * 画出中心的圆
     *
     * @param canvas
     */
    private void drawCenterCircle(Canvas canvas) {
        float radius = mTotalMin / 2 - mAnnulusWidth - mAnnulusPadding;
        canvas.drawCircle(mTotalWidth / 2, mTotalHieght / 2, radius, mPaint);
    }

    /**
     * 画出中心圆上的提示文字
     *
     * @param canvas
     */
    private void drawHintText(Canvas canvas) {
        canvas.drawText(mHint, mTotalWidth / 2, mTotalHieght / 2, mTextPaint);
    }

    /**
     * 画出倒计时
     *
     * @param canvas
     */
    private void drawCountdown(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mSmallTextPaint.getFontMetrics();
        float hieght = mTotalHieght / 2 + fontMetrics.bottom + (mTotalHieght - 2 * mAnnulusWidth -
                2 * mAnnulusPadding) / 4;
        canvas.drawText((mDurationTime - (int) (mDurationTime * mCurrentValue)) + "秒",
                mTotalWidth / 2, hieght, mSmallTextPaint);
    }

    /**
     * 画出倒计时圆环
     *
     * @param canvas
     */
    private void drawCountdownAnnulus(Canvas canvas) {
        mPathDst.reset();
        mPathDst.lineTo(0, 0);
        float start = 0;
        float end = mPathMeasure.getLength() * mCurrentValue;
        mPathMeasure.getSegment(start, end, mPathDst, true);
        canvas.drawPath(mPathDst, mAnnulusPaint);
    }

    public void setAnnulusColor(@ColorInt int color) {
        mAnnulusColor = color;
        mAnnulusPaint.setColor(mAnnulusColor);
    }

    public void setCircleColor(@ColorInt int color) {
        mCircleColor = color;
        mPaint.setColor(mCircleColor);
    }

    public void setAnnulusWidth(int width) {
        mAnnulusWidth = DensityUtils.dp2px(getContext(), width);
        mAnnulusPaint.setStrokeWidth(mAnnulusWidth);
    }

    public void setAnnulusPadding(int padding) {
        mAnnulusPadding = DensityUtils.dp2px(getContext(), padding);
    }

    public void setDurationTime(int second) {
        mDurationTime = second;
        mValueAnimator.setDuration(mDurationTime * 1000);
    }

    public void setHint(String str) {
        mHint = str;
    }

    public void setHintTextSize(int size) {
        mHintTextSize = DensityUtils.sp2px(getContext(), size);
        mTextPaint.setTextSize(mHintTextSize);
    }

    public void setSmallTextSize(int size) {
        mSmallTextSize = DensityUtils.sp2px(getContext(), size);
        mSmallTextPaint.setTextSize(mSmallTextSize);
    }

    public void setOnStatusListener(OnStatusListener listener) {
        mOnStatusListener = listener;
    }

    public void statAnim() {
        mValueAnimator.start();
    }

    public interface OnStatusListener {
        void start();

        void end();
    }
}
