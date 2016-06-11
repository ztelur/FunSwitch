package com.carpediem.homer.funswitch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by homer on 16-6-11.
 * 按钮上的笑脸，记录自己的数据，并且把字节画到canvas上去
 */
public class SmileFace {
    private int mFaceColor = 0xffffffff;
    private int mEyeAndMouthColor = 0xffcccccc;
    private Paint mPaint;
    private float mFaceRadius;
    private float mCenterX;
    private float mCenterY;
    public SmileFace(float x,float y,float radius) {
        mCenterX = x;
        mCenterY = y;
        mFaceRadius = radius;
        mPaint = new Paint();
    }
    public void setFaceColor(int color) {
        mFaceColor = color;
    }
    public void setEyeAndMouthColor(int color) {
        mEyeAndMouthColor = color;
    }

    public void draw(Canvas canvas) {
        //面部背景
        mPaint.setColor(mFaceColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX,mCenterY,mFaceRadius,mPaint);

        // 双眼
        float eyeRectWidth = mFaceRadius * 0.2f;
        float eyeRectHeight = mFaceRadius * 0.5f;
        float eyeOffSet = mFaceRadius * 0.3f;
        float eyeLeft = mCenterX - eyeOffSet - eyeRectWidth;
        float eyeTop = mCenterY - eyeOffSet - eyeRectHeight;

        RectF leftEye = new RectF(eyeLeft,eyeTop,eyeLeft+eyeRectWidth,eyeTop+eyeRectHeight);

        eyeLeft = mCenterX + eyeOffSet;
        RectF rightEye = new RectF(eyeLeft,eyeTop,eyeLeft + eyeRectWidth,eyeTop + eyeRectHeight);

        mPaint.setColor(mEyeAndMouthColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(leftEye,mPaint);
        canvas.drawOval(rightEye,mPaint);
        //嘴巴

        float mouthWidth = ( eyeRectWidth + eyeOffSet) * 2; //嘴的长度正好和双眼之间的距离一样
        float mouthHeight = (mFaceRadius * 0.05f);
        float mouthLeft = mCenterX - mouthWidth/2;
        float mouthTop = mCenterY + eyeOffSet;
        canvas.drawRect(mouthLeft,mouthTop,mouthLeft+mouthWidth,mouthTop+mouthHeight,mPaint);


    }
}
