package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Loading) {
            startLoadingAnimation()
        }
        invalidate()
    }

    private val colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
    private val colorPrimaryDark = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private val colorWhite = ContextCompat.getColor(context, R.color.white)
    private val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)

    private var paint = Paint().apply {
        color = colorPrimary
        style = Paint.Style.FILL
    }

    private var loadingBackgroundPaint = Paint().apply {
        color = colorPrimaryDark
        style = Paint.Style.FILL
    }

    private var textPaint = Paint().apply {
        color = colorWhite
        style = Paint.Style.FILL
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    private var circlePaint = Paint().apply {
        color = colorAccent
        style = Paint.Style.FILL
    }

    private var buttonText = ""
    private var backgroundColor = colorPrimary
    private var loadingColor = colorPrimaryDark
    private var loadingText = ""

    private var backgroundAnimatedValue = 0f
    private var circleAnimatedValue = 0f

    init {
        val attrsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        )

        try {
            buttonText = attrsArray.getString(R.styleable.LoadingButton_defaultText) ?: ""
            loadingText = attrsArray.getString(R.styleable.LoadingButton_loadingText) ?: ""
            backgroundColor =
                attrsArray.getColor(R.styleable.LoadingButton_color, colorPrimary)
            loadingColor = attrsArray.getColor(R.styleable.LoadingButton_loadingColor, loadingColor)
            loadingText = context.getString(R.string.loading)

            paint = Paint().apply {
                color = colorPrimary
                style = Paint.Style.FILL
            }

            loadingBackgroundPaint = Paint().apply {
                color = colorPrimaryDark
                style = Paint.Style.FILL
            }

            textPaint = Paint().apply {
                color = colorWhite
                style = Paint.Style.FILL
                textSize = 50f
                textAlign = Paint.Align.CENTER
            }

            circlePaint = Paint().apply {
                color = colorAccent
                style = Paint.Style.FILL
            }
        } finally {
            attrsArray.recycle()
        }

        isClickable = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Loading -> {
                val width = width.toFloat()
                val height = height.toFloat()

                // Draw the background rectangles
                canvas?.drawRect(0f, 0f, width, height, paint)
                canvas?.drawRect(
                    0f,
                    0f,
                    backgroundAnimatedValue,
                    height,
                    loadingBackgroundPaint
                )

                // Draw the text
                val textX = width / 2
                val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
                canvas?.drawText(loadingText, textX, textY, textPaint)

                // Arc / circle
                val arcRadius = dpToPx(16f)
                val arcMargin = dpToPx(8f)

                val textWidth = textPaint.measureText(loadingText)

                val arcLeft = textX + textWidth / 2 + arcMargin
                val arcTop = height / 2 - arcRadius
                val arcRight = arcLeft + 2 * arcRadius
                val arcBottom = arcTop + 2 * arcRadius

                canvas?.drawArc(
                    arcLeft,
                    arcTop,
                    arcRight,
                    arcBottom,
                    -90f,
                    circleAnimatedValue,
                    true,
                    circlePaint
                )

                // Check if animation is complete
                if (circleAnimatedValue == 360f && backgroundAnimatedValue == width) {
                    buttonState = ButtonState.Completed
                    invalidate()
                }
            }

            ButtonState.Completed -> {
                val width = width.toFloat()
                val height = height.toFloat()

                canvas?.drawRect(0f, 0f, width, height, paint)

                val textX = width / 2
                val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2

                canvas?.drawText(buttonText, textX, textY, textPaint)
            }

            ButtonState.Clicked -> {
            }
        }
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

    private fun dpToPx(dp: Float): Float = dp * context.resources.displayMetrics.density

    fun setLoading() {
        buttonState = ButtonState.Loading
    }

    private fun startLoadingAnimation() {
        valueAnimator = ValueAnimator.ofPropertyValuesHolder(
            PropertyValuesHolder.ofFloat("background", 0f, width.toFloat()),
            PropertyValuesHolder.ofFloat("circle", 0f, 360f)
        ).apply {
            duration = 3000

            addUpdateListener { animation ->
                backgroundAnimatedValue = (animation.getAnimatedValue("background") as? Float) ?: 0f
                circleAnimatedValue = (animation.getAnimatedValue("circle") as? Float) ?: 0f
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
        }
        valueAnimator.start()
    }

    fun setCompletedState() {
        buttonState = ButtonState.Completed
    }
}