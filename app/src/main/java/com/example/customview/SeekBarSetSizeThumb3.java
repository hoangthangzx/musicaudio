package com.example.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.st046_audioeditorandmusiceditor.R;

public class SeekBarSetSizeThumb3 extends AppCompatSeekBar {

    public SeekBarSetSizeThumb3(Context context) {
        super(context);
        init(context);
    }

    public SeekBarSetSizeThumb3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekBarSetSizeThumb3 (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Call method to set the thumb size
        setThumbSize(R.drawable.ic_thumb_seekbar, dpToPx(18f, context), dpToPx(18f, context));
    }

    private void setThumbSize(int resourceId, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable resizedDrawable = new BitmapDrawable(getResources(), resizedBitmap);
        setThumb(resizedDrawable);
    }

    // Method to convert dp to pixels
    private int dpToPx(float dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
