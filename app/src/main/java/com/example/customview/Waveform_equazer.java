package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.st046_audioeditorandmusiceditor.R;


public class Waveform_equazer extends View {
    private MediaPlayer mediaPlayer;

    private float[] amplitudes; // Mảng lưu dữ liệu độ cao cột
    private Paint paint;
    private static final float MIN_COLUMN_HEIGHT_DP = 2f;
    private static final float MAX_COLUMN_HEIGHT_DP = 30f;
    private float maxAmplitude = 0f; // Biên độ lớn nhất trong dữ liệu

    private Bitmap thumbBitmap; // Image for the draggable thumb
    private float thumbX; // X-coordinate of the thumb
    private float thumbRadius; // Radius of the thumb's touchable area
    private float startX = 0f; // Vị trí bắt đầu của khoảng thời gian được chọn
    private float endX = 0f; // Vị trí kết thúc của khoảng thời gian được chọn

    // Thêm hàm để cập nhật vị trí start và end
    public void setSelectedRange(float start, float end) {
        this.startX = start * getWidth();
        this.endX = end * getWidth();
        invalidate(); // Vẽ lại view để cập nhật giao diện
    }

    private OnThumbPositionChangedListener listener; // Listener for thumb position changes

    public Waveform_equazer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void setThumbPosition(float progress) {
        thumbX = progress * getWidth(); // Cập nhật vị trí thumb dựa vào tiến trình phát nhạc
        invalidate(); // Vẽ lại view để cập nhật giao diện
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
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

        // Tải ảnh từ tài nguyên và tạo BitmapShader
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.line); // Đặt ảnh của bạn ở đây
        Shader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT); // Lặp lại ảnh theo trục X và Y

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

            if (i <= thumbColumnIndex) {
                // Áp dụng ảnh bitmap vào paint
                paint.setShader(bitmapShader);
            } else {
                // Nếu ngoài phạm vi của thumb, đặt lại màu trắng
                paint.setShader(null);
                paint.setColor(Color.WHITE);
            }

            // Tô màu vùng được chọn từ start đến end
            if (i * columnWidth >= startX && i * columnWidth <= endX) {
                paint.setColor(Color.BLUE); // Ví dụ: tô màu xanh cho vùng được chọn
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

                    // Cập nhật vị trí phát lại của MediaPlayer
                    if (listener != null) {
                        float newPosition = thumbX / getWidth(); // Tính toán vị trí mới
                        listener.onThumbPositionChanged(newPosition);

                        // Cập nhật MediaPlayer theo vị trí mới
                        int newMediaPlayerPosition = (int) (newPosition * mediaPlayer.getDuration());
                        mediaPlayer.seekTo(newMediaPlayerPosition); // Cập nhật vị trí phát nhạc
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnThumbPositionChangedListener(OnThumbPositionChangedListener listener) {
        this.listener = listener;
    }

    // Interface for thumb position changes
    public interface OnThumbPositionChangedListener {
        void onThumbPositionChanged(float position);
    }
}
