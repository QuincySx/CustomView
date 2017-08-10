package com.a21vianet.sample.customview.addshop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.a21vianet.sample.customview.utilitly.BezierUtility;

import java.lang.ref.WeakReference;

/**
 * Created by wang.rongqiang on 2017/6/20.
 */

public class AddShopDrawableView extends View {
    private Bitmap mBitmap;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ValueAnimator mValueAnimator;
    private OnAnimCallback mOnAnimCallback;

    private Matrix mMatrix = new Matrix();

    public AddShopDrawableView(Context context) {
        this(context, null);
    }

    public AddShopDrawableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddShopDrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        } catch (Exception e) {
        }
    }

    public void startAnim(ImageView beginview, View endview, ViewGroup rootView) {
        final WeakReference<ViewGroup> reference = new WeakReference<ViewGroup>(rootView);
        final WeakReference<ImageView> beginviewreference = new WeakReference<ImageView>(beginview);
        final WeakReference<View> endviewreference = new WeakReference<View>(endview);

        //计算图片宽高
        ViewGroup.LayoutParams layoutParams = beginviewreference.get().getLayoutParams();
        final int width = layoutParams.width;
        final int height = layoutParams.height;
        mBitmap = drawableToBitmap(beginviewreference.get().getDrawable(), width, height);

        //计算图片位置
        final int[] beginlocation = new int[2];
        final int[] endlocation = new int[2];

        beginviewreference.get().getLocationOnScreen(beginlocation);
        endviewreference.get().getLocationOnScreen(endlocation);

        //把View添加到布局 不然获取位置为0
        reference.get().addView(this);

        //减去 上边栏部分
        int[] anim = new int[2];
        getLocationOnScreen(anim);

        Log.e("====: anim", anim[0] + " " + anim[1]);

        beginlocation[0] -= anim[0];
        endlocation[0] -= anim[0];
        beginlocation[1] -= anim[1];
        endlocation[1] -= anim[1];

        int x = (beginlocation[0] + endlocation[0]) / 2;
        x -= 300;

        int y = (beginlocation[1] + endlocation[1]) / 2;

        final float[] src = {
                beginlocation[0], beginlocation[1],
                beginlocation[0] + width, beginlocation[1],
                beginlocation[0] + width, beginlocation[1] + height,
                beginlocation[0], beginlocation[1] + height
        };
        final float[] dst = {
                beginlocation[0], beginlocation[1],
                beginlocation[0] + width, beginlocation[1],
                beginlocation[0] + width, beginlocation[1] + height,
                beginlocation[0], beginlocation[1] + height
        };

        final PointF mControlPointF = new PointF(x, y);
        final PointF startValue = new PointF(beginlocation[0], beginlocation[1]);
        final PointF endValue = new PointF(endlocation[0], endlocation[1]);

        //计算贝塞尔曲线做动画
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(600);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float animatedValue = (Float) animation.getAnimatedValue();

                PointF pointF = BezierUtility.CalculateBezierPointForQuadratic(animatedValue,
                        startValue, mControlPointF, endValue);

                mMatrix.reset();
                mMatrix.setPolyToPoly(src, 0, dst, 0, 2);
                mMatrix.preTranslate(pointF.x, pointF.y);

                if (animatedValue < 0.8) {
                    mMatrix.preScale(1 - animatedValue / 2, 1 - animatedValue / 2);
                } else {
                    mMatrix.preScale(0.6f, 0.6f);
                }

                invalidate();
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mOnAnimCallback != null) {
                    mOnAnimCallback.onAnimStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                reference.get().removeView(AddShopDrawableView.this);
                if (mOnAnimCallback != null) {
                    mOnAnimCallback.onAnimEnd();
                }
                mBitmap = null;
                reference.clear();
                beginviewreference.clear();
                endviewreference.clear();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mValueAnimator.start();
    }

    public void setAnimCallback(OnAnimCallback animCallback) {
        mOnAnimCallback = animCallback;
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(
                width,
                height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public interface OnAnimCallback {
        void onAnimStart();

        void onAnimEnd();
    }
}
