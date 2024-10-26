package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class WaveformViewcutter extends View {
    private float[] amplitudes; // Mảng lưu dữ liệu độ cao cột
    private Paint paint;
    private static final float MIN_COLUMN_HEIGHT_DP = 4f;  // Chiều cao tối thiểu cho cột không có biên độ
    private static final float MAX_COLUMN_HEIGHT_DP = 80f; // Chiều cao tối đa cho cột có biên độ cao nhất
    private float maxAmplitude = 0f; // Biên độ lớn nhất trong dữ liệu
    private float data;
    private float[] selectedRange;
    private int[] columnColors;  // Mảng lưu màu cho từng cột

    public float[] getSelectedRange() {
        return selectedRange;
    }
    public void setSelectedRange(float[] selectedRange) {
        // Lưu lại phạm vi trước đó
        int oldStartIndex = (int) (this.selectedRange[0] * amplitudes.length);
        int oldEndIndex = (int) (this.selectedRange[1] * amplitudes.length);

        this.selectedRange = selectedRange;

        // Chuyển đổi phạm vi mới thành index trong mảng amplitudes
        double newStartIndex = selectedRange[0] * amplitudes.length + 0.25683084;
        double newEndIndex = selectedRange[1] * amplitudes.length + 0.35683084;
        for (int i = 0; i < amplitudes.length; i++) {
            if (i >= oldStartIndex && i <= oldEndIndex) {
                columnColors[i] = Color.WHITE;  // Reset màu cũ
            }
            if (i >= newStartIndex && i < newEndIndex) { // Sử dụng < thay cho <= để tránh tràn mảng
                columnColors[i] = Color.parseColor("#3DA2E9");  // Màu mới cho cột trong phạm vi mới
            } else {
                columnColors[i] = Color.WHITE; // Đặt lại màu cho cột không trong phạm vi
            }
        }


        // Thay đổi màu sắc cột ngoài phạm vi cũ và trong phạm vi mới
        for (int i = 0; i < amplitudes.length; i++) {
            if (i >= oldStartIndex && i <= oldEndIndex) {
                columnColors[i] = Color.WHITE;  // Reset màu cũ
            }
            if (i >= newStartIndex && i <= newEndIndex) {
                columnColors[i] = Color.CYAN;  // Màu mới cho cột trong phạm vi mới
            }
        }

        // Vẽ lại chỉ phần thay đổi
        invalidate(); // Vẽ lại view với màu mới
        Log.d("CutaudioAudio", "StartThumb: " + selectedRange[0] + ", EndThumb: " + selectedRange[1]); // Log ra để kiểm tra
    }

    public WaveformViewcutter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        selectedRange = new float[]{0f, 1f};
        paint = new Paint();
        paint.setStrokeWidth(2f); // Độ rộng cột
    }

    public void setAudioData(float[] amplitudes) {
        this.amplitudes = amplitudes;
        columnColors = new int[amplitudes.length]; // Mảng màu có cùng kích thước với số cột

        // Tìm biên độ lớn nhất trong dữ liệu
        maxAmplitude = 0;
        for (float amplitude : amplitudes) {
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
            }
        }

        // Khởi tạo tất cả cột với màu trắng ban đầu
        for (int i = 0; i < columnColors.length; i++) {
            columnColors[i] = Color.WHITE;
        }
        invalidate(); // Vẽ lại view
    }

    public void setData(float data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (amplitudes == null || maxAmplitude == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        // Số lượng cột và khoảng cách giữa các cột
        int numColumns = amplitudes.length;
        int spacing = 4; // Khoảng cách giữa các cột, bạn có thể điều chỉnh giá trị này
        int totalSpacing = spacing * (numColumns - 1); // Tổng khoảng cách

        // Tính toán chiều rộng của từng cột
        int columnWidth = (width - totalSpacing) / numColumns; // Chiều rộng cột

        // Chuyển đổi dp sang px
        float minHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());
        float maxHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_COLUMN_HEIGHT_DP, getResources().getDisplayMetrics());

        for (int i = 0; i < numColumns; i++) {
            float amplitude = amplitudes[i];

            // Chuẩn hóa biên độ theo chiều cao tối đa
            float normalizedHeight = (amplitude / maxAmplitude) * maxHeightPx;

            // Đảm bảo cột không có biên độ sẽ có chiều cao tối thiểu
            if (normalizedHeight < minHeightPx) {
                normalizedHeight = minHeightPx;
            }

            // Vẽ cột lên canvas
            float left = i * (columnWidth + spacing); // Tính toán vị trí bên trái của cột
            float top = (height / 2) - (normalizedHeight / 2);
            float bottom = (height / 2) + (normalizedHeight / 2);

            paint.setColor(columnColors[i]);
            canvas.drawLine(left, top, left, bottom, paint);
        }
    }

}
