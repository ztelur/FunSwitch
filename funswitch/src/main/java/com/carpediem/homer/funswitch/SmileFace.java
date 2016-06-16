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


}
