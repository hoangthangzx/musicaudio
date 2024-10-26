package com.example.cutter

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RawRes
import com.example.st046_audioeditorandmusiceditor.R
import com.masoudss.lib.utils.WaveGravity
import java.io.File
import java.util.*

class WaveformSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var waveWidth: Float = 0f
    private var waveGap: Float = 0f
    private var wavePaddingTop: Int = 0
    private var wavePaddingBottom: Int = 0
    private var wavePaddingLeft: Int = 0
    private var wavePaddingRight: Int = 0
    private var waveCornerRadius: Float = 0f
    private var waveMinHeight: Float = 0f
    private var waveBackgroundColor: Int = 0
    private var waveProgressColor: Int = 0
    private var progress: Float = 0f
    private var maxProgress: Float = 0f
    private var visibleProgress: Float = 0f
    private var waveGravity: WaveGravity = WaveGravity.CENTER
    private var markerWidth: Float = 0f
    private var markerColor: Int = 0
    private var markerTextColor: Int = 0
    private var markerTextSize: Float = 0f
    private var markerTextPadding: Float = 0f
    private var sample: IntArray? = null
    private var mMaxValue: Int = 0
    private var progressBitmap: Bitmap? = null
    private var progressShader: BitmapShader? = null
    private var mCanvasWidth: Int = 0
    private var mCanvasHeight: Int = 0
    private val mWaveRect = RectF()
    private val mMarkerRect = RectF()
    private val mWavePaint = Paint()
    private val mMarkerPaint = Paint()
    private var isSeeking: Boolean = false
    private var totalAudioDuration: Float = 0.0f
    private var startTime: Float = 0.0f
    private var endTime: Float = 0.0f
    private var listener: RangeChangeListener? = null

    fun setAudioDuration(totalDuration: Float, start: Float, end: Float) {
        totalAudioDuration = totalDuration
        startTime = start
        endTime = end
        invalidate()
    }

    init {
        attrs?.let {
            val ta: TypedArray = context.obtainStyledAttributes(it, R.styleable.WaveformSeekBar)
            waveWidth = ta.getDimension(R.styleable.WaveformSeekBar_wave_width, waveWidth)
            waveGap = ta.getDimension(R.styleable.WaveformSeekBar_wave_gap, waveGap)
            wavePaddingTop = ta.getDimension(R.styleable.WaveformSeekBar_wave_padding_top, 0F).toInt()
            wavePaddingBottom = ta.getDimension(R.styleable.WaveformSeekBar_wave_padding_bottom1, 0F).toInt()
            wavePaddingLeft = ta.getDimension(R.styleable.WaveformSeekBar_wave_padding_left, 0F).toInt()
            wavePaddingRight = ta.getDimension(R.styleable.WaveformSeekBar_wave_padding_right, 0F).toInt()
            waveCornerRadius = ta.getDimension(R.styleable.WaveformSeekBar_wave_corner_radius, waveCornerRadius)
            waveMinHeight = ta.getDimension(R.styleable.WaveformSeekBar_wave_min_height, waveMinHeight)
            waveBackgroundColor = ta.getColor(R.styleable.WaveformSeekBar_wave_background_color, waveBackgroundColor)
            waveProgressColor = ta.getColor(R.styleable.WaveformSeekBar_wave_progress_color, waveProgressColor)
            progress = ta.getFloat(R.styleable.WaveformSeekBar_wave_progress, progress)
            maxProgress = ta.getFloat(R.styleable.WaveformSeekBar_wave_max_progress, maxProgress)
            visibleProgress = ta.getFloat(R.styleable.WaveformSeekBar_wave_visible_progress, visibleProgress)
            val gravity = ta.getInt(R.styleable.WaveformSeekBar_wave_gravity, WaveGravity.CENTER.ordinal)
            waveGravity = WaveGravity.values()[gravity]
            markerWidth = ta.getDimension(R.styleable.WaveformSeekBar_marker_width, markerWidth)
            markerColor = ta.getColor(R.styleable.WaveformSeekBar_marker_color, markerColor)
            markerTextColor = ta.getColor(R.styleable.WaveformSeekBar_marker_text_color, markerTextColor)
            markerTextSize = ta.getDimension(R.styleable.WaveformSeekBar_marker_text_size, markerTextSize)
            markerTextPadding = ta.getDimension(R.styleable.WaveformSeekBar_marker_text_padding, markerTextPadding)
            ta.recycle()
        }
    }

    private fun setMaxValue() {
        mMaxValue = sample?.maxOrNull() ?: 0
    }

    fun setSampleFrom(samples: IntArray) {
        this.sample = samples
        setMaxValue()
        invalidate() // Refresh the view when samples are set
    }

    fun setSampleFrom(audio: File) {
        setSampleFrom(audio.path)
    }

    fun setSampleFrom(audio: String) {
        WaveformOptions.getSampleFrom(context, audio) { result ->
            setSampleFrom(result)
        }
    }

    fun setSampleFrom(@RawRes audio: Int) {
        WaveformOptions.getSampleFrom(context, audio) { result ->
            setSampleFrom(result)
        }
    }

    fun setSampleFrom(audio: Uri) {
        WaveformOptions.getSampleFrom(context, audio) { result ->
            setSampleFrom(result)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasWidth = w
        mCanvasHeight = h
        progressBitmap = Bitmap.createBitmap(getAvailableWidth(), getAvailableHeight(), Bitmap.Config.ARGB_8888)
        progressShader = BitmapShader(progressBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (sample == null || sample!!.isEmpty()) return

        canvas.clipRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (mCanvasWidth - paddingRight).toFloat(),
            (mCanvasHeight - paddingBottom).toFloat()
        )

        val totalWaveWidth = waveGap + waveWidth
        val availableWidth = getAvailableWidth()
        val barsToDraw = (availableWidth / totalWaveWidth).toInt() // Chuyển đổi sang Int

        var step = sample!!.size / barsToDraw
        var previousWaveRight = (paddingLeft + wavePaddingLeft).toFloat()

        val (start, progressXPosition) = if (visibleProgress > 0) {
            // Chuyển đổi toInt() chính xác
            step *= (visibleProgress / maxProgress).toInt()
            previousWaveRight += (availableWidth * 0.5F) % totalWaveWidth
            previousWaveRight -= ((progress % visibleProgress) / visibleProgress) * totalWaveWidth
            val startPos = (progress * barsToDraw / visibleProgress).toInt() - 1
            startPos to (availableWidth * 0.5F)
        } else {
            0 to (availableWidth * progress / maxProgress)
        }

        // Tính toán chỉ số mẫu cho thời gian bắt đầu và kết thúc
        val startSampleIndex = (startTime * sample!!.size / totalAudioDuration)
        val endSampleIndex = (endTime * sample!!.size / totalAudioDuration)

        for (i in start until (barsToDraw + start + 3)) {
            val sampleItemPosition = (i * step).toInt()
            val waveHeight = if (sampleItemPosition in sample!!.indices && mMaxValue != 0) {
                (getAvailableHeight() - wavePaddingTop - wavePaddingBottom) * sample!![sampleItemPosition] / mMaxValue.toFloat()
            } else {
                0F
            }

            val finalWaveHeight = if (waveHeight < waveMinHeight) waveMinHeight else waveHeight
            val top = when (waveGravity) {
                WaveGravity.TOP -> paddingTop + wavePaddingTop.toFloat()
                WaveGravity.CENTER -> (paddingTop + wavePaddingTop + getAvailableHeight()) / 2F - finalWaveHeight / 2F
                WaveGravity.BOTTOM -> mCanvasHeight - paddingBottom - wavePaddingBottom - finalWaveHeight
            }

            mWaveRect.set(previousWaveRight, top, previousWaveRight + waveWidth, top + finalWaveHeight)

            // Đổi màu cột thành Cyan nếu nó nằm trong khoảng bắt đầu và kết thúc
            mWavePaint.color = if (sampleItemPosition.toFloat() in startSampleIndex..endSampleIndex) {
                Color.parseColor( "#3DA2E9") // Đặt màu cột thành cyan
            } else {
                waveBackgroundColor // Đặt lại màu nền
            }

            canvas.drawRoundRect(mWaveRect, waveCornerRadius, waveCornerRadius, mWavePaint)
            previousWaveRight += waveWidth + waveGap
        }
    }


    private fun getAvailableWidth(): Int {
        return mCanvasWidth - paddingLeft - paddingRight
    }

    private fun getAvailableHeight(): Int {
        return mCanvasHeight - paddingTop - paddingBottom
    }
}
