package com.example.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;

import com.example.common.R;
import com.example.common.utils.BlurUtil;
import com.example.common.utils.CommonHelper;

/**
 * @author Administrator
 */
public class FullScreenDialog extends Dialog {

    protected Context context;
    /**
     * 限制内区域
     */
    private View[] innerViews;

    public FullScreenDialog(@NonNull Context context) {
        super(context, R.style.FullScreenTheme);
        this.context = context;
        init(context);
    }

    protected void init(Context context) {
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initBackground(context);
        if (getWindow() != null) {
            getWindow().setWindowAnimations(R.style.cloudDialogWindowAnim);
        }
    }

    /**
     * 设置不关闭的view
     *
     * @param innerViews 限制内区域
     * @return
     */
    public void setInnerViews(View... innerViews) {
        this.innerViews = innerViews;
    }

    private void initBackground(Context context) {
        if (getWindow() == null) {
            return;
        }
        if (!isBlurBackground()) {
            getWindow().setBackgroundDrawableResource(R.color.dialog_shadow);
            return;
        }
        if (!(context instanceof Activity)) {
            getWindow().setBackgroundDrawableResource(R.color.dialog_shadow);
            return;
        }
        Bitmap bitmap = BlurUtil.blur(context, CommonHelper.getViewBitmap(((Activity) context).getWindow().getDecorView()), getBlurScale(), getBlurRadius());
        if (bitmap != null) {
            getWindow().setBackgroundDrawable(new BitmapDrawable(context.getResources(), makeShadowBitmap(bitmap)));
        } else {
            getWindow().setBackgroundDrawableResource(R.color.dialog_shadow);
        }
    }

    private Bitmap makeShadowBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAlpha(65);
        canvas.drawRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
        return bitmap;
    }

    @Override
    public void show() {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!((Activity) context).isDestroyed()) {
                    super.show();
                }
            } else {
                super.show();
            }
        }
    }

    @Override
    public void dismiss() {
        if (context != null && !((Activity) context).isFinishing()) {
            super.dismiss();
        }
    }

    protected boolean isBlurBackground() {
        return false;
    }

    protected float getBlurScale() {
        return 8.0f;
    }

    protected int getBlurRadius() {
        return 8;
    }

    /**
     * 向上滑动动画
     *
     * @param view
     */
    protected void slideToUp(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(200);
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillEnabled(true);
        view.startAnimation(translateAnimation);
    }

    private boolean isTouchPointInView(View targetView, int xAxis, int yAxis) {
        if (targetView == null) {
            return false;
        }
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + targetView.getMeasuredWidth();
        int bottom = top + targetView.getMeasuredHeight();
        if (yAxis >= top && yAxis <= bottom && xAxis >= left && xAxis <= right) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        if (innerViews == null || innerViews.length == 0) {
            return super.dispatchTouchEvent(ev);
        }
        for (View view : innerViews) {
            if (isTouchPointInView(view, x, y)) {
                return super.dispatchTouchEvent(ev);
            }
        }
        dismiss();
        return true;
    }
}
