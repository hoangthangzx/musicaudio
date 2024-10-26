package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.st046_audioeditorandmusiceditor.R;

public class CutaudioAudio extends View {
    private static final int COLUMN_COLOR = Color.parseColor("#02051F");
    private static final String TAG = "tet";
    private static final int SIDE_COLUMN_WIDTH_DP = 16;
    private Paint columnPaint;
    private Paint progressPaint;
    private Paint selectedRangePaint;
    private float audioProgressThumb;
    private float startThumb;
    private float endThumb;
    private String startTime;
    private String endTime;
    private Bitmap progressThumbImage;
    private Bitmap startThumbImage;
    private Bitmap endThumbImage;
    private float audioDurationInMs ; // Example: 2 minutes (120,000 ms)
    private OnThumbPosition listener;
    private OnRangeChangedListener onRangeChangedListener;
    private OnSelectedRangeChangedListener onSelectedRangeChangedListener;
    private WaveformViewcutter audioCutterView;
    private MediaPlayer mediaPlayer;
    private static final long ONE_SECOND_IN_MS = 1000;
    private static final int SIDE_MARGIN_DP = 16; // side margin in dp
    private float sideMarginPx; // to store the converted pixel value
    private float sideColumnWidthPx;
    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public WaveformViewcutter getAudioCutterView() {
        return audioCutterView;
    }

    public float getStartThumb() {
        return startThumb;
    }
    public void adjustStartThumb(long milliseconds) {
        if (mediaPlayer != null) {
            long currentPositionMs = (long) (startThumb * mediaPlayer.getDuration());
            long newPositionMs = Math.max(0, Math.min(currentPositionMs + milliseconds, (long) (endThumb * mediaPlayer.getDuration() - ONE_SECOND_IN_MS)));
            float newStartThumb = (float) newPositionMs / mediaPlayer.getDuration();
            setStartThumb(newStartThumb);

        }
    }

    public void adjustEndThumb(long milliseconds) {
        if (mediaPlayer != null) {
            long currentPositionMs = (long) (endThumb * mediaPlayer.getDuration());
            long newPositionMs = Math.max((long) (startThumb * mediaPlayer.getDuration() + ONE_SECOND_IN_MS), Math.min(currentPositionMs + milliseconds, mediaPlayer.getDuration()));
            float newEndThumb = (float) newPositionMs / mediaPlayer.getDuration();
            setEndThumb(newEndThumb);
            Log.d("CutaudioAudio", "New EndThumb: " + newEndThumb);
        }
    }
    public void adjustProgressThumb(long milliseconds) {
        if (mediaPlayer != null) {
            long currentPositionMs = (long) (audioProgressThumb * mediaPlayer.getDuration());
            long newPositionMs = Math.max(0, Math.min(currentPositionMs + milliseconds, mediaPlayer.getDuration()));
            float newProgressThumb = (float) newPositionMs / mediaPlayer.getDuration();
            setAudioProgress(newProgressThumb);
            Log.d("CutaudioAudio", "New ProgressThumb: " + newProgressThumb);
        }
    }
    public void setAudioProgress(float progress) {
        if (progress >= 0 && progress <= 1) {
            this.audioProgressThumb = progress;
            invalidate();
            if (listener != null) {
                listener.onThumbPositionChanged(this.audioProgressThumb);
            }
            if (mediaPlayer != null) {
                int newPosition = (int) (this.audioProgressThumb * mediaPlayer.getDuration());
                mediaPlayer.seekTo(newPosition);
            }
            Log.d("CutaudioAudio", "ProgressThumb set to: " + progress);
        }
    }

    public void setStartThumb(float startThumb) {
        if (startThumb >= 0 && startThumb < this.endThumb) {
            this.startThumb = startThumb;
            notifyRangeChanged();
            invalidate();
            if (audioCutterView != null) {
                audioCutterView.setSelectedRange(new float[]{this.startThumb, this.endThumb});
            }
            Log.d("CutaudioAudio", "StartThumb set to: " + startThumb);
        }
    }

    public void setEndThumb(float endThumb) {
        if (endThumb <= 1 && endThumb > this.startThumb) {
            this.endThumb = endThumb;
            notifyRangeChanged();
            invalidate();
            if (audioCutterView != null) {
                audioCutterView.setSelectedRange(new float[]
                        {this.startThumb, this.endThumb});
            }
            Log.d("CutaudioAudio", "EndThumb set to: " + endThumb);
        }
    }

    public float getEndThumb() {
        return endThumb;
    }


    // Trong hàm khởi tạo hoặc một phương thức nào đó, khởi tạo audioCutterView
    public void setAudioCutterView(WaveformViewcutter audioCutterView) {
        this.audioCutterView = audioCutterView;
    }
    // Interface for thumb position changes
    public interface OnThumbPosition {
        void onThumbPositionChanged(float position);
    }
    public void setListener(OnThumbPosition listener) {
        this.listener = listener;
    }

    public void updateThumbPosition(float progress) {
        audioProgressThumb = progress;
        invalidate();
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        if (mediaPlayer != null) {
            audioDurationInMs = mediaPlayer.getDuration();
        }
    }

    public CutaudioAudio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CutaudioAudio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
    private void init() {
        sideColumnWidthPx = dpToPx(SIDE_COLUMN_WIDTH_DP);

        progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#02051F"));
//        progressPaint.setStyle(Paint.Style.FILL);

        selectedRangePaint = new Paint();
        selectedRangePaint.setStyle(Paint.Style.FILL);
        selectedRangePaint.setShader(new LinearGradient(0, 0, 0, dpToPx(163),
                Color.parseColor("#336573ED"), Color.parseColor("#3314D2E6"), Shader.TileMode.CLAMP));

        columnPaint = new Paint();
        columnPaint.setColor(COLUMN_COLOR);
        columnPaint.setStyle(Paint.Style.FILL);

        progressThumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.audio_progress_thumb);
        startThumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.audio_start_thumb);
        endThumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.audio_start_thumb);

        int thumbHeightPx = dpToPx(163);
        int thumbWidthPx = dpToPx(2);
        progressThumbImage = Bitmap.createScaledBitmap(progressThumbImage, thumbWidthPx, thumbHeightPx, true);
        startThumbImage = Bitmap.createScaledBitmap(startThumbImage, thumbWidthPx, thumbHeightPx, true);
        endThumbImage = Bitmap.createScaledBitmap(endThumbImage, thumbWidthPx, thumbHeightPx, true);

        audioProgressThumb = 0f;
        startThumb = 0.0f;
        endThumb = 1.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float totalWidth = getWidth();
        float contentWidth = totalWidth - (2 * sideColumnWidthPx);
        float height = getHeight();

        // Draw side columns
        canvas.drawRect(0, 0, sideColumnWidthPx, height, columnPaint);
        canvas.drawRect(totalWidth - sideColumnWidthPx, 0, totalWidth, height, columnPaint);

        float selectedRangeHeight = dpToPx(163);
        float selectedRangeTop = (height - selectedRangeHeight) / 2;
        float selectedRangeBottom = selectedRangeTop + selectedRangeHeight;

        // Draw selected range
        float startX = sideColumnWidthPx + startThumb * contentWidth;
        float endX = sideColumnWidthPx + endThumb * contentWidth;
        canvas.drawRect(startX, selectedRangeTop, endX, selectedRangeBottom, selectedRangePaint);

        // Draw progress thumb
        float progressX = sideColumnWidthPx + audioProgressThumb * contentWidth;
        canvas.drawBitmap(progressThumbImage, progressX - progressThumbImage.getWidth() / 2, height / 2 - progressThumbImage.getHeight() / 2, null);

        // Draw start and end thumbs
        canvas.drawBitmap(startThumbImage, startX - startThumbImage.getWidth() / 2, height / 2 - startThumbImage.getHeight() / 2, null);
        canvas.drawBitmap(endThumbImage, endX - endThumbImage.getWidth() / 2, height / 2 - endThumbImage.getHeight() / 2, null);

        // Draw time texts
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);

        float textPadding = dpToPx(2);
        float textPadding2 = dpToPx(20);
        float imagePadding = dpToPx(0);
        String progressTime = formatTime(audioProgressThumb);
        canvas.drawText(progressTime, progressX - textPaint.measureText(progressTime) / 2, height / 2 + progressThumbImage.getHeight() / 2 + textPadding2, textPaint);

        String startTime = formatTime(startThumb);
        canvas.drawText(startTime, startX - textPaint.measureText(startTime) / 2, height / 2 - startThumbImage.getHeight() / 2 - textPadding, textPaint);

        String endTime = formatTime(endThumb);
        canvas.drawText(endTime, endX - textPaint.measureText(endTime) / 2, height / 2 - endThumbImage.getHeight() / 2 - textPadding, textPaint);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        int imageHeightPx = dpToPx(12);

        image1 = Bitmap.createScaledBitmap(image1, (int) (image1.getWidth() * ((float) imageHeightPx / image1.getHeight())), imageHeightPx, true);
        image2 = Bitmap.createScaledBitmap(image2, (int) (image2.getWidth() * ((float) imageHeightPx / image2.getHeight())), imageHeightPx, true);
        image3 = Bitmap.createScaledBitmap(image3, (int) (image3.getWidth() * ((float) imageHeightPx / image3.getHeight())), imageHeightPx, true);

        canvas.drawBitmap(image1, progressX - image1.getWidth() / 2, height / 2 - progressThumbImage.getHeight() / 2 - imageHeightPx - imagePadding, null);
        canvas.drawBitmap(image2, startX - image2.getWidth() / 2, height / 2 + startThumbImage.getHeight() / 2 + imagePadding, null);
        canvas.drawBitmap(image3, endX - image3.getWidth() / 2, height / 2 + endThumbImage.getHeight() / 2 + imagePadding, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float width = getWidth();
        float progress = x / width;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Start Thumb (ms): " + getStartThumbInMs());
                Log.d(TAG, "End Thumb (ms): " + getEndThumbInMs());
                ThumbMovement(progress);
                break;
            case MotionEvent.ACTION_UP:
                handleThumbRelease();

                break;
        }

        return true;
    }
    public int getStartThumbInMs() {
        if (mediaPlayer != null) {
            return (int) (startThumb * mediaPlayer.getDuration());
        }
        return 0; // Trả về 0 nếu mediaPlayer chưa được khởi tạo
    }

    public int getEndThumbInMs() {
        if (mediaPlayer != null) {
            return (int) (endThumb * mediaPlayer.getDuration());
        }
        return 0; // Trả về 0 nếu mediaPlayer chưa được khởi tạo
    }

    private void ThumbMovement(float progress) {
        if (isProgressThumbClosest(progress)) {
            updateProgressThumb(progress);
        } else if (isStartThumbClosest(progress)) {
            updateStartThumb(progress);
        } else {
            updateEndThumb(progress);
        }

        notifyRangeChanged();
        invalidate();

    }

    private void handleThumbRelease() {
        float[] range = new float[] {startThumb, endThumb};
        audioCutterView.setSelectedRange(range);
        Log.d("CutaudioAudio", "Start Time: " + formatTime(startThumb) + ", End Time: " + formatTime(endThumb));
    }

    private boolean isProgressThumbClosest(float progress) {
        return Math.abs(progress - audioProgressThumb) < Math.abs(progress - startThumb) &&
                Math.abs(progress - audioProgressThumb) < Math.abs(progress - endThumb);
    }

    private boolean isStartThumbClosest(float progress) {
        return Math.abs(progress - startThumb) < Math.abs(progress - endThumb);
    }

    private void updateProgressThumb(float progress) {
        audioProgressThumb = Math.max(0, Math.min(progress, 1));
        if (listener != null) {
            listener.onThumbPositionChanged(audioProgressThumb);
        }
        if (mediaPlayer != null) {
            int newPosition = (int) (audioProgressThumb * mediaPlayer.getDuration());
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void updateStartThumb(float progress) {
        // Chỉ cập nhật startThumb nếu nó không vượt quá endThumb
        if (progress <= endThumb) {
            startThumb = Math.max(0, progress);
            Log.d(TAG, "updateStartThumb: ");
            startTime = formatTime(startThumb); // Cập nhật thời gian tương ứng
        }
    }

    private void updateEndThumb(float progress) {
        // Chỉ cập nhật endThumb nếu nó không thấp hơn startThumb
        if (progress >= startThumb) {
            endThumb = Math.min(1, progress);
            endTime = formatTime(endThumb); // Cập nhật thời gian tương ứng
        }
    }

    private void notifyRangeChanged() {
        if (onRangeChangedListener != null) {
            onRangeChangedListener.onRangeChanged(startThumb, endThumb);
        }
        if (onSelectedRangeChangedListener != null) {
            onSelectedRangeChangedListener.onSelectedRangeChanged(startThumb, endThumb);
        }
    }
    public float[] getThumbPositions() {
        return new float[] {startThumb, endThumb};
    }

    public void setAudioDuration(float durationInMs) {
        this.audioDurationInMs = durationInMs;
        invalidate();
    }

    public float[] getSelectedRange() {
        return new float[]{startThumb, endThumb};
    }

    private String formatTime(float position) {
        int timeInMs = (int) (position * audioDurationInMs);
        int minutes = (timeInMs / 1000) / 60;
        int seconds = (timeInMs / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setOnRangeChangedListener(OnRangeChangedListener listener) {
        this.onRangeChangedListener = listener;
    }

    public void setOnSelectedRangeChangedListener(OnSelectedRangeChangedListener listener) {
        this.onSelectedRangeChangedListener = listener;
    }
    public interface OnRangeChangedListener {
        void onRangeChanged(float start, float end);
    }

    public interface OnSelectedRangeChangedListener {
        void onSelectedRangeChanged(float start, float end);
    }
}