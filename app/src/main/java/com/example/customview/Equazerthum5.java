package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.st046_audioeditorandmusiceditor.R;

public class Equazerthum5 extends AppCompatSeekBar {

    public Equazerthum5(Context context) {
        super(context);
        init(context);
    }

    public Equazerthum5(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Equazerthum5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Set the thumb size
        setThumbSize(R.drawable.ic_thumb_seekbar, dpToPx(22f, context), dpToPx(22f, context));
    }

    private void setThumbSize(int resourceId, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable resizedDrawable = new BitmapDrawable(getResources(), resizedBitmap);
        setThumb(resizedDrawable);
    }

    private int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
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
                // Calculate the progress based on the Y position of the touch event
                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());

                // Ensure progress stays within bounds (0 to getMax())
                progress = Math.max(0, Math.min(progress, getMax()));

                // Set the progress of the SeekBar
                setProgress(progress);
                postInvalidate(); // Redraw the SeekBar to update the thumb

                // Optionally, handle any further callbacks here
                onProgressChanged(progress);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    // Override setProgress() to ensure UI updates when progress is set programmatically
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        postInvalidate(); // Force a redraw to update the thumb and SeekBar
        onProgressChanged(progress); // Update any relevant UI or listeners
    }

    private void onProgressChanged(int progress) {
        // Optionally update any UI elements that should react to progress changes
        updateUIWithProgress(progress);
    }

    private void updateUIWithProgress(int progress) {
        // You can use this method to update other parts of your UI based on the SeekBar progress
    }
}