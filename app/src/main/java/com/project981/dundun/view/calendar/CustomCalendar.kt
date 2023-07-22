package com.project981.dundun.view.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.project981.dundun.model.dto.BottomDetailDTO
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
    private var listener: ((List<BottomDetailDTO>) -> Unit)? = null
    private var focused: CustomCalendarItem? = null
    private val weekList = listOf("S", "M", "T", "W", "T", "F", "S")
    private val monthList = listOf(0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365)


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

    fun setFocusedItem(item: CustomCalendarItem?, list: List<BottomDetailDTO>) {
        focused?.endAnima()
        focused = item
        if (listener != null) {
            listener!!(list)
        }
    }


    fun addTouchCallbackListener(callback: (List<BottomDetailDTO>) -> (Unit)) {
        listener = callback
    }


    fun changedCalendar(list: List<List<BottomDetailDTO>>, month: Int, year: Int) {
        val top = this
        val startWeek = getWeekByYearAndMonth(year, month)
        val lastDay =
            monthList[month + 1] - monthList[month] + if (month == 2 && year % 4 == 0 && year != 1900) 1 else 0
        setFocusedItem(null, listOf())
        val date = Date(System.currentTimeMillis())
        val today = SimpleDateFormat("yyyy.M.d", Locale.getDefault()).format(date)
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
                        if (node.date in 1..lastDay) {
                            node.date
                        } else {
                            0
                        },
                        if (node.date in 1..lastDay) {
                            list[node.date]
                        } else {
                            listOf()
                        },
                        today == (year.toString()+"."+month.toString() + "." + node.date.toString())
                    )
                }
                if (node.x < 6 && set.contains(node.date + 1).not()) {
                    set.add(node.date + 1)
                    queue.add(Temp(node.x + 1, node.y, node.date + 1))
                }

                if (node.y < 6 && set.contains(node.date + 7).not()) {
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
        day += 1
        day %= 7
        return day
    }
}

data class Temp(
    val x: Int,
    val y: Int,
    val date: Int
)