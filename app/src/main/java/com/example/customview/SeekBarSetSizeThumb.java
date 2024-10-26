package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.st046_audioeditorandmusiceditor.R;

public class SeekBarSetSizeThumb extends AppCompatSeekBar {
    private ImotionEvent iAction;

    public SeekBarSetSizeThumb(Context context) {
        super(context);
        initThumb();
    }

    public SeekBarSetSizeThumb(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThumb();
    }

    public SeekBarSetSizeThumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThumb();
    }

    // Method to initialize and set thumb size
    private void initThumb() {
        // Load the thumb drawable from resources
        Bitmap thumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_thumb_seekbar); // Replace with your drawable

        // Convert 22dp to pixels
        int sizeInDp = 22;
        int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        // Resize the bitmap to 22dp x 22dp
        Bitmap scaledThumbBitmap = Bitmap.createScaledBitmap(thumbBitmap, sizeInPx, sizeInPx, true);

        // Set the thumb drawable
        Drawable thumbDrawable = new BitmapDrawable(getResources(), scaledThumbBitmap);
        setThumb(thumbDrawable);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(height, width, oldHeight, oldWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90.0f);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }

        int action = motionEvent.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
            int progress = getMax() - (int) (getMax() * (motionEvent.getY() / getHeight()));
            setProgress(progress);
        }

        if (action == MotionEvent.ACTION_DOWN) {
            if (iAction != null) {
                iAction.down();
            }
        }

        if (action == MotionEvent.ACTION_UP) {
            if (iAction != null) {
                iAction.up();
            }
        }

        return true;
    }

    public void setImotionEvent(ImotionEvent iAction) {
        this.iAction = iAction;
    }

    public interface ImotionEvent {
        void down();
        void up();
    }
}
