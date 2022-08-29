package com.example.common.widget;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

/**
 * source: https://github.com/facebook/shimmer-android
 */
public class GlowDrawable extends Drawable {

    private final int COMPONENT_COUNT = 4;

    private final Rect drawRect = new Rect();
    private final Matrix shaderMatrix = new Matrix();
    private final Paint mGlowPaint = new Paint();

    private @Nullable
    ValueAnimator mValueAnimator;

    long animationDuration = 2600L;
    float repeatDelay = 50;


    int[] colors = new int[COMPONENT_COUNT];
    float[] positions = new float[COMPONENT_COUNT];

    int baseColor = 0x00000000;//透明
    //100% FF, 90% E6, 80% CC, 70%B3, 50% 80, 40% 66, 30% 4D, 20% 33, 10% 1A
    int highlightColor = 0x66FFFFFF;//白色，不要太亮, 会有点眩晕

    float intensity = 0f;//0-1f之间，
    float dropoff = 0.5f;//0-1f之间，

    int tiltAngle = 20;//弧度

    public GlowDrawable() {
        mGlowPaint.setAntiAlias(false);
        updateColorsAndPositions();
    }

    void updateColorsAndPositions() {
        colors[0] = baseColor;
        colors[1] = highlightColor;
        colors[2] = highlightColor;
        colors[3] = baseColor;

        positions[0] = Math.max((1f - intensity - dropoff) / 2f, 0f);
        positions[1] = Math.max((1f - intensity - 0.001f) / 2f, 0f);
        positions[2] = Math.min((1f + intensity + 0.001f) / 2f, 1f);
        positions[3] = Math.min((1f + intensity + dropoff) / 2f, 1f);
    }

    public void setGlow() {
        mGlowPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        updateShader();
        updateValueAnimator();
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        final int width = bounds.width();
        final int height = bounds.height();
        drawRect.set(0, 0, width, height);
        updateShader();
        //startShimmer();
    }

    private float offset(float start, float end, float percent) {
        return start + (end - start) * percent;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final float tiltTan = (float) Math.tan(Math.toRadians(tiltAngle));
        final float translateHeight = drawRect.height() + tiltTan * drawRect.width();
        final float translateWidth = drawRect.width() + tiltTan * drawRect.height();
        final float dx;
        final float dy;

        final float animatedValue = mValueAnimator != null ? mValueAnimator.getAnimatedFraction() : 0f;

        dx = offset(-translateWidth, translateWidth, animatedValue);
        dy = 0;
        shaderMatrix.reset();
        shaderMatrix.setRotate(tiltAngle, drawRect.width() / 2f, drawRect.height() / 2f);
        shaderMatrix.postTranslate(dx, dy);
        if (mGlowPaint.getShader() != null) {
            mGlowPaint.getShader().setLocalMatrix(shaderMatrix);
        }
        canvas.drawRect(drawRect, mGlowPaint);
    }

    private void updateShader() {
        final Rect bounds = getBounds();
        final int boundsWidth = bounds.width();
        final int boundsHeight = bounds.height();
        if (boundsWidth == 0 || boundsHeight == 0) {
            return;
        }
        final int width = boundsWidth;
        final Shader shader;

        shader = new LinearGradient(
                0, 0, width, 0, colors, positions, Shader.TileMode.CLAMP);
        mGlowPaint.setShader(shader);
    }

    private void updateValueAnimator() {
        final boolean started;
        if (mValueAnimator != null) {
            started = mValueAnimator.isStarted();
            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
        } else {
            started = false;
        }

        mValueAnimator =
                ValueAnimator.ofFloat(0f, 1f + (float) (repeatDelay / animationDuration));
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setDuration(animationDuration);
        mValueAnimator.addUpdateListener(mUpdateListener);
        if (started) {
            mValueAnimator.start();
        }
    }

    private final ValueAnimator.AnimatorUpdateListener mUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //Log.d("TestShimmer", "");
                    invalidateSelf();
                }
            };

    @Override
    public void setAlpha(int alpha) {
        //nothing
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        //nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    void maybeStartShimmer(boolean start) {
        if (mValueAnimator != null
                && !mValueAnimator.isStarted()
                && start
                && getCallback() != null) {
            //随机，突显玩家个性
            mValueAnimator.setStartDelay(new Random().nextInt(800));
            mValueAnimator.start();
        }
    }


    void startShimmer() {
        if (mValueAnimator != null
                && !mValueAnimator.isStarted()
                && getCallback() != null) {
            mValueAnimator.setStartDelay(new Random().nextInt(800));
            mValueAnimator.start();
        }
    }

    void stopShimmer() {
        if (mValueAnimator != null && isShimmerStarted()) {
            mValueAnimator.cancel();
        }
    }

    public boolean isShimmerStarted() {
        return mValueAnimator != null && mValueAnimator.isStarted();
    }
}
