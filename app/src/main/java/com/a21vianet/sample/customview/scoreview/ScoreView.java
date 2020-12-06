package com.a21vianet.sample.customview.scoreview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.a21vianet.sample.customview.MyApplication;
import com.a21vianet.sample.customview.utilitly.DensityUtils;

/**
 * Created by wang.rongqiang on 2017/7/11.
 */

public class ScoreView extends View {
    private String mDescribeText = "正确率";
    private int mTempNumberText = 10;
    private String mNumberText = "00";

    @ColorInt
    private int mTextColor = 0XFFFFFFFF;

    //控件宽高
    private int mWidth;
    private int mHeight;

    //中心点
    private int mCenterLocationX;
    private int mCenterLocationY;

    private int mRadiusOne;

    private float mCircleOneWidth = 4;

    //小圆半径
    private float mCircleRadius1;
    private float mCircleRadius2;
    private float mCircleRadius3;
    private float mCircleRadius4;
    private float mCircleRadius5;

    //背景
    LinearGradient mLinearGradientBackground;

    //画笔
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCircleOnePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCircleTwoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCircleThreePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDescribePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mCircleSmallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //动画
    private ValueAnimator mNumberValueAnimator = ValueAnimator.ofFloat(0, 1);
    private ValueAnimator mCircleValueAnimator = ValueAnimator.ofFloat(0, 1);

    private float mCurrentCircleValue = 0;

    //小圆
    private Path mPathCircleOne = new Path();
    private Path mPathCircleTwo = new Path();
    private Path mPathCircleThree = new Path();
    private PathMeasure mPathMeasureCircleOne = new PathMeasure();
    private PathMeasure mPathMeasureCircleTwo = new PathMeasure();
    private PathMeasure mPathMeasureCircleThree = new PathMeasure();

    public ScoreView(Context context) {
        this(context, null);
    }

    public ScoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();

        initAnim();
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        //数字增长动画
        mNumberValueAnimator.setDuration(700);
        mNumberValueAnimator.setRepeatCount(0);
        mNumberValueAnimator.setInterpolator(new LinearInterpolator());
        mNumberValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                int number = (int) (animatedValue * mTempNumberText);
                if (number < 10) {
                    mNumberText = "0" + number;
                } else {
                    mNumberText = number + "";
                }
            }
        });

        //圆圈转动动画
        mCircleValueAnimator.setDuration(4000);
        mCircleValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCircleValueAnimator.setInterpolator(new LinearInterpolator());
        mCircleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentCircleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mCircleOnePaint.setStyle(Paint.Style.STROKE);
        mCircleOnePaint.setStrokeWidth(mCircleOneWidth);
        mCircleOnePaint.setColor(0xffffffff);

        mCircleTwoPaint.setStyle(Paint.Style.STROKE);
        mCircleTwoPaint.setStrokeWidth(mCircleOneWidth);
        mCircleTwoPaint.setColor(0xA0ffffff);

        mCircleThreePaint.setStyle(Paint.Style.STROKE);
        mCircleThreePaint.setStrokeWidth(mCircleOneWidth);
        mCircleThreePaint.setColor(0x40ffffff);

        mNumberPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), 46));
        mNumberPaint.setColor(mTextColor);
        mNumberPaint.setTextAlign(Paint.Align.RIGHT);

        mPercentPaint.setColor(mTextColor);
        mPercentPaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), 22));

        mDescribePaint.setColor(mTextColor);
        mDescribePaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), 14));
        mDescribePaint.setTextAlign(Paint.Align.CENTER);

        mCircleSmallPaint.setColor(0XFFFFFFFF);
        mCircleSmallPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 设置背景画笔
     */
    private void initBackgroundPaint() {
        int colors[] = new int[2];
        float positions[] = new float[2];

        // 第1个点
        colors[0] = 0xFF0096FF;
        positions[0] = 0;

        // 第2个点
        colors[1] = 0xFF71C9FF;
        positions[1] = 1;

        mLinearGradientBackground = new LinearGradient(
                0, 0,
                0, mHeight,
                colors,
                positions,
                Shader.TileMode.MIRROR);
        mBackgroundPaint.setShader(mLinearGradientBackground);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mCenterLocationX = w / 2;
        mCenterLocationY = h / 2;

        mRadiusOne = Math.min(w, h) / 4;

        mPathCircleOne.addCircle(mCenterLocationX, mCenterLocationY, mRadiusOne, Path.Direction.CW);
        mPathCircleTwo.addCircle(mCenterLocationX, mCenterLocationY, mRadiusOne + mRadiusOne * (1 / 4.0f), Path.Direction.CCW);
        mPathCircleThree.addCircle(mCenterLocationX, mCenterLocationY, mRadiusOne + mRadiusOne * (2 / 4.0f), Path.Direction.CW);

        mPathMeasureCircleOne.setPath(mPathCircleOne, false);
        mPathMeasureCircleTwo.setPath(mPathCircleTwo, false);
        mPathMeasureCircleThree.setPath(mPathCircleThree, false);

        //测量出布局高度在初始化 渐变
        initBackgroundPaint();

        changeSize();
    }

    private void changeSize() {
        mNumberPaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), getMinSp() * (1 / 5f)));
        mPercentPaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), getMinSp() * (1 / 10f)));
        mDescribePaint.setTextSize(DensityUtils.sp2px(MyApplication.getContext(), getMinSp() * (1 / 17f)));

        mCircleOneWidth = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 240f));
        mCircleRadius1 = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 120f));
        mCircleRadius2 = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 200f));
        mCircleRadius3 = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 240f));
        mCircleRadius4 = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 260f));
        mCircleRadius5 = DensityUtils.sp2px(MyApplication.getContext(), getMinDp() * (1 / 400f));

        mCircleOnePaint.setStrokeWidth(mCircleOneWidth);
        mCircleTwoPaint.setStrokeWidth(mCircleOneWidth);
        mCircleThreePaint.setStrokeWidth(mCircleOneWidth);
    }

    private float getMinSp() {
        return DensityUtils.px2sp(MyApplication.getContext(), Math.min(mHeight, mWidth));
    }

    private float getMinDp() {
        return DensityUtils.px2dp(MyApplication.getContext(), Math.min(mHeight, mWidth));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCircleOne(canvas);
        drawCircleTwo(canvas);
        drawCircleThree(canvas);

        drawNumber(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, mBackgroundPaint);
    }

    /**
     * 绘制第一个圆
     *
     * @param canvas
     */
    private void drawCircleOne(Canvas canvas) {
        canvas.drawPath(mPathCircleOne, mCircleOnePaint);

        float end = mPathMeasureCircleOne.getLength() * offsetValue(mCurrentCircleValue, 0.3f);
        float[] coords = new float[]{0f, 0f};
        mPathMeasureCircleOne.getPosTan(end, coords, null);
        canvas.drawCircle(coords[0], coords[1], DensityUtils.sp2px(MyApplication.getContext(), mCircleRadius1), mCircleSmallPaint);
    }

    /**
     * 绘制第二个圆
     *
     * @param canvas
     */
    private void drawCircleTwo(Canvas canvas) {
        canvas.drawPath(mPathCircleTwo, mCircleTwoPaint);

        float end = mPathMeasureCircleTwo.getLength() * offsetValue(mCurrentCircleValue, 0.1f);
        float[] coords = new float[]{0f, 0f};
        mPathMeasureCircleTwo.getPosTan(end, coords, null);
        canvas.drawCircle(coords[0], coords[1], DensityUtils.sp2px(MyApplication.getContext(), mCircleRadius3), mCircleSmallPaint);

        end = mPathMeasureCircleTwo.getLength() * offsetValue(mCurrentCircleValue, 0.5f);
        coords = new float[]{0f, 0f};
        mPathMeasureCircleTwo.getPosTan(end, coords, null);
        canvas.drawCircle(coords[0], coords[1], DensityUtils.sp2px(MyApplication.getContext(), mCircleRadius2), mCircleSmallPaint);

        end = mPathMeasureCircleTwo.getLength() * offsetValue(mCurrentCircleValue, 0.7f);
        coords = new float[]{0f, 0f};
        mPathMeasureCircleTwo.getPosTan(end, coords, null);
        canvas.drawCircle(coords[0], coords[1], DensityUtils.sp2px(MyApplication.getContext(), mCircleRadius4), mCircleSmallPaint);
    }

    /**
     * 绘制第三个圆
     *
     * @param canvas
     */
    private void drawCircleThree(Canvas canvas) {
        canvas.drawPath(mPathCircleThree, mCircleThreePaint);

        float end = mPathMeasureCircleThree.getLength() * offsetValue(mCurrentCircleValue, 0.7f);
        float[] coords = new float[]{0f, 0f};
        mPathMeasureCircleThree.getPosTan(end, coords, null);
        canvas.drawCircle(coords[0], coords[1], DensityUtils.sp2px(MyApplication.getContext(), mCircleRadius5), mCircleSmallPaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        if (mNumberText.length() > 2) {
            mNumberPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(mNumberText + "", mCenterLocationX + mRadiusOne * (3 / 7f), mCenterLocationY + mRadiusOne / 2, mNumberPaint);
        } else {
            mNumberPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mNumberText + "", mCenterLocationX, mCenterLocationY + mRadiusOne / 2, mNumberPaint);
        }
        canvas.drawText("%", mCenterLocationX + mRadiusOne / 2f, mCenterLocationY + mRadiusOne / 2, mPercentPaint);
        canvas.drawText(mDescribeText, mCenterLocationX, mCenterLocationY - mRadiusOne / 3f, mDescribePaint);
    }

    public ScoreView setNumberText(int text) {
        if (text > 999) {
            new RuntimeException("数字不能大于999");
        }
        mTempNumberText = text;
        return this;
    }

    public ScoreView setDescribeText(String text) {
        if (text.length() > 5) {
            new RuntimeException("提示字符串不能超过3位");
        }
        mDescribeText = text;
        return this;
    }

    public ScoreView setTextColor(@ColorInt int color) {
        mTextColor = color;
        mNumberPaint.setColor(mTextColor);

        mPercentPaint.setColor(mTextColor);

        mDescribePaint.setColor(mTextColor);
        return this;
    }

    public void start() {
        mNumberValueAnimator.start();
        mCircleValueAnimator.start();
    }

    private float offsetValue(float current, float v) {
        if (current + v > 1) {
            current = (current + v) - 1;
        } else {
            current = current + v;
        }
        return current;
    }
}
