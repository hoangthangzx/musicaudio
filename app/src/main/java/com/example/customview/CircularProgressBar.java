package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.st046_audioeditorandmusiceditor.R;

public class CircularProgressBar extends View {
    private int progress = 0;
    private Paint progressPaint;
    private Paint backgroundPaint;
    private RectF rectF;
    private boolean isPaused = true;
    private Bitmap playBitmap;
    private Bitmap pauseBitmap;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(2);
        progressPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        rectF = new RectF();

        // Load play and pause icons
        playBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mergepaush);
        pauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mergeplay);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        int left = (width - size) / 2;
        int top = (height - size) / 2;

        rectF.set(left + 5, top + 5, left + size - 5, top + size - 5);

        // Draw background (play/pause icon)
        // Chỉ vẽ bitmap đã được nạp trong init()
        Bitmap iconBitmap = isPaused ? playBitmap : pauseBitmap;
        if (iconBitmap != null) {
            canvas.drawBitmap(iconBitmap, null, rectF, null);
        }

        // Draw progress
        int startColor = 0xFF14D2E6; // #6573ED
        int endColor = 0xFF6573ED ;   // #14D2E6
        SweepGradient sweepGradient = new SweepGradient(width / 2f, height / 2f,
                new int[]{startColor, endColor},
                null);
        progressPaint.setShader(sweepGradient);

        float angle = 360 * progress / 100f;
        canvas.drawArc(rectF, -90, angle, false, progressPaint);
    }


    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        invalidate();
    }
}