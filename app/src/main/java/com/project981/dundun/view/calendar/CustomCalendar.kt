package com.project981.dundun.view.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.project981.dundun.model.dto.CalendarDotDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue

class CustomCalendar @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var queue: Queue<Temp> = LinkedList()
    private var listener: ((List<CalendarDotDTO>) -> Unit)? = null
    private var focused: CustomCalendarItem? = null
    private val weekList = listOf("S", "M", "T", "W", "T", "F", "S")
    private val monthList = listOf(0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)


    init {
        this.post {
            val linearParams = LayoutParams(LayoutParams.MATCH_PARENT, measuredHeight / 8)
            val itemParams = LayoutParams(measuredWidth / 7, LayoutParams.MATCH_PARENT)
            for (i in 0..6) {

                val temp = LinearLayout(context)
                temp.orientation = HORIZONTAL
                for (j in 0..6) {
                    val item = CustomCalendarItem(context)
                    item.setParent(this)
                    if (i == 0) {
                        item.setWeek(
                            weekList[j],
                            j
                        )
                    }
                    item.layoutParams = itemParams
                    temp.addView(item)
                }
                temp.layoutParams = linearParams
                addView(temp)
            }
        }
    }

    fun getFocusedItem(): CustomCalendarItem? {
        return focused
    }

    fun setFocusedItem(item: CustomCalendarItem, list: List<CalendarDotDTO>) {
        focused = item
        if (listener != null) {
            listener!!(list)
        }
    }


    fun addTouchCallbackListener(callback: (List<CalendarDotDTO>) -> (Unit)) {
        listener = callback
    }


    fun changedCalendar(list: List<List<CalendarDotDTO>>, year: Int, month: Int) {
        val top = this
        val startWeek = getWeekByYearAndMonth(year, month)
        val lastDay = monthList[month] - monthList[month - 1]

        val date = Date(System.currentTimeMillis())
        val today = SimpleDateFormat("M.d", Locale.getDefault()).format(date)
        queue.add(Temp(0, 1, -startWeek + 1))
        val set = mutableSetOf(-startWeek + 1)
        CoroutineScope(Dispatchers.Main).launch {
            while (!queue.isEmpty()) {
                val node: Temp = queue.peek()!!
                queue.remove()
                val item =
                    ((top.getChildAt(node.y) as LinearLayout).getChildAt(node.x) as CustomCalendarItem)

                CoroutineScope(Dispatchers.Main).launch {
                    item.change(
                        node.date,
                        if (node.date <= 0) {
                            listOf()
                        } else {
                            list[node.date]
                        },
                        today == (month.toString() + "." + node.date.toString())
                    )
                }
                if (node.x < 6 && node.date + 1 <= lastDay && set.contains(node.date + 1).not()) {
                    set.add(node.date + 1)
                    queue.add(Temp(node.x + 1, node.y, node.date + 1))
                }

                if (node.y < 7 && node.date + 7 <= 31 && set.contains(node.date + 7).not()) {
                    set.add(node.date + 7)
                    queue.add(Temp(node.x, node.y + 1, node.date + 7))
                }
                delay(10)
            }
        }

    }

    private fun getWeekByYearAndMonth(year: Int, month: Int): Int {
        var day = ((year - 1900) / 4)
        day += (year - 1900) * 365
        day += monthList[month]
        day %= 7
        day += 1
        return day
    }
}

data class Temp(
    val x: Int,
    val y: Int,
    val date: Int
)