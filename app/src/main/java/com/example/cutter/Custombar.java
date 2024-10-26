package com.example.cutter;


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
import android.widget.TextView;
import android.widget.Toast;

import com.example.st046_audioeditorandmusiceditor.R;

public class Custombar extends View {
    private static final int COLUMN_COLOR = Color.parseColor("#02051F");
    private float sideColumnWidthPx;
    private Paint columnPaint;
    private static final int SIDE_COLUMN_WIDTH_DP = 16;
    private Paint progressPaint;
    private Paint selectedRangePaint;
    private float audioProgressThumb;
    private float startThumb;
    private float endThumb;
    private String startTime;
    private String endTime;
    private int a;
    private int b;
    private Bitmap progressThumbImage;
    private Bitmap startThumbImage;
    private Bitmap endThumbImage;
    private float audioDurationInMs ; // Example: 2 minutes (120,000 ms)
    private OnThumbPosition listener;
    private OnRangeChangedListener onRangeChangedListener;
    private OnSelectedRangeChangedListener onSelectedRangeChangedListener;
    private MediaPlayer mediaPlayer;
    private static final long ONE_SECOND_IN_MS = 1000;
    private RangeChangeListener rangeChangeListener;
    private  float newEndThumb;
    private  float newStartThumb;
    private boolean isZoomed = false;
    private float zoomFactor = 1.0f;
    private boolean isStartThumbAdjusted = false;
    private boolean isEndThumbAdjusted = false; // Khai báo biến boolean cho endThumb
    private boolean isProgressThumbAdjusted = false; // Khai báo biến boolean cho audioProgressThumb
private long newPositionMs;
private long currentPositionMs;
    private OnclickA onclickA;
    private Onclickend Onclickend;

    public void setOnclickend(Onclickend onclickend) {
        Onclickend = onclickend;
    }

    public int getA() {
        return a;
    }

    public void setOnclickA(OnclickA onclickA) {
        this.onclickA = onclickA;
    }

    public boolean isEndThumbAdjusted() {
        return isEndThumbAdjusted;
    }

    public boolean isProgressThumbAdjusted() {
        return isProgressThumbAdjusted;
    }

    public boolean isStartThumbAdjusted() {
        return isStartThumbAdjusted;
    }
    WaveformSeekBar waveformSeekBar;
    private OnSliderChangeListener sliderChangeListener;

    public interface OnSliderChangeListener {
        void onSliderChanged(float progress);
    }

    public void setOnSliderChangeListener(OnSliderChangeListener listener) {
        this.sliderChangeListener = listener;
    }

    public int geta() {
        return a;
    }

    public int getB() {
        return b;
    }

    public float getStartThumb() {
        return startThumb;
    }
    public void adjustStartThumb(long milliseconds) {
        if (mediaPlayer != null) {
            currentPositionMs = (long) (startThumb * mediaPlayer.getDuration());
            newPositionMs = Math.max(0, Math.min(currentPositionMs + milliseconds, (long) (endThumb * mediaPlayer.getDuration() - ONE_SECOND_IN_MS)));
           newStartThumb = (float) newPositionMs / mediaPlayer.getDuration();
            setStartThumb(newStartThumb);

            Log.d("TAG", "adjustStartThumb: "+newStartThumb);

        }
    }

    public void adjustEndThumb(long milliseconds) {
        if (mediaPlayer != null) {
            long currentPositionMs = (long) (endThumb * mediaPlayer.getDuration());
            long newPositionMs = Math.max((long) (startThumb * mediaPlayer.getDuration() + ONE_SECOND_IN_MS), Math.min(currentPositionMs + milliseconds, mediaPlayer.getDuration()));
            float newEndThumb = (float) newPositionMs / mediaPlayer.getDuration();

            // Kiểm tra sự thay đổi của endThumb
            if (newEndThumb != endThumb) {
                isEndThumbAdjusted = false; // Nếu có sự thay đổi
            } else {
                isEndThumbAdjusted = true; // Nếu không có sự thay đổi
            }

            setEndThumb(newEndThumb);
            Log.d("custombar", "New EndThumb: " + newEndThumb);
            Log.d("TAG", "adjustEndThumb: " + audioDurationInMs);
        }
    }

    public void adjustProgressThumb(long milliseconds) {
        if (mediaPlayer != null) {
            long currentPositionMs = (long) (audioProgressThumb * mediaPlayer.getDuration());
            long newPositionMs = Math.max(0, Math.min(currentPositionMs + milliseconds, mediaPlayer.getDuration()));
            float newProgressThumb = (float) newPositionMs / mediaPlayer.getDuration();

            // Kiểm tra sự thay đổi của audioProgressThumb
            if (newProgressThumb != audioProgressThumb) {
                isProgressThumbAdjusted = false; // Nếu có sự thay đổi
            } else {
                isProgressThumbAdjusted = true; // Nếu không có sự thay đổi
            }

            setAudioProgress(newProgressThumb);
            Log.d("custombar", "New ProgressThumb: " + newProgressThumb);
        }
    }
    public void setAudioProgress(float progress) {
        if (progress >= 0 && progress <= 1) {
            this.audioProgressThumb = progress;
            invalidate();

            if (mediaPlayer != null) {
                int newPosition = (int) (this.audioProgressThumb * mediaPlayer.getDuration());
                mediaPlayer.seekTo(newPosition);
            }
            Log.d("custombar", "ProgressThumb set to: " + progress);
        }
    }

    public void setStartThumb(float startThumb) {
        if (startThumb >= 0 && startThumb < this.endThumb) {
            this.startThumb = startThumb;
            String st =formatTime(startThumb);
            Log.d("custombar", "StartThumb set to: " + st);
            if (onclickA != null) {
                onclickA.onclick(st);
            } else {
            }

            notifyRangeChanged();
            invalidate();
//            onclickA.onclick(startThumb);

        }
    }

    public void setEndThumb(float endThumb) {
        if (endThumb <= 1 && endThumb > this.startThumb) {
            this.endThumb = endThumb;
            String end =formatTime(endThumb);
            if (onclickA != null) {
                onclickA.Onclickend(end);
            } else {
            }
            notifyRangeChanged();
            invalidate();
            Log.d("custombar", "EndThumb set to: " + endThumb);
        }
    }

    public float getEndThumb() {
        return endThumb;
    }

    // Interface for thumb position changes
    public interface OnThumbPosition {
        void onThumbPositionChanged(float position);

        void onSliderChanged(float progress);
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

    public Custombar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Custombar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sideColumnWidthPx = dpToPx(SIDE_COLUMN_WIDTH_DP);

        columnPaint = new Paint();
        columnPaint.setColor(COLUMN_COLOR); // Set the column color to transparent
        columnPaint.setStyle(Paint.Style.FILL);

        selectedRangePaint = new Paint();
        selectedRangePaint.setStyle(Paint.Style.FILL);
        selectedRangePaint.setShader(new LinearGradient(0, 0, 0, dpToPx(163),
                Color.parseColor("#336573ED"), Color.parseColor("#3314D2E6"), Shader.TileMode.CLAMP));


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
        float progressX = Math.max(startX, Math.min(endX, sideColumnWidthPx + audioProgressThumb * contentWidth));
        canvas.drawBitmap(progressThumbImage, progressX - progressThumbImage.getWidth() / 2, height / 2 - progressThumbImage.getHeight() / 2, null);

        // Draw start and end thumbs
        canvas.drawBitmap(startThumbImage, startX - startThumbImage.getWidth() / 2, height / 2 - startThumbImage.getHeight() / 2, null);
        canvas.drawBitmap(endThumbImage, endX - endThumbImage.getWidth() / 2, height / 2 - endThumbImage.getHeight() / 2, null);

        // Draw time texts
        Paint textPaint = new Paint();
        textPaint.setTextSize(30f);
        float textPadding = dpToPx(2);
        float textPadding2 = dpToPx(20);
        float imagePadding = dpToPx(0);
        applyGradientToSaveText(textPaint);

        Paint textPaint1 = new Paint();
        textPaint1.setTextSize(30f);
        applyGradientToSaveText1(textPaint1);
        String progressTime = formatTime(audioProgressThumb);
        canvas.drawText(progressTime, progressX - textPaint1.measureText(progressTime) / 2, height / 2 + progressThumbImage.getHeight() / 2 + textPadding2, textPaint1);

        String startTime = formatTime(startThumb);
        canvas.drawText(startTime, startX - textPaint.measureText(startTime) / 2, height / 2 - startThumbImage.getHeight() / 2 - textPadding, textPaint);

        String endTime = formatTime(endThumb);
        canvas.drawText(endTime, endX - textPaint.measureText(endTime) / 2, height / 2 - endThumbImage.getHeight() / 2 - textPadding, textPaint);

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        int imageHeightPx = dpToPx(10);

        image1 = Bitmap.createScaledBitmap(image1, (int) (image1.getWidth() * ((float) imageHeightPx / image1.getHeight())), imageHeightPx, true);
        image2 = Bitmap.createScaledBitmap(image2, (int) (image2.getWidth() * ((float) imageHeightPx / image2.getHeight())), imageHeightPx, true);
        image3 = Bitmap.createScaledBitmap(image3, (int) (image3.getWidth() * ((float) imageHeightPx / image3.getHeight())), imageHeightPx, true);

        canvas.drawBitmap(image1, progressX - image1.getWidth() / 2, height / 2 - progressThumbImage.getHeight() / 2 - imageHeightPx - imagePadding, null);
        canvas.drawBitmap(image2, startX - image2.getWidth() / 2, height / 2 + startThumbImage.getHeight() / 2 + imagePadding, null);
        canvas.drawBitmap(image3, endX - image3.getWidth() / 2, height / 2 + endThumbImage.getHeight() / 2 + imagePadding, null);
    }
    private void applyGradientToSaveText(Paint paint) {
        Shader textShader = new LinearGradient(0, 0, 0, paint.getTextSize(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.1 for 10% top, 1f for 100% bottom

        paint.setShader(textShader);
    }
    private void applyGradientToSaveText1(Paint paint) {
        Shader textShader = new LinearGradient(0, 0, 0, paint.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFF"), // Top color (20%)
                        Color.parseColor("#FFFFFF")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.1 for 10% top, 1f for 100% bottom

        paint.setShader(textShader);
    }
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
    //////////////////////////////////////////////////////////////////////
    // Thêm mảng để lưu vị trí view trên màn hình
    private int[] locationOnScreen = new int[2];

    // Phương thức lấy vị trí tuyệt đối của start thumb và log vị trí
    public float[] getStartThumbScreenPosition() {
        getLocationOnScreen(locationOnScreen);
        float contentWidth = getWidth() - (2 * sideColumnWidthPx);
        float startX = sideColumnWidthPx + startThumb * contentWidth;

        float[] startPosition = new float[] {
                locationOnScreen[0] + startX,  // vị trí X trên màn hình
                locationOnScreen[1] + getHeight()/2  // vị trí Y trên màn hình
        };

        // Log vị trí start thumb
        Log.d("StartThumbPosition", "Start Thumb Position: X = " + startPosition[0] + ", Y = " + startPosition[1]);

        return startPosition;
    }

    // Phương thức lấy vị trí tuyệt đối của end thumb và log vị trí
    public float[] getEndThumbScreenPosition() {
        getLocationOnScreen(locationOnScreen);
        float contentWidth = getWidth() - (2 * sideColumnWidthPx);
        float endX = sideColumnWidthPx + endThumb * contentWidth;

        float[] endPosition = new float[] {
                locationOnScreen[0] + endX,  // vị trí X trên màn hình
                locationOnScreen[1] + getHeight()/2  // vị trí Y trên màn hình
        };

        // Log vị trí end thumb
        Log.d("EndThumbPosition", "End Thumb Position: X = " + endPosition[0] + ", Y = " + endPosition[1]);

        return endPosition;
    }


    public float[][] getAllThumbScreenPositions() {
        return new float[][] {
                getStartThumbScreenPosition(),
                getEndThumbScreenPosition()
        };
    }

    public float screenXToProgress(float screenX) {
        getLocationOnScreen(locationOnScreen);
        float localX = screenX - locationOnScreen[0];
        float contentWidth = getWidth() - (2 * sideColumnWidthPx);
        return (localX - sideColumnWidthPx) / contentWidth;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float contentWidth = getWidth() - (2 * sideColumnWidthPx);
        float progress = (x - sideColumnWidthPx) / contentWidth;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (isProgressThumbClosest(progress)) {
                    // Constrain progress between startThumb and endThumb
                    audioProgressThumb = Math.max(startThumb, Math.min(endThumb, progress));
                    if (listener != null) {
                        listener.onThumbPositionChanged(audioProgressThumb);
                    }
                    if (mediaPlayer != null) {
                        int newPosition = (int) (audioProgressThumb * mediaPlayer.getDuration());
                        mediaPlayer.seekTo(newPosition);
                    }
                } else if (isStartThumbClosest(progress)) {
                    updateStartThumb(progress);
                } else {
                    updateEndThumb(progress);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;
    }

    private boolean isProgressThumbClosest(float progress) {
        return Math.abs(progress - audioProgressThumb) < Math.abs(progress - startThumb) &&
                Math.abs(progress - audioProgressThumb) < Math.abs(progress - endThumb);
    }

    private void updateStartThumb(float progress) {
        if (progress < endThumb) {
            startThumb = Math.max(0, progress);
            audioProgressThumb = Math.max(audioProgressThumb, startThumb);
            startTime = formatTime(startThumb);
            a=convertTimeToMillis(startTime);
            if (onclickA != null) {
                onclickA.onclick(startTime);
            } else {
            }

            Log.d("TAG", "updateStartThumb123: "+startTime);
            Log.d("b", "updateStartThumb: "+a);
            notifyRangeChanged();
            if (listener != null) {
                listener.onSliderChanged(audioProgressThumb);
            }
        }
    }

    private void updateEndThumb(float progress) {
        if (progress > startThumb) {
            endThumb = Math.min(1, progress);
            audioProgressThumb = Math.min(audioProgressThumb, endThumb);
            endTime = formatTime(endThumb);
            b=convertTimeToMillis(endTime);
            if (onclickA != null) {
            onclickA.Onclickend(endTime);
            } else {
            }
            Log.d("b", "updateStartThumb: "+b);
            Log.d("b", "endThumb: "+endThumb);
            notifyRangeChanged();
            if (listener != null) {
                listener.onSliderChanged(audioProgressThumb);
            }
        }
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

    private int convertTimeToMillis(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return (minutes * 60 + seconds) * 1000; // convert to milliseconds
    }


    private void notifyRangeChanged() {
        if (onRangeChangedListener != null) {
            onRangeChangedListener.onRangeChanged(startThumb, endThumb);
        }
        if (onSelectedRangeChangedListener != null) {
            onSelectedRangeChangedListener.onSelectedRangeChanged(startThumb, endThumb);
        }

    }

    public void setAudioDuration(float durationInMs) {
        this.audioDurationInMs = durationInMs;
        invalidate();
    }
    private String formatTime(float position) {
        int timeInMs = (int) (position * audioDurationInMs);
        int minutes = (timeInMs / 1000) / 60;
        int seconds = (timeInMs / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
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
    public interface OnRangeChangedListener {
        void onRangeChanged(float start, float end);
    }

    public interface OnSelectedRangeChangedListener {
        void onSelectedRangeChanged(float start, float end);
    }
    public interface OnProgress {
        void onThumbPositionChanged(float position);
        void onSliderChanged(float progress);
    }
    public interface OnclickA{
        void onclick (String startThumb);
        void Onclickend (String Endthumb);
    }
    public interface Onclickend{
//        void Onclickend (String Endthumb);
    }
}