package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

public class CustomSeekbar extends AppCompatSeekBar {

    private ImotionEvent iAction;

    public CustomSeekbar(Context context) {
        super(context);
    }

    public CustomSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90.0f);
        canvas.translate(-getHeight(), 0.0f);
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

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_MOVE) {
            setProgress(getMax() - (int) (getMax() * motionEvent.getY() / getHeight()));
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