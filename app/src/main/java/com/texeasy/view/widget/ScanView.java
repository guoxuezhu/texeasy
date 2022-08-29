package com.texeasy.view.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.texeasy.R;


/**
 * Created by SY on 2018/5/11.
 */


public class ScanView extends View {


    private int measuredHeight;
    private int measuredWidth;
    //圆心x坐标
    private int centerX;
    //圆心y坐标
    private int centerY;
    //圆半径
    private int radius;
    //起始弧度、扫过弧度
    private float startAngle = -90, sweepAngle = 180;
    private final Paint paint = new Paint();
    private final Paint loopPaint = new Paint();
    private final float distance = 0;
    private final int strokeWidth = 10;
    private RectF oval;
    private final LinearGradient lg = new LinearGradient(0, 0, 1000, 1000, Color.parseColor("#a5d2fe"), Color.parseColor("#519aff"), Shader.TileMode.MIRROR);
    private final PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);


    public ScanView(Context context) {
        super(context);
    }


    public ScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public ScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        oval = new RectF((int) (measuredWidth / 2f) - radius - distance, (int) (measuredHeight / 2f) - radius - distance,
                (int) (measuredWidth / 2f) - radius - distance + 2 * (radius + distance), (int) (measuredHeight / 2f) - radius - distance + 2 * (radius + distance));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //创建镂空圆和遮罩bitmap
        Bitmap rectBitmap = createRectBitmap();
        Bitmap circleBitmap = createCircleBitmap();

        paint.setFilterBitmap(false);
        //保存所有的标识
        canvas.saveLayer(0, 0, measuredWidth, measuredHeight, null,
                Canvas.ALL_SAVE_FLAG);
        //画圆
        canvas.drawBitmap(circleBitmap, 0, 0, paint);
        //setXfermode   为SRC_OUT   只在源图像和目标图像不相交的地方绘制
        paint.setXfermode(xfermode);
        //画遮罩矩形
        canvas.drawBitmap(rectBitmap, 0, 0, paint);

        //画外部圆环
        loopPaint.setStrokeWidth(strokeWidth);
        loopPaint.setAntiAlias(true);
        loopPaint.setStyle(Paint.Style.STROKE);
        loopPaint.setStrokeCap(Paint.Cap.ROUND);

        //设置环形颜色
        loopPaint.setColor(Color.GRAY);
        //底层灰色圆
        canvas.drawCircle(centerX, centerY, radius + distance, loopPaint);
        //渐变色
        loopPaint.setShader(lg);
        //蓝色圆弧
        canvas.drawArc(oval, startAngle, sweepAngle, false, loopPaint);
        startAngle = (startAngle + 1) % 360;
        //回收
        rectBitmap.recycle();
        circleBitmap.recycle();
        rectBitmap = null;
        circleBitmap = null;
        //重绘
        invalidate();
    }


    /**
     * 创建镂空层圆形形状
     */
    private Bitmap createCircleBitmap() {
        Bitmap bm = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.home_bg_color));
        centerX = (int) (measuredWidth / 2f);
        centerY = (int) (measuredHeight / 2f);
        radius = (measuredHeight - strokeWidth) / 2;
        canvas.drawCircle(centerX, centerY, radius, paint);
        return bm;
    }


    /**
     * 创建遮罩层形状
     */
    private Bitmap createRectBitmap() {
        Bitmap bm = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.home_bg_color));
        canvas.drawRect(new RectF(0, 0, measuredWidth, measuredHeight), paint);
        return bm;
    }
}