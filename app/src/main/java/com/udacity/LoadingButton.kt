package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }


    private var buttonText = ""

    init {
        val attrsArray = context.theme.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.text), 0, 0)

        try {
            buttonText = attrsArray.getString(0) ?: ""
        } catch (_: Exception) {
        }

        isClickable = true
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        canvas?.drawRect(0f, 0f, width, height, paint)

        val textX = width / 2
        val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2

        canvas?.drawText(buttonText, textX, textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}