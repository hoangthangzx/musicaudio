package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

import java.util.Random;

public class AudioWaveSeekBar extends AppCompatSeekBar {

    private Paint wavePaint;
    private Paint progressPaint;
    private float[] waveHeights;
    private int barWidth = 3;
    private int barGap = 2;
    private Random random = new Random();

    public AudioWaveSeekBar(Context context) {
        super(context);
        init();
    }

    public AudioWaveSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioWaveSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setColor(0x55FFFFFF); // Semi-transparent white
        wavePaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint();
        progressPaint.setColor(0xFF2196F3); // Blue color
        progressPaint.setStyle(Paint.Style.FILL);

        generateWaveHeights();
    }


    private void generateWaveHeights() {
        int numBars = 200;
        waveHeights = new float[numBars];
        float centerY = 0.5f;
        float baseAmplitude = 0.2f;
        float maxAmplitude = 0.4f;

        // Tạo sóng cơ bản
        for (int i = 0; i < numBars; i++) {
            float phase = (float) i / numBars * 4 * (float) Math.PI;
            waveHeights[i] = (float) Math.sin(phase) * baseAmplitude;
        }

        // Thêm nhiễu và biến đổi biên độ
        for (int i = 0; i < numBars; i++) {
            float noise = (random.nextFloat() * 2 - 1) * 0.1f;
            float amplitudeVariation = random.nextFloat() * (maxAmplitude - baseAmplitude);
            waveHeights[i] = (waveHeights[i] + noise) * (baseAmplitude + amplitudeVariation);

            // Giữ giá trị trong khoảng [-0.5, 0.5] để có thể nhấp nhô cả trên và dưới
            waveHeights[i] = Math.max(-0.5f, Math.min(0.5f, waveHeights[i]));
        }

        // Làm mượt dạng sóng
        float[] smoothedHeights = new float[numBars];
        for (int i = 0; i < numBars; i++) {
            float sum = 0;
            int count = 0;
            for (int j = Math.max(0, i - 2); j <= Math.min(numBars - 1, i + 2); j++) {
                sum += waveHeights[j];
                count++;
            }
            smoothedHeights[i] = sum / count;
        }
        waveHeights = smoothedHeights;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int progressWidth = (int) (width * (getProgress() / (float) getMax()));

        for (int i = 0; i < waveHeights.length; i++) {
            float left = i * (barWidth + barGap);
            float center = height / 2f;
            float top = center - (height * waveHeights[i] / 2f);
            float right = left + barWidth;
            float bottom = center + (height * waveHeights[i] / 2f);

            if (left < progressWidth) {
                canvas.drawRect(left, top, right, bottom, progressPaint);
            } else {
                canvas.drawRect(left, top, right, bottom, wavePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int progress = (int) (getMax() * event.getX() / getWidth());
                setProgress(progress);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}