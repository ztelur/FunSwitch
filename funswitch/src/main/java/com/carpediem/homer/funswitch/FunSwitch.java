package com.carpediem.homer.funswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by homer on 16-6-11.
 */
public class FunSwitch extends View {
    private final static float DEFAULT_WIDTH_HEIGHT_PERCENT = 0.65f;
    private float mWidthAndHeightPercent ;
    private float mWidth;
    private float mHeight;

    private Path mBackgroundPath;

    //paint
    private Paint mPaint;
    private int mOnBackgroundColor = 0xffcccccc;
    private int mOffBackgroundColor = 0xffcfcfff;
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
        RectF backgroundRecf = new RectF(top,left,bottom,bottom);
        mBackgroundPath = new Path();
        //TODO:???????????
        mBackgroundPath.arcTo(backgroundRecf,90,180);
        backgroundRecf.left = right - bottom;
        backgroundRecf.right = right;
        mBackgroundPath.arcTo(backgroundRecf,270,180);
        mBackgroundPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mOnBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mBackgroundPath,mPaint);
        mPaint.reset();
    }
    private void drawForeground(Canvas canvas) {

    }
}
