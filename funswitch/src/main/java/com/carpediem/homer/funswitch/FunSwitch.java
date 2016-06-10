package com.carpediem.homer.funswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by homer on 16-6-9.
 */
public class FunSwitch extends View {
    private final Paint mPaint = new Paint();
    private final Path mBackgroundPath = new Path();
    private int mWidth,mHeight;
    private float mBackgroundWidth, mBackgroundHeight;
    private float mBackgroundLeft, mBackgroundTop, mBackgroundRight, mBackgroundBottom;
    private float mScaleCenterX, mScaleCenterY;
    private float mOvalButtonCenterX,mOvalButtonCenterY;
    private float mOvalButtonRadius;
    private float mOvalButtonStrokeWidth;
    private float mAnimationCount;
    private boolean mIsAnimationOn = false;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();


    private enum State {
        STATE_SWITCH_ON,STATE_SWITCH_OFF,STATE_SWITCH_PENDING_ON,STATE_SWITCH_PENDING_OFF
    }
    private int  mState;


    public FunSwitch(Context context) {
        super(context);
    }

    public FunSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FunSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mBackgroundLeft = mBackgroundTop = 0;
        mBackgroundRight = mWidth;
        mBackgroundBottom = mHeight * 0.8f;
        mBackgroundWidth = mBackgroundRight - mBackgroundLeft;
        mBackgroundHeight = mBackgroundBottom - mBackgroundTop;
        mScaleCenterX = mBackgroundRight - mBackgroundBottom /2;
        mScaleCenterY = mBackgroundTop + mBackgroundBottom /2;

        RectF sRectF = new RectF(mBackgroundLeft, mBackgroundTop, mBackgroundBottom, mBackgroundBottom);
        mBackgroundPath.arcTo(sRectF,90,180);
        sRectF.left = mBackgroundRight - mBackgroundBottom;
        sRectF.right = mBackgroundRight;
        mBackgroundPath.arcTo(sRectF,270,180);
        mBackgroundPath.close();

        // 白色按钮
        mOvalButtonRadius = (mBackgroundBottom - mBackgroundTop) /2;
        mOvalButtonCenterX = mBackgroundLeft + mOvalButtonRadius;
        mOvalButtonCenterY = mBackgroundTop + mOvalButtonRadius;
        mOvalButtonStrokeWidth = mOvalButtonRadius - mOvalButtonRadius * 0.9f;
        mOvalButtonRadius *= 0.9f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWith = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = (int) (measuredWith * 0.65f);
        setMeasuredDimension(measuredWith,measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffcccccc);
        canvas.drawPath(mBackgroundPath,mPaint); //绘制背景

        mPaint.setColor(0xffffffff);
        float scale = getScaleValue();
        canvas.save();
        canvas.scale(scale,scale, mScaleCenterX, mScaleCenterY);
        canvas.drawPath(mBackgroundPath,mPaint);
        canvas.restore();

        //绘制按钮
        mPaint.setColor(0xffffffff);
        //绘制白色按钮的白色部分
        canvas.translate(getTranslateValue(),0);
        canvas.drawCircle(mOvalButtonCenterX,mOvalButtonCenterY,mOvalButtonRadius,mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mOvalButtonStrokeWidth);
        mPaint.setColor(0xffdddddd);
        //绘制按钮的灰色边缘
        canvas.drawCircle(mOvalButtonCenterX,mOvalButtonCenterY,mOvalButtonRadius,mPaint);
        stepNextAnimationOrEndAnimation();

        mPaint.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getDeviceId()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                startAnimation();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startAnimation() {

        mAnimationCount = 1f;
        mIsAnimationOn = true;
        invalidate();
    }

    private float getScaleValue() {
        float value = mIsAnimationOn ? (0.98f * (1f -mAnimationCount)): 1;
        return mInterpolator.getInterpolation(value);
    }

    private float getTranslateValue() {
        float currentValue = mIsAnimationOn ? (1f- mAnimationCount) : 0;
        return mInterpolator.getInterpolation(currentValue) * (mBackgroundRight - mBackgroundBottom);

    }

    private void stepNextAnimationOrEndAnimation() {
        if (mAnimationCount > 0 && mIsAnimationOn) {
            mAnimationCount -= 0.05f;
            Log.e("TEST","call invalidate"+ mAnimationCount);
            invalidate();
        } else {
            mIsAnimationOn = false;
        }
    }

    public void setState(State state) {
        mState = state.ordinal();
    }
}
