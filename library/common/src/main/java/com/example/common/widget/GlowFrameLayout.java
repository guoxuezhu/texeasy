package com.example.common.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * source: https://github.com/facebook/shimmer-android
 */
public class GlowFrameLayout extends FrameLayout {

    private GlowDrawable glowDrawable = new GlowDrawable();

    private boolean mShowShimmer = true;
    private boolean mStoppedShimmerBecauseVisibility = false;

    public GlowFrameLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GlowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GlowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int width = getWidth();
        final int height = getHeight();
        glowDrawable.setBounds(0, 0, width, height);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        glowDrawable.maybeStartShimmer(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopShimmer();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        // View's constructor directly invokes this method, in which case no fields on
        // this class have been fully initialized yet.
        if (glowDrawable == null) {
            return;
        }
        if (visibility != View.VISIBLE) {
            // GONE or INVISIBLE
            if (glowDrawable.isShimmerStarted()) {
                stopShimmer();
                mStoppedShimmerBecauseVisibility = true;
            }
        } else if (mStoppedShimmerBecauseVisibility) {
            glowDrawable.maybeStartShimmer(true);
            mStoppedShimmerBecauseVisibility = false;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mShowShimmer) {
            glowDrawable.draw(canvas);
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == glowDrawable;
    }

    private void init(Context context, AttributeSet set) {
        setWillNotDraw(false);
        glowDrawable.setCallback(this);
        glowDrawable.setGlow();
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public void showShimmer(boolean startShimmer) {
        mShowShimmer = true;
        if (startShimmer) {
            startShimmer();
        }
        invalidate();
    }

    /**
     * Sets the ShimmerDrawable to be invisible, stopping it in the process.
     */
    public void hideShimmer() {
        stopShimmer();
        mShowShimmer = false;
        invalidate();
    }

    public void startShimmer() {
        glowDrawable.startShimmer();
    }

    public void stopShimmer() {
        mStoppedShimmerBecauseVisibility = false;
        glowDrawable.stopShimmer();
    }
}
