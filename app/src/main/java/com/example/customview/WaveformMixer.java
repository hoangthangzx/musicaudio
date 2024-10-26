package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class WaveformMixer extends View {
    private MediaPlayer mediaPlayer;
    private int[] amplitudes; // Mảng lưu dữ liệu độ cao cột
    private Paint paint;
    private static final float MIN_COLUMN_HEIGHT_DP = 2f;
    private static final float MAX_COLUMN_HEIGHT_DP = 30f;
    private float maxAmplitude = 0f; // Biên độ lớn nhất trong dữ liệu
    private float thumbX; // X-coordinate of the thumb
    private float thumbRadius; // Radius of the thumb's touchable area
    private OnThumbPositionChangedListener listener; // Listener for thumb position changes
    private int hiddenColumns = 0; // Số cột đã bị ẩn
    private boolean canHideColumns = true; // Biến để kiểm soát việc ẩn cột

    public WaveformMixer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f); // Độ rộng cột
        thumbX = 0;

        // Set a radius around the thumb for easier dragging
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
    }

    public void setAudioData(int[] amplitudes) {
        this.amplitudes = amplitudes;
        updateMaxAmplitude();
        invalidate();
    }

    private void updateMaxAmplitude() {
        maxAmplitude = 0;
        if (amplitudes != null) {
            for (int amplitude : amplitudes) { // Sử dụng int
                if (amplitude > maxAmplitude) {
                    maxAmplitude = amplitude;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (amplitudes == null || amplitudes.length == 0 || maxAmplitude == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        float columnWidth = (float) width / (amplitudes.length + hiddenColumns);
        float minHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());
        float maxHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());

        for (int i = hiddenColumns; i < amplitudes.length; i++) {  // Bắt đầu vẽ từ cột thứ hiddenColumns
            float amplitude = amplitudes[i];
            float normalizedHeight = (amplitude / maxAmplitude) * maxHeightPx;

            if (normalizedHeight < minHeightPx) {
                normalizedHeight = minHeightPx;
            }

            float top = (height / 2) - (normalizedHeight / 2);
            float bottom = (height / 2) + (normalizedHeight / 2);
            float xPos = (i - hiddenColumns) * columnWidth;

            canvas.drawLine(xPos, top, xPos, bottom, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - thumbX) < thumbRadius) {
                    thumbX = Math.max(0, Math.min(event.getX(), getWidth()));
                    invalidate();

                    if (listener != null) {
                        float newPosition = thumbX / getWidth();
                        listener.onThumbPositionChanged(newPosition);

                        if (mediaPlayer != null) {
                            int newMediaPlayerPosition = (int) (newPosition * mediaPlayer.getDuration());
                            mediaPlayer.seekTo(newMediaPlayerPosition);
                        }
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void hideColumn() {
        if (canHideColumns && amplitudes != null && hiddenColumns < amplitudes.length) {
            hiddenColumns++; // Tăng số cột bị ẩn lên
            invalidate();

            // Kiểm tra nếu tất cả các cột đã bị ẩn
            if (hiddenColumns >= amplitudes.length) {
                canHideColumns = false; // Dừng việc ẩn cột
                reset(); // Khôi phục lại tất cả các cột
            }
        }
    }

    // Phương thức khôi phục lại trạng thái ban đầu
    public void reset() {
        hiddenColumns = 0;
        canHideColumns = false; // Khôi phục khả năng ẩn cột
        invalidate();
    }

    public int getAmplitudesCount() {
        return amplitudes != null ? amplitudes.length : 0;
    }

    public interface OnThumbPositionChangedListener {
        void onThumbPositionChanged(float position);
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setOnThumbPositionChangedListener(OnThumbPositionChangedListener listener) {
        this.listener = listener;
    }
}
