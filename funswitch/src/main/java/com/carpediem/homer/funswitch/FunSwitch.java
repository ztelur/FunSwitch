package com.carpediem.homer.funswitch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by homer on 16-6-11.
 */
public class FunSwitch extends View implements ValueAnimator.AnimatorUpdateListener{
    private final static float DEFAULT_WIDTH_HEIGHT_PERCENT = 0.65f;
    private final static float FACE_ANIM_MAX_FRACTION = 1.4f;
    private final static float NORMAL_ANIM_MAX_FRACTION = 1.0f;
    private float mWidthAndHeightPercent ;
    private float mWidth;
    private float mHeight;
    private float mTransitionLength;
    private Path mBackgroundPath;
    private Path mFacePath;

    //paint
    private int mOnBackgroundColor = 0xffcccccc;
    private int mOffBackgroundColor = 0xffcfcfff;

    // animation
    private ValueAnimator mValueAnimator;
    private float mAnimationFraction = 0.0f;


    private int mFaceColor = 0xffffffff;
    private int mEyeAndMouthColor = 0xffcccc00;
    private Paint mPaint;
    private float mFaceRadius;
    private float mCenterX;
    private float mCenterY;
    public FunSwitch(Context context) {
        super(context);
        init(context);
    }

    public FunSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FunSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        mWidthAndHeightPercent = DEFAULT_WIDTH_HEIGHT_PERCENT;
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * DEFAULT_WIDTH_HEIGHT_PERCENT);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //TODO：还有padding的问题偶！！！
        mWidth = w;
        mHeight = h;
        float top = 0;
        float left = 0;
        float bottom = h*0.8f; //下边预留0.2空间来画阴影
        float right = w;
        RectF backgroundRecf = new RectF(left,top,bottom,bottom);
        mBackgroundPath = new Path();
        //TODO:???????????
        mBackgroundPath.arcTo(backgroundRecf,90,180);

        backgroundRecf.left = right - bottom;
        backgroundRecf.right = right;
        mBackgroundPath.arcTo(backgroundRecf,270,180);
        mBackgroundPath.close();

        float radius = (bottom / 2) * 0.98f;
        mCenterX =(top+bottom)/2;
        mCenterY = (left+bottom)/2;
        mFaceRadius = radius;
        mTransitionLength = right - bottom;

        RectF faceRecf = new RectF(mCenterX-mFaceRadius,mCenterY-mFaceRadius,mCenterX+mFaceRadius,mCenterY+mFaceRadius);
        mFacePath = new Path();
        mFacePath.arcTo(faceRecf,90,180);
        mFacePath.arcTo(faceRecf,270,180);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawForeground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mOnBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mBackgroundPath,mPaint);
        mPaint.reset();
    }
    private void drawForeground(Canvas canvas) {

        //移动画布
        canvas.save();
        canvas.translate(getForegroundTransitionValue(),0);
        drawFace(canvas, mAnimationFraction);
        canvas.restore();
    }

    public void drawFace(Canvas canvas,float fraction) {
        mPaint.setAntiAlias(true);
        //面部背景
        mPaint.setColor(mFaceColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mFacePath,mPaint);

        translateAndClipFace(canvas,fraction);
        drawEye(canvas,fraction);
        drawMouth(canvas,fraction);
    }
    private void translateAndClipFace(Canvas canvas,float fraction) {
        //截掉超出face的部分。
        canvas.clipPath(mFacePath);

        float faceTransition ;
        //TODO：合理的转动区间，眼睛出现和消失的时间比为1：1,所以当fraction=0.25时，应该只显示侧脸
        if (fraction >=0.0f && fraction <0.5f) {
            faceTransition = fraction * mFaceRadius *4;
        } else if (fraction <=NORMAL_ANIM_MAX_FRACTION){
            faceTransition = - (NORMAL_ANIM_MAX_FRACTION - fraction) * mFaceRadius * 4;
        } else if (fraction <=(NORMAL_ANIM_MAX_FRACTION+FACE_ANIM_MAX_FRACTION)/2) {
            faceTransition =  (fraction - NORMAL_ANIM_MAX_FRACTION) * mFaceRadius * 2;
        } else {
            faceTransition = (FACE_ANIM_MAX_FRACTION - fraction) * mFaceRadius * 2;
        }

        canvas.translate(faceTransition,0);
    }

    private void drawEye(Canvas canvas,float fraction) {
        // 双眼
        float eyeRectWidth = mFaceRadius * 0.2f;
        float eyeRectHeight = mFaceRadius * 0.5f;
        float eyeOffSet = mFaceRadius * 0.15f;
        float eyeLeft = mCenterX - eyeOffSet - eyeRectWidth;
        float eyeTop = mCenterY - eyeOffSet - eyeRectHeight;

        RectF leftEye = new RectF(eyeLeft,eyeTop,eyeLeft+eyeRectWidth,eyeTop+eyeRectHeight);

        eyeLeft = mCenterX + eyeOffSet;
        RectF rightEye = new RectF(eyeLeft,eyeTop,eyeLeft + eyeRectWidth,eyeTop + eyeRectHeight);

        mPaint.setColor(mEyeAndMouthColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawOval(leftEye,mPaint);
        canvas.drawOval(rightEye,mPaint);
    }
    private void drawMouth(Canvas canvas,float fraction) {
        // 双眼
        float eyeRectWidth = mFaceRadius * 0.2f;
        float eyeOffSet = mFaceRadius * 0.15f;

        //嘴巴

        float mouthWidth = ( eyeRectWidth + eyeOffSet) * 2; //嘴的长度正好和双眼之间的距离一样
        float mouthHeight = (mFaceRadius * 0.05f);
        float mouthLeft = mCenterX - mouthWidth/2;
        float mouthTop = mCenterY + eyeOffSet;
        canvas.drawRect(mouthLeft,mouthTop,mouthLeft+mouthWidth,mouthTop+mouthHeight,mPaint);
    }

    private float getForegroundTransitionValue() {
        return  mAnimationFraction <= NORMAL_ANIM_MAX_FRACTION ? mAnimationFraction * mTransitionLength: mTransitionLength;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TEST","onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                startAnimation();
                return true;
        }
        return super.onTouchEvent(event);
    }
    private void startAnimation() {
        Log.e("TEST","startAnimation");
        mValueAnimator = ValueAnimator.ofFloat(0.0f, FACE_ANIM_MAX_FRACTION);
        mValueAnimator.setDuration(1000);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        Log.e("TEST",animation.getAnimatedValue()+" ");
        mAnimationFraction = (float)animation.getAnimatedValue();
        invalidate();
    }
}
