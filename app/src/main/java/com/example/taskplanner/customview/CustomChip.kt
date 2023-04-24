
package com.example.taskplanner.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.taskplanner.R
import com.example.taskplanner.utils.spToPx
import com.example.taskplanner.utils.toPx
import kotlin.math.roundToInt

class CustomChip(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var backgroundColor = Color.LTGRAY
    private var backgroundActiveColor = Color.BLUE
    private var textColor = Color.BLACK
    private var activeTextColor = Color.WHITE
    private var activeInfoTextColor = Color.WHITE
    private var infoTextColor = Color.BLACK
    private var activeInfoBackgroundColor = Color.GREEN
    private var infoBackgroundColor = Color.RED
    private var textSize = 16.spToPx.toFloat()

    private var roundedCorner = 4f
    private var iconSize = 20.toPx.toFloat()
    private var iconTint = Color.RED
    private var isChecked = false
    private var isAdd = false
    private var gap = 16f

    private var text = "Hello"
    private var infoText = "77"
    private var infoTextRadius = 10f
    private var infoTextSize = 10f

    private lateinit var iconDrawable: Drawable




    // paints
    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = backgroundColor
    }

    private val textPaint = Paint().apply {
        color = textColor
        textSize = textSize
    }

    private val infoTextPaint = Paint().apply {
        color = infoTextColor
        textSize = infoTextSize
    }

    private val infoBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = infoBackgroundColor
    }



    init {
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet){
        // getting all attributes from xml
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomChip, 0,0)

        // extracting values
        backgroundColor = typedArray.getColor(R.styleable.CustomChip_backgroundColor, backgroundColor)
        backgroundActiveColor = typedArray.getColor(R.styleable.CustomChip_activeBackgroundColor, backgroundActiveColor)
        textColor = typedArray.getColor(R.styleable.CustomChip_textColor, textColor)
        activeTextColor = typedArray.getColor(R.styleable.CustomChip_activeTextColor, activeTextColor)
        textSize = typedArray.getDimension(R.styleable.CustomChip_textSize, textSize)

        infoBackgroundColor = typedArray.getColor(R.styleable.CustomChip_infoBackgroundColor, infoBackgroundColor)
        activeInfoBackgroundColor = typedArray.getColor(R.styleable.CustomChip_activeInfoBackgroundColor, activeInfoBackgroundColor)
        infoTextColor = typedArray.getColor(R.styleable.CustomChip_infoTextColor, infoTextColor)
        activeInfoTextColor = typedArray.getColor(R.styleable.CustomChip_activeInfoTextColor, activeInfoTextColor)
        infoTextSize = typedArray.getDimension(R.styleable.CustomChip_infoTextSize, infoTextSize)

        roundedCorner = typedArray.getDimension(R.styleable.CustomChip_cornerRadius, roundedCorner)
        text = typedArray.getString(R.styleable.CustomChip_text)?: text
        infoText = typedArray.getString(R.styleable.CustomChip_infoText)?:infoText
        infoTextRadius = typedArray.getDimension(R.styleable.CustomChip_infoBackgroundRadius, infoTextRadius)
        gap = typedArray.getDimension(R.styleable.CustomChip_gap, gap)
        isChecked = typedArray.getBoolean(R.styleable.CustomChip_isChecked, isChecked)
        isAdd = typedArray.getBoolean(R.styleable.CustomChip_isIcon, isAdd)
        iconTint = typedArray.getColor(R.styleable.CustomChip_iconTint, iconTint)

        setupXml()


        // TypedArray objects are shared and must be recycled.
        typedArray.recycle()
    }

    private fun createDrawable(){
        iconDrawable = AppCompatResources.getDrawable(context, R.drawable.icon_add)!!.mutate()
        iconDrawable.colorFilter = PorterDuffColorFilter(iconTint, PorterDuff.Mode.SRC_IN)

    }


    private fun calculateContentWidth(): Int{
        return if(isAdd){
            iconSize.toInt() + paddingLeft + paddingRight
        }else{
            (textPaint.measureText(text) + gap + infoTextRadius * 2 + paddingLeft + paddingRight).roundToInt()
        }
    }

    private fun calculateContentHeight(): Int{

        val fontMetrics = textPaint.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        return (textHeight + paddingTop + paddingBottom).roundToInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val contentHeight = calculateContentHeight()
        // calculating icon size
        iconSize = (contentHeight - paddingTop - paddingBottom).toFloat()

        val contentWidth = calculateContentWidth()


        val desiredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            contentWidth
        }

        val desiredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            contentHeight
        }

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        setupXml()
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), roundedCorner, roundedCorner, backgroundPaint)

        if(isAdd){

            // icon
            val iconX = ((width - iconSize) / 2).roundToInt()
            iconDrawable.setBounds( iconX, paddingTop, (iconX + iconSize).toInt(), (paddingTop + iconSize).toInt())
            iconDrawable.draw(canvas)
        }else{


            // left text
            var fontMetrics = textPaint.fontMetrics
            var baselineOffset = fontMetrics.ascent
            canvas.drawText(text, paddingLeft.toFloat(), paddingTop - baselineOffset, textPaint)

            // calculate right text
            fontMetrics = infoTextPaint.fontMetrics
            baselineOffset = fontMetrics.ascent
            val textHeight = fontMetrics.descent - fontMetrics.ascent
            val infoX = textPaint.measureText(text) + paddingLeft + gap
            val infoY = height / 2 - textHeight / 2

            // right text background

            val radius = infoTextRadius
            val circleX = infoX + infoTextPaint.measureText(infoText) / 2
            canvas.drawCircle(circleX, height/2f, radius, infoBackgroundPaint)

            // right text
            canvas.drawText(infoText, infoX, infoY - baselineOffset, infoTextPaint)
        }
    }

    private fun setupXml(){

        if(isAdd){
            createDrawable()
        }

        textPaint.textSize = textSize
        infoTextPaint.textSize = infoTextSize
        if(isChecked){
            backgroundPaint.color = backgroundActiveColor
            textPaint.color = activeTextColor
            infoTextPaint.color = activeInfoTextColor
            infoBackgroundPaint.color = activeInfoBackgroundColor
        }else{
            backgroundPaint.color = backgroundColor
            textPaint.color = textColor
            infoTextPaint.color = infoTextColor
            infoBackgroundPaint.color = infoBackgroundColor
        }
    }


    // public functions

    fun setIconView(isIconView: Boolean){
        isAdd = isIconView
        invalidate()
    }

    fun setActive(isActive: Boolean){
        isChecked = isActive
        invalidate()
    }

    fun setText(text: String){
        this.text = text
        invalidate()
    }

    fun setInfoText(text: String){
        infoText = text
        invalidate()
    }

}