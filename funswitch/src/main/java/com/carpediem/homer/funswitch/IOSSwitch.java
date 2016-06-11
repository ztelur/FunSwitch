package com.carpediem.homer.funswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by homer on 16-6-9.
 */
public class IOSSwitch extends View {
    private final Paint mPaint = new Paint();
    private final Path mBackgroundPath = new Path();
    private RectF mButtonRectF;
    private float mButtonOffSet;
    private int mWidth,mHeight;
    private float mBackgroundWidth, mBackgroundHeight;
    private float mBackgroundLeft, mBackgroundTop, mBackgroundRight, mBackgroundBottom;
    private float mScaleCenterX, mScaleCenterY;
    private float mOvalButtonCenterX,mOvalButtonCenterY;
    private float mOvalButtonRadius;
    private float mOvalButtonStrokeWidth;
    private float mAnimationPercent1;
    private float mAnimationPercent2;
    private boolean mIsAnimationOn = false;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private boolean mIsCanClick = true; //动画期间无法点击

    private enum State {
        STATE_SWITCH_ON,STATE_SWITCH_OFF,STATE_SWITCH_PENDING_ON,STATE_SWITCH_PENDING_OFF
    }
    private State mState;
    private State mLastState;

    public IOSSwitch(Context context) {
        super(context);
        init(context);
    }

    public IOSSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IOSSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mState = State.STATE_SWITCH_OFF;//初始化
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
        mButtonOffSet = mOvalButtonRadius * 0.3f;
        mButtonRectF = new RectF(mBackgroundLeft,mBackgroundTop,mBackgroundBottom,mBackgroundBottom);
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
        canvas.drawPath(figureButtonPath(),mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mOvalButtonStrokeWidth);
        mPaint.setColor(0xffdddddd);
        //绘制按钮的灰色边缘
        canvas.drawPath(figureButtonPath(),mPaint);
        mPaint.reset();
        stepNextAnimationOrEndAnimation();
    }

    private Path figureButtonPath() {
        float percent = getButtonOffSetPercent();
        float left = mOvalButtonCenterX - mOvalButtonRadius;
        float top = mOvalButtonCenterY - mOvalButtonRadius;
        float right = mOvalButtonCenterX + mOvalButtonRadius;
        float bottom = mOvalButtonCenterY + mOvalButtonRadius;
        Path buttonPath = new Path();
        mButtonRectF = new RectF(left,top,right,bottom);
        buttonPath.arcTo(mButtonRectF,90,180);
        mButtonRectF.left += percent * mButtonOffSet;
        mButtonRectF.right += percent * mButtonOffSet;
        buttonPath.arcTo(mButtonRectF,270,180);
        buttonPath.close();
        return buttonPath;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getDeviceId()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if (mIsCanClick) {
                    startAnimation();
                } else {
                    Log.e("TEST", mIsCanClick + " ");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startAnimation() {
        mLastState = mState;
        if (State.STATE_SWITCH_OFF.equals(mState)) {
            mState = State.STATE_SWITCH_PENDING_ON;
            mAnimationPercent2 = 0.0f;
        } else if (State.STATE_SWITCH_PENDING_ON.equals(mState)) {
            mState = State.STATE_SWITCH_ON;
            mAnimationPercent1 = 1.0f;
        } else if (State.STATE_SWITCH_ON.equals(mState)) {
            mState = State.STATE_SWITCH_PENDING_OFF;
            mAnimationPercent2 = 0.0f;
        } else if (State.STATE_SWITCH_PENDING_OFF.equals(mState)) {
            mState = State.STATE_SWITCH_OFF;
            mAnimationPercent1 = 0.0f;
        } else {
            throw new RuntimeException("no init state");
        }
        mIsAnimationOn = true;
        mIsCanClick = false;
        mInterpolator = new AccelerateDecelerateInterpolator();
        invalidate();
    }
    private float getButtonOffSetPercent() {
        if (State.STATE_SWITCH_ON.equals(mState) || State.STATE_SWITCH_OFF.equals(mState)) {
            return 0f;
        } else if (State.STATE_SWITCH_PENDING_ON.equals(mState)) {
            return mAnimationPercent2;
        } else if (State.STATE_SWITCH_PENDING_OFF.equals(mState)) {
            return mAnimationPercent2;
        }
        return 0f;
    }
    private float getScaleValue() {
        float value = mIsAnimationOn ? (0.98f * (1f - mAnimationPercent1)): 1;
        return mInterpolator.getInterpolation(value);
    }

    private float getTranslateValue() {
        if (State.STATE_SWITCH_PENDING_ON.equals(mState)) {
            return 0f;
        } else if (State.STATE_SWITCH_PENDING_OFF.equals(mState)) {
            return (mBackgroundRight-mBackgroundBottom) - mButtonOffSet * getButtonOffSetPercent();
        }
        float currentValue = mIsAnimationOn ? (1f- mAnimationPercent1) : 0;
        Log.e("TEST","currentValue is"+currentValue + " interpolator is"+ mInterpolator.getInterpolation(currentValue));
        return mInterpolator.getInterpolation(currentValue) * (mBackgroundRight - mBackgroundBottom);
    }

    private void stepNextAnimationOrEndAnimation() {
        if (!mIsAnimationOn) {
            return;
        }
        if (mAnimationPercent1 > 0 && mState.equals(State.STATE_SWITCH_ON)) {
            mAnimationPercent1 -= 0.05f;
            invalidate();
        } else if (mAnimationPercent1 <=1.0f && mState.equals(State.STATE_SWITCH_OFF)){
            mAnimationPercent1 += 0.05f;
            invalidate();
        } else if ((State.STATE_SWITCH_PENDING_ON.equals(mState) || State.STATE_SWITCH_PENDING_OFF.equals(mState))
                            && mAnimationPercent2 < 1.0f) {
            mAnimationPercent2 += 0.05f;
            invalidate();
        } else {
            mIsAnimationOn = false;
            mIsCanClick = true;
            mAnimationPercent2 = 0.0f;
        }
    }

    public void setState(State state) {
        mState = state;
    }
//
//    static final class SavedState extends BaseSavedState {
//        private State mState;
//
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        public SavedState(Parcel source) {
//            super(source);
//            mState = State.values()[source.readInt()];
//        }
//
//        @Override
//        public void writeToParcel(Parcel out, int flags) {
//            super.writeToParcel(out, flags);
//            out.writeInt(mState.ordinal());
//        }
//    }
}
