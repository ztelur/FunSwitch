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

/**
 * Created by homer on 16-6-9.
 */
public class FunSwitch extends View {
    private final Paint mPaint = new Paint();
    private final Path sPath = new Path();
    private int mWidth,mHeight;
    private float sWidth,sHeight;
    private float sLeft,sTop,sRight,sBottom;
    private float sCenterX,sCenterY;

    private float mAnimationCount;
    private boolean mIsAnimationOn = false;
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
        sLeft = sTop = 0;
        sRight = mWidth;
        sBottom  = mHeight * 0.8f;
        sWidth = sRight - sLeft;
        sHeight = sBottom - sTop;
        sCenterX = (sRight + sLeft) / 2;
        sCenterY = (sBottom + sTop) / 2;

        RectF sRectF = new RectF(sLeft,sTop,sBottom,sBottom);
        sPath.arcTo(sRectF,90,180);
        sRectF.left = sRight - sBottom;
        sRectF.right = sRight;
        sPath.arcTo(sRectF,270,180);
        sPath.close();
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
        canvas.drawPath(sPath,mPaint); //绘制背景

        if (mIsAnimationOn) {
            mPaint.setColor(0xffffffff);
            final float scale = 0.98f * (1f -mAnimationCount);
            canvas.save();
            canvas.scale(scale,scale,sCenterX,sCenterY);
            canvas.drawPath(sPath,mPaint);
            canvas.restore();
            if (mAnimationCount >0) {
                mAnimationCount -= 0.1f;
                Log.e("TEST","call invalidate"+ mAnimationCount);
                invalidate();
            }
        }
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
                mAnimationCount = 1f;
                mIsAnimationOn = true;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
