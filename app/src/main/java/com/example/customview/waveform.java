package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.st046_audioeditorandmusiceditor.R;

public class waveform extends View {
    private float[] amplitudes; // Mảng lưu dữ liệu độ cao cột
    private Paint paint;
    private static final float MIN_COLUMN_HEIGHT_DP = 2f;  // Chiều cao tối thiểu cho cột không có biên độ
    private static final float MAX_COLUMN_HEIGHT_DP = 30f; // Chiều cao tối đa cho cột có biên độ cao nhất
    private float maxAmplitude = 0f; // Biên độ lớn nhất trong dữ liệu

    private Bitmap thumbBitmap; // Image for the draggable thumb
    private float thumbX; // X-coordinate of the thumb
    private float thumbRadius; // Radius of the thumb's touchable area

    private OnThumbPositionChangedListener listener; // Listener for thumb position changes

    public waveform(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setThumbPosition(float progress) {
        thumbX = progress * getWidth();
        invalidate();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f); // Độ rộng cột

        // Convert 30dp height and 2dp width to pixels
        float thumbHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        float thumbWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());

        // Load and scale the thumb image to the desired size
        Bitmap originalThumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumcut);
        thumbBitmap = Bitmap.createScaledBitmap(originalThumbBitmap, (int) thumbWidthPx, (int) thumbHeightPx, false);

        // Initialize the thumb's starting position in the middle
        thumbX = 0;

        // Set a radius around the thumb for easier dragging
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
    }

    public void setAudioData(float[] amplitudes) {
        this.amplitudes = amplitudes;

        // Tìm biên độ lớn nhất trong dữ liệu để chuẩn hóa chiều cao
        maxAmplitude = 0;
        for (float amplitude : amplitudes) {
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
            }
        }
        invalidate(); // Gọi lại onDraw() để vẽ lại view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (amplitudes == null || maxAmplitude == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int columnWidth = width / amplitudes.length;

        // Chuyển đổi các giá trị dp thành pixel dựa trên độ phân giải màn hình
        float minHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());
        float maxHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());

        // Determine which column the thumb is currently over
        int thumbColumnIndex = (int) (thumbX / columnWidth);

        for (int i = 0; i < amplitudes.length; i++) {
            float amplitude = amplitudes[i];

            // Chuẩn hóa biên độ theo chiều cao tối đa là 30dp (maxHeightPx)
            float normalizedHeight = (amplitude / maxAmplitude) * maxHeightPx;

            // Đảm bảo cột không có biên độ sẽ có chiều cao tối thiểu là 2 dp
            if (normalizedHeight < minHeightPx) {
                normalizedHeight = minHeightPx;
            }

            // Vẽ cả hai chiều (lên và xuống)
            float top = (height / 2) - (normalizedHeight / 2);
            float bottom = (height / 2) + (normalizedHeight / 2);

            // Change color to cyan if this column is to the left of or at the thumb
            if (i <= thumbColumnIndex) {
                paint.setColor(Color.CYAN);
            } else {
                paint.setColor(Color.WHITE);
            }

            canvas.drawLine(i * columnWidth, top, i * columnWidth, bottom, paint);
        }

        // Draw the thumb
        canvas.drawBitmap(thumbBitmap, thumbX - thumbBitmap.getWidth() / 2, height / 2 - thumbBitmap.getHeight() / 2, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Check if the touch is within the thumb's touchable area
                if (Math.abs(event.getX() - thumbX) < thumbRadius) {
                    // Update the thumb's position
                    thumbX = event.getX();
                    invalidate();

                    // Notify listener about the thumb's new position
                    if (listener != null) {
                        listener.onThumbPositionChanged(thumbX / getWidth());
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void clearAudioData() {
        this.setAudioData(new float[60]); // Set to default values or empty data
    }

    public void setOnThumbPositionChangedListener(OnThumbPositionChangedListener listener) {
        this.listener = listener;
    }

    // Interface for thumb position changes
    public interface OnThumbPositionChangedListener {
        void onThumbPositionChanged(float position);
    }
}
