package sidev.app.android.sitracker.ui.usecase

import androidx.compose.runtime.Composable
import sidev.app.android.sitracker.core.domain.model.CalendarLegend
import sidev.app.android.sitracker.core.domain.model.CalendarRange
import sidev.app.android.sitracker.ui.model.CalendarTileUiData
import sidev.app.android.sitracker.ui.theme.*
import sidev.app.android.sitracker.util.Color
import sidev.app.android.sitracker.util.Const
import java.util.*
import java.util.concurrent.TimeUnit

interface CalendarUiUseCase {
  /**
   * Translate rule in [calendarRange] into every single
   * tile data of [CalendarTileUiData].
   */
  fun getCalendarTileData(
    calendarRange: CalendarRange,
    overallDayCount: Int = Const.calendarDayInMonth,
    firstDayOfWeek: Int = 1,
    sharedCalendar: Calendar = Calendar.getInstance(),
    isDark: Boolean = false,
  ): List<CalendarTileUiData>
}

class CalendarUiUseCaseImpl: CalendarUiUseCase {
  /**
   * Translate rule in [calendarRange] into every single
   * tile data of [CalendarTileUiData].
   */
  override fun getCalendarTileData(
    calendarRange: CalendarRange,
    overallDayCount: Int,
    firstDayOfWeek: Int,
    sharedCalendar: Calendar,
    isDark: Boolean,
  ): List<CalendarTileUiData> {
    sharedCalendar.timeInMillis = calendarRange.start

    val result = mutableListOf<CalendarTileUiData>()

    val firstDayOfMonthInWeek = sharedCalendar[Calendar.DAY_OF_WEEK]
    val daysBeforeActiveMonth = firstDayOfMonthInWeek - firstDayOfWeek

    fun addOutsideActiveMonthTile(dateText: String) {
      result += CalendarTileUiData(
        dateText = dateText,
        dateTextColor = TransOppositeDarkColor2(isDark),
        bgColor = TransFollowingDarkColor2(isDark),
        legend = null,
      )
    }

    val millisInDay = TimeUnit.DAYS.toMillis(1)
    var currentMillisInDay = calendarRange.start - millisInDay * daysBeforeActiveMonth

    /*
    ============
    Before Active Month
    ============
     */
    for(i in 0 until daysBeforeActiveMonth) {
      sharedCalendar.timeInMillis = currentMillisInDay
      val date = sharedCalendar[Calendar.DAY_OF_MONTH]
      addOutsideActiveMonthTile(dateText = date.toString())
      currentMillisInDay += millisInDay
    }

    sharedCalendar.timeInMillis = calendarRange.end
    val lastDayOfMonth = sharedCalendar[Calendar.DAY_OF_MONTH]
    val daysAfterActiveMonth = overallDayCount - (
        daysBeforeActiveMonth + lastDayOfMonth
      )

    /*
    ============
    In Active Month
    ============
     */
    for(i in 0 until lastDayOfMonth) {
      sharedCalendar.timeInMillis = currentMillisInDay
      val date = sharedCalendar[Calendar.DAY_OF_MONTH]

      val filteredEvents = calendarRange.events.filter { event ->
        event.intervals.any { currentMillisInDay in it }
      }

      val foundMark = filteredEvents.find {
        it.mark != null
      }?.mark

      val legends = if(filteredEvents.isEmpty()) null
      else {
        val list = mutableListOf<CalendarLegend>()
        filteredEvents.forEach { event ->
          list += event.legends
        }
        list
      }

      result += CalendarTileUiData(
        dateText = date.toString(),
        dateTextColor = foundMark?.dateTextColor?.let { Color(it) } ?: OppositeDark(isDark),
        bgColor = foundMark?.tileBgColor?.let { Color(it) } ?: FollowingDark(isDark),
        legend = legends,
      )

      currentMillisInDay += millisInDay
    }

    /*
    ============
    After Active Month
    ============
     */
    for(i in 0 until daysAfterActiveMonth) {
      sharedCalendar.timeInMillis = currentMillisInDay
      val date = sharedCalendar[Calendar.DAY_OF_MONTH]
      addOutsideActiveMonthTile(dateText = date.toString())
      currentMillisInDay += millisInDay
    }

    return result
  }
}