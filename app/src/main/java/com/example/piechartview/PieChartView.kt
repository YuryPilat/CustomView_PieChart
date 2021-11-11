package com.example.piechartview

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

private const val textSizeReductCoeff = 30f
private const val viewMarginCoeff = 10
private const val pieStartAngle = -90f

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var oval = RectF()
    private val pieValues = arrayListOf<Int>()
    private val sliceColors = arrayListOf(
        Color.CYAN,
        Color.YELLOW,
        Color.GREEN,
        Color.RED,
        Color.GRAY,
        Color.MAGENTA,
        Color.BLUE,
        Color.DKGRAY
    )
    private var sliceStartPoint = pieStartAngle
    private var viewCenterX = 0f
    private var viewCenterY = 0f
    private var viewRadius = 0f
    private var defaultViewMargin = 0
    private var adoptedTextSize = 0f
    private var valuesSum = 0
    private val piePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        adoptedTextSize = w/textSizeReductCoeff
        textPaint.textSize = adoptedTextSize
        viewCenterX = (width/2).toFloat()
        viewCenterY= (height/2).toFloat()
        viewRadius = (width/2).toFloat()
        oval = RectF(0f, 0f, viewCenterX + viewRadius, viewCenterY + viewRadius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val deviceWidth = MeasureSpec.getSize(widthMeasureSpec)

        val viewWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> deviceWidth
            MeasureSpec.AT_MOST -> deviceWidth
            MeasureSpec.UNSPECIFIED -> deviceWidth
            else -> deviceWidth
        }
        defaultViewMargin = viewWidth/viewMarginCoeff
        setMeasuredDimension(viewWidth-defaultViewMargin, viewWidth-defaultViewMargin)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var colorIndex = 0
        (pieValues.indices).forEach { i ->
            if (colorIndex >= sliceColors.size) colorIndex = 0
            piePaint.color = sliceColors[colorIndex]
            colorIndex++
            canvas.drawArc(oval,
                sliceStartPoint,
                countSweepAngle(pieValues[i]),
                true,
                piePaint)

            val midAngle = sliceStartPoint + countSweepAngle(pieValues[i])

            val sliceCenterX = if(pieValues.size == 1) viewCenterX
            else findSliceCenterX(
                cX = viewCenterX,
                midAngle = (midAngle+sliceStartPoint)/2,
                radius = viewRadius.roundToInt()
            )

            val sliceCenterY = if(pieValues.size == 1) viewCenterY
            else findSliceCenterY(
                cY = viewCenterY,
                midAngle = (midAngle+sliceStartPoint)/2,
                radius = viewRadius.roundToInt()
            )
            drawSliceText(
                canvas = canvas,
                text = countSlicePercent(pieValues[i]),
                sliceX = sliceCenterX,
                sliceY = sliceCenterY,
                textPaint = textPaint
            )

            sliceStartPoint += countSweepAngle(pieValues[i])
        }
    }

    private fun countSlicePercent(value: Int): String {
        return "${value*100/valuesSum}%"
    }

    private fun countSweepAngle(value : Int) : Float{
        return (value/valuesSum.toFloat() * 360)
    }

    private fun findSliceCenterX(cX: Float, midAngle: Float, radius: Int): Float {
        val rX = cX + radius * (kotlin.math.cos(Math.toRadians(midAngle.toDouble())))
        return ((rX + cX) / 2).toFloat()
    }

    private fun findSliceCenterY(cY: Float, midAngle: Float, radius: Int) : Float {
        val rY = cY + radius*(kotlin.math.sin(Math.toRadians(midAngle.toDouble())))
        return ((rY+cY)/2).toFloat()
    }

    private fun drawSliceText(canvas: Canvas, text : String, sliceX : Float, sliceY: Float, textPaint : Paint) {
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val correctedX = sliceX - textBounds.width()/2
        val correctedY = sliceY + textBounds.height()/2
        canvas.drawText(text, correctedX, correctedY, textPaint)
    }

    fun pieIsEmpty():Boolean {
        return valuesSum == 0
    }

    fun setPieData(values : ArrayList<Int>) {
        if(pieValues.size > 0) {
            pieValues.clear()
        }
        pieValues.addAll(values)
        valuesSum = pieValues.sum()
        invalidate()
    }


}