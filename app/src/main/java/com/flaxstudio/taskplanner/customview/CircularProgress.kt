package com.flaxstudio.taskplanner.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.utils.toPx

class CircularProgress(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val defaultSeekBarTrackColor = Color.BLACK
    private val defaultSeekBarProgressTrackColor = Color.RED

    private var viewSize = 0f
    private var canvasCenter = 0f
    private var playDiscRadius = 0f
    private var seekBarRadius = 0f
    private var progressValue = 45f
    private var maxProgressValue = 0f
    private var seekBarProgressAngle = 0f


    // paints
    private val seekBarTrackPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2.toPx.toFloat()
        color = defaultSeekBarTrackColor
    }

    private val seekBarProgressTrackPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4.toPx.toFloat()
        strokeCap = Paint.Cap.ROUND
    }



    init {
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet){
        // getting all attributes from xml
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularProgress, 0,0)

        // extracting values
        seekBarTrackPaint.color = typedArray.getColor(R.styleable.CircularProgress_trackColor, defaultSeekBarTrackColor)
        seekBarProgressTrackPaint.color = typedArray.getColor(R.styleable.CircularProgress_progressColor, defaultSeekBarProgressTrackColor)

        seekBarTrackPaint.strokeWidth = typedArray.getDimension(R.styleable.CircularProgress_trackWidth, 2.toPx.toFloat())
        seekBarProgressTrackPaint.strokeWidth = seekBarTrackPaint.strokeWidth

        progressValue = typedArray.getFloat(R.styleable.CircularProgress_progress, 10f)
        maxProgressValue = typedArray.getFloat(R.styleable.CircularProgress_maxProgress, 1000f);
        // TypedArray objects are shared and must be recycled.
        typedArray.recycle()
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth.coerceAtMost(measuredHeight)

        viewSize = size.toFloat()
        canvasCenter = viewSize * 0.5f
        playDiscRadius = (size - paddingLeft - paddingRight) * 0.35f
        seekBarRadius = (size - paddingLeft - paddingRight) * 0.45f

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        seekBarProgressAngle = progressValue / maxProgressValue * 360

        canvas.drawCircle(canvasCenter, canvasCenter, seekBarRadius, seekBarTrackPaint)

        canvas.drawArc(canvasCenter - seekBarRadius , canvasCenter - seekBarRadius, canvasCenter + seekBarRadius, canvasCenter + seekBarRadius, -90f, seekBarProgressAngle, false, seekBarProgressTrackPaint)
    }


    // public functions
    /**
     * Set the maximum progress value
     */
    fun setMaxProgressValue(maxValue: Int){
        maxProgressValue = maxValue.toFloat()
    }

    /**
     * Before setting progress value, you must have to set max progress value
     */
    fun setProgressValue(value: Int){
        progressValue = value.toFloat()
        invalidate()
    }
}