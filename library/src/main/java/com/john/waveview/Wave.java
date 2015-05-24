package com.john.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

// y=Asin(ωx+φ)+k
class Wave extends View {

    private final int WAVE_HEIGHT_LARGE = 36;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;

    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1.2f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final float WAVE_HZ_FAST = 0.13f;
    private final float WAVE_HZ_NORMAL = 0.06f;
    private final float WAVE_HZ_SLOW = 0.05f;

    public final int DEFAULT_BLOW_WAVE_ALPHA = 255;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mBlowWavePath = new Path();
    private Paint mBlowWavePaint = new Paint();

    private int mBlowWaveColor;

    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    // wave animation
    private float mBlowOffset;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;
    private boolean mStopped = false;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBlowWavePath, mBlowWavePaint);
//        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
    }

    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        mBlowOffset = mWaveHeight * 0.4f;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mWaveHeight * 2);
        setLayoutParams(params);
    }

    private void stopWave() {

        mStopped = true;
        removeCallbacks(mRefreshProgressRunnable);
    }

    private void restartWave() {

        postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d("Wave", "run");

//                removeCallbacks(mRefreshProgressRunnable);
                mStopped = false;
                removeCallbacks(mRefreshProgressRunnable);
                mRefreshProgressRunnable = new RefreshProgressRunnable();
                post(mRefreshProgressRunnable);
            }
        }, 200);
    }

    public void initializePainters() {

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case WaveView.MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case WaveView.LARGE:
                return getResources().getDimensionPixelSize(R.dimen.wave_height);
            case WaveView.MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HZ_FAST;
            case WaveView.MIDDLE:
                return WAVE_HZ_NORMAL;
            case WaveView.LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    private void calculatePath() {

        if (isStopped()) return;

        mBlowWavePath.reset();

        getWaveOffset();

        float y;

        mBlowWavePath.moveTo(left, bottom);

        for (float x = 0; x <= mMaxRight; x += X_SPACE) {

            y = (float) (
                    mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight
            );

            mBlowWavePath.lineTo(x, y);
        }

        mBlowWavePath.lineTo(right, bottom);
    }

    private boolean isStopped() {
        return mStopped;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        Log.d("Wave", "onWindowVisibilityChanged - visibility: "
                + visibility);

        if (GONE == visibility) {

            stopWave();
            removeCallbacks(mRefreshProgressRunnable);

        } else {

            if (isStopped()) {

                Log.d("Wave", "onWindowVisibilityChanged ONRESTART");
                restartWave();
                return;
            }

            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        Log.d("Wave", "onWindowFocusChanged focus: " + hasWindowFocus);

        if (hasWindowFocus) {

            if (mWaveLength == 0) {

                startWave();
            }
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom();
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {

                long start = System.currentTimeMillis();

                calculatePath();
                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);

                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}
