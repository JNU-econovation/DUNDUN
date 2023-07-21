package com.project981.dundun.view.calendar

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.project981.dundun.R
import com.project981.dundun.model.dto.CalendarDotDTO
import kotlin.math.min

class CustomCalendarItem @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private lateinit var parent: CustomCalendar
    private val basePaint = Paint().apply {
        color = resources.getColor(R.color.base_primary_color)
    }
    private val todayPaint = Paint().apply {
        color = resources.getColor(R.color.base_primary_color)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val textPaint = Paint().apply {
        textSize = 30f
        textAlign = Paint.Align.CENTER
        typeface = resources.getFont(R.font.base_font)
    }
    private var iconPaint = Paint().apply {
        color = resources.getColor(R.color.base_calendar_dot_color)
    }
    private var iconSize = 7f
    private var startAnimator: ValueAnimator? = null
    private var endAnimator: ValueAnimator? = null
    private var circleSize = 0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }
    private var maxCircleSize = 0f
    private var isWeek = false
    private var weekText = ""
    private var startAlpha = 0
        set(value) {
            field = value
            textPaint.alpha = value
            iconPaint.alpha = value
            todayPaint.alpha = value
            postInvalidateOnAnimation()
        }
    private var dayNumber = 0
    private var eventList = listOf<CalendarDotDTO>()
    private var isToday = false
        set(value) {
            if (textPaint.color != resources.getColor(R.color.base_reverse_font_color) && value) {
                textPaint.color = resources.getColor(R.color.base_primary_color)
            }

            field = value
        }

    init {
        this.post {
            maxCircleSize = min(height, width) / 10f * 4f
            iconSize = min(height, width) / 10f * 1f
            textPaint.textSize = min(height, width) / 10f * 4f

            startAnimator = ValueAnimator.ofFloat(0f, maxCircleSize).apply {
                addUpdateListener {
                    circleSize = it.animatedValue as Float
                    textPaint.color = Color.WHITE
                }
                duration = CIRCLE_DURATION
                repeatCount = 0
                interpolator = AccelerateDecelerateInterpolator()
            }

            endAnimator = ValueAnimator.ofFloat(maxCircleSize, 0f).apply {
                addUpdateListener {
                    circleSize = it.animatedValue as Float
                    if (isToday) {
                        textPaint.color = resources.getColor(R.color.base_primary_color)
                    } else {
                        textPaint.color = resources.getColor(R.color.base_font_color)
                    }
                }
                duration = CIRCLE_DURATION
                repeatCount = 0
                interpolator = AccelerateDecelerateInterpolator()
            }
        }
    }

    //반드시 실행 되어야 함.
    fun setParent(nowParent: CustomCalendar) {
        parent = nowParent
    }

    override fun onDraw(canvas: Canvas?) {
        if (isWeek) {
            canvas?.drawText(
                weekText,
                width / 2f,
                ((height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
                textPaint
            )
            return
        }

        if (dayNumber <= 0) return

        if (isToday) {
            canvas?.drawCircle(width / 2f, height / 2f, maxCircleSize, todayPaint)
        }
        canvas?.drawCircle(width / 2f, height / 2f, circleSize, basePaint)
        if (eventList.isNotEmpty()) {
            canvas?.drawCircle(width / 2f, height / 2f - maxCircleSize, iconSize, iconPaint)
        }
        canvas?.drawText(
            dayNumber.toString(),
            width / 2f,
            ((height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (dayNumber <= 0 || isWeek) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val focused = parent.getFocusedItem()
                if (focused != null) {
                    if (focused == this) return true
                    focused.endAnima()
                }
                parent.setFocusedItem(this, eventList)
                endAnimator?.cancel()
                startAnimator?.start()
            }
        }

        return true
    }

    fun change(newDay: Int, newList: List<CalendarDotDTO>, today: Boolean) {
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(this, "rotationY", 0f, 360f)
        animator.duration = FLIP_DURATION
        animator.start()

        isToday = today
        dayNumber = newDay
        eventList = newList
        ValueAnimator.ofInt(0, 255).apply {
            addUpdateListener {
                startAlpha = it.animatedValue as Int
            }
            duration = FLIP_DURATION
            repeatCount = 0
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
        invalidate()
    }

    private fun endAnima() {
        startAnimator?.cancel()
        endAnimator?.start()
    }

    fun setWeek(week: String, nowWeek: Int) {
        weekText = week
        isWeek = true
        textPaint.color = when (nowWeek) {
            0 -> {
                resources.getColor(R.color.base_calendar_red)
            }
            6 -> {
                resources.getColor(R.color.base_calendar_blue)
            }
            else -> {
                resources.getColor(R.color.base_font_color)
            }
        }
    }


    companion object{
        const val FLIP_DURATION = 700L
        const val CIRCLE_DURATION = 100L
    }
}