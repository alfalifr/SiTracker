package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.util.*
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.*
import java.util.concurrent.TimeUnit


/*
TODO: Consider whether this use case mechanism:
 1. should map query joints to every single [CalendarTileData]
 2. or just map into a calendar rule (of course has less size than the option 1.)
 */

interface CalendarUseCase {
  /**
   * Gets a [CalendarMark] in a particular [dateTime].
   * If this returns null it means there is no a particular [CalendarMark] in [dateTime].
   */
  fun getDateMark(
    dateTime: Long,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): CalendarMark?

  /**
   * Gets a [CalendarRange] for a month which [dateTime] is in.
   */
  fun getMonthCalendarRange(
    taskJoint: TaskJoint,
    dateTime: Long,
    //overallDayCount: Int = Const.calendarDayInMonth,
    firstDayOfWeek: Int = 1,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): CalendarRange


  /**
   * Gets a [CalendarRange] for a month which [dateTime] is in
   * from given list of [taskJoints].
   */
  fun getMonthCalendarRange(
    taskJoints: List<TaskJoint>,
    dateTime: Long,
    //overallDayCount: Int = Const.calendarDayInMonth,
    firstDayOfWeek: Int = 1,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): CalendarRange

  /**
   * Merges some element [CalendarEvent] if they are
   * in same period (same size and content of [CalendarEvent.intervals]).
   */
  fun mergeCalendarEvents(
    events: List<CalendarEvent>
  ): List<CalendarEvent>

/*
  /**
   * [dateTime] is measured in millis after epoch.
   * [dateTime] can contain hour or other smaller time unit
   * data, but the queried time unit will be limited
   * to day of month.
   *
   * [sharedLegends] is useful to save memory for same
   * repeating [scheduleJoint] accross different [dateTime]s.
   */
  fun getTileData(
    scheduleJoint: ScheduleJoint,
    dateTime: Long,
    sharedCalendar: Calendar = Calendar.getInstance(),
    sharedLegends: List<CalendarLegend>? = null,
  ): CalendarTileData

  /**
   * Creates a list of [CalendarTileData] to construct
   * a view of calendar tiles with [CalendarLegend]
   * based on given [scheduleJoint] from [startTime]
   * until next [days].
   */
  fun getTileDataRange(
    scheduleJoint: ScheduleJoint,
    startTime: Long,
    days: Int = 30,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): List<CalendarTileData>

  /**
   * Same as [getTileDataRange] but in convenient way
   * to get a list of [CalendarTileData] in a month which
   * [dateTime] is in.
   */
  fun getTileDataRangeInMonth(
    scheduleJoint: ScheduleJoint,
    dateTime: Long,
    days: Int = Const.calendarDayInMonth,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): List<CalendarTileData>


  /**
   * Creates a list of [CalendarTileData] to construct
   * a view of calendar tiles with [CalendarLegend]
   * based on given [TaskJoint] from [startTime]
   * until next [days].
   */
  fun getTileDataRange(
    taskJoint: TaskJoint,
    startTime: Long,
    days: Int = 30,
    sharedCalendar: Calendar = Calendar.getInstance(),
  ): List<CalendarTileData>
 */
}


class CalendarUseCaseImpl(
  private val iconUseCase: IconUseCase,
  private val timeUseCase: TimeUseCase,
): CalendarUseCase {
  /**
   * Gets a [CalendarMark] in a particular [dateTime].
   * If this returns null it means there is no a particular [CalendarMark] in [dateTime].
   */
  override fun getDateMark(
    dateTime: Long,
    sharedCalendar: Calendar
  ): CalendarMark? = when(sharedCalendar[Calendar.DAY_OF_WEEK]) {
    Calendar.SUNDAY -> CalendarMark(
      dateTextColor = Const.redHex,
      tileBgColor = null,
    )
    else -> null //Const.blackHex
  }

  /**
   * Gets a [CalendarRange] for a month which [dateTime] is in.
   */
  override fun getMonthCalendarRange(
    taskJoint: TaskJoint,
    dateTime: Long,
    //overallDayCount: Int,
    firstDayOfWeek: Int,
    sharedCalendar: Calendar,
  ): CalendarRange {
    sharedCalendar.timeInMillis = dateTime

    val minDateInMonth = sharedCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    sharedCalendar[Calendar.DAY_OF_MONTH] = minDateInMonth

    val minDateInMonthDay = sharedCalendar[Calendar.DAY_OF_WEEK]
    val daysBeforeActiveMonth = minDateInMonthDay - firstDayOfWeek

    val rangeStart = getDateMillis(sharedCalendar)

    val calendarRangeStart = rangeStart - TimeUnit.DAYS.toMillis(daysBeforeActiveMonth.toLong())


    val maxDateInMonth = sharedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    sharedCalendar[Calendar.DAY_OF_MONTH] = maxDateInMonth

    val rangeEnd = getDateMillis(sharedCalendar) +
      TimeUnit.DAYS.toMillis(1) - 1L

    val calendarRange = UnclosedLongRange(
      rangeStart,
      rangeEnd,
    )

    val sundayMarkEvent = CalendarEvent(
      intervals = listOf(
        TimeInterval(
          interval = TimeUnit.DAYS.toMillis(7),
          min = rangeStart,
          max = rangeEnd,
          start = calendarRangeStart,
        )
      ),
      legends = emptyList(),
      mark = CalendarMark(
        dateTextColor = Const.redHex,
        tileBgColor = null,
      ),
    )

    val events = mutableListOf(
      sundayMarkEvent
    )
    taskJoint.scheduleJoints.mapNotNullTo(events) { scheduleJoint ->
      with(scheduleJoint) {
        val mergedActiveDates = timeUseCase.mergeActiveDates(
          activeDates.filter { it overlaps calendarRange }
        )

        val intervals = if(mergedActiveDates.isNotEmpty()) combineMap(
          mergedActiveDates,
          preferredDays
        ) { (activeDate, preferredDay) ->
          TimeInterval(
            interval = TimeUnit.DAYS.toMillis(preferredDay.dayInWeek.toLong()),
            min = activeDate.startDate,
            max = activeDate.endDate,
            start = calendarRangeStart,
          )
        } else preferredDays.map {
          TimeInterval(
            interval = TimeUnit.DAYS.toMillis(it.dayInWeek.toLong()),
            min = rangeStart,
            max = rangeEnd,
            start = calendarRangeStart,
          )
        }

        if(intervals.isEmpty()) {
          return@mapNotNullTo null
        }

        val legends = listOf(
          CalendarLegend(
            text = schedule.label,
            icon = IconPicData(
              resId = iconUseCase.getResId(task.iconId),
              color = task.color,
              desc = task.name,
            ),
          )
        )

        CalendarEvent(
          intervals = intervals,
          legends = legends,
          mark = null,
        )
      }
    }

    return CalendarRange(
      start = rangeStart,
      end = rangeEnd,
      events = events,
    )
  }

  /**
   * Gets a [CalendarRange] for a month which [dateTime] is in
   * from given list of [taskJoints].
   */
  override fun getMonthCalendarRange(
    taskJoints: List<TaskJoint>,
    dateTime: Long,
    //overallDayCount: Int,
    firstDayOfWeek: Int,
    sharedCalendar: Calendar,
  ): CalendarRange {
    if(taskJoints.isEmpty()) {
      sharedCalendar.timeInMillis = dateTime

      val minDateInMonth = sharedCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
      sharedCalendar[Calendar.DAY_OF_MONTH] = minDateInMonth

      val rangeStart = getDateMillis(sharedCalendar)

      val maxDateInMonth = sharedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
      sharedCalendar[Calendar.DAY_OF_MONTH] = maxDateInMonth

      val rangeEnd = getDateMillis(sharedCalendar) +
        TimeUnit.DAYS.toMillis(1) - 1L

      return CalendarRange(
        start = rangeStart,
        end = rangeEnd,
        events = emptyList(),
      )
    }

    val ranges = taskJoints.map {
      getMonthCalendarRange(
        taskJoint = it,
        dateTime = dateTime,
        //overallDayCount = overallDayCount,
        firstDayOfWeek = firstDayOfWeek,
        sharedCalendar = sharedCalendar,
      )
    }

    val events = mutableListOf<CalendarEvent>()
    ranges.forEach { range ->
      events += range.events
    }
    val mergedEvent = mergeCalendarEvents(events)

    return ranges.first().copy(
      events = mergedEvent,
    )
  }

  /**
   * Merges some element [CalendarEvent] if they are
   * in same period (same size and content of [CalendarEvent.intervals]).
   */
  override fun mergeCalendarEvents(events: List<CalendarEvent>): List<CalendarEvent> =
    events.mergeIf(
      condition = { a, b ->
        a inSamePeriodAs b
      },
      merge = { a, b ->
        a.merge(b)
      }
    )

  /*
  private fun getDateMarkOutsideActiveMonth(
    from: Long,
    days: Int,
  ): CalendarEvent {
    val start = getDateMillis(from)
    val end = start + TimeUnit.DAYS.toMillis(days.toLong())
    return CalendarEvent(
      interval = TimeInterval(
        interval = TimeUnit.DAYS.toMillis(1),
        min = start,
        max = end,
      ),
      legends = emptyList(),
      mark = CalendarMark()
    )
  }
   */
/*
  /**
   * [dateTime] is measured in millis after epoch.
   * [dateTime] can contain hour or other smaller time unit
   * data, but the queried time unit will be limited
   * to day of month.
   *
   * [sharedLegends] is useful to save memory for same
   * repeating [scheduleJoint] accross different [dateTime]s.
   */
  override fun getTileData(
    scheduleJoint: ScheduleJoint,
    dateTime: Long,
    sharedCalendar: Calendar,
    sharedLegends: List<CalendarLegend>?,
  ): CalendarTileData {
    sharedCalendar.timeInMillis = dateTime

    val dayOfWeek = sharedCalendar[Calendar.DAY_OF_WEEK]

    val dateTextColor = getDateMark(dayOfWeek)

    val legends: List<CalendarLegend>? = with(scheduleJoint) {
      if(
        activeDates.any { dateTime in it } //`dateTime` is in active dates range.
        && preferredDays.any { it.dayInWeek == dayOfWeek } //`dayOfWeek` is one of preferred days.
      ) {
        sharedLegends ?: listOf(
           CalendarLegend(
            text = schedule.label,
            icon = IconPicData(
              resId = iconUseCase.getResId(task.iconId),
              color = task.color,
            ),
          )
        )
      } else null
    }

    return CalendarTileData(
      date = CalendarDate(
        dayOfWeek = dayOfWeek,
        dayOfMonth = sharedCalendar[Calendar.DAY_OF_MONTH],
        monthInYear = sharedCalendar[Calendar.MONTH],
      ),
      dateTextColor = dateTextColor,
      legends = legends,
    )
  }

  /**
   * Creates a list of [CalendarTileData] to construct
   * a view of calendar tiles with [CalendarLegend]
   * based on given [scheduleJoint] from [startTime]
   * until next [days].
   */
  override fun getTileDataRange(
    scheduleJoint: ScheduleJoint,
    startTime: Long,
    days: Int,
    sharedCalendar: Calendar,
  ): List<CalendarTileData> {
    val cal = Calendar.getInstance()
    val millisInDay = TimeUnit.DAYS.toMillis(1)

    var currentTime = startTime - millisInDay

    val result = mutableListOf<CalendarTileData>()
    val sharedLegends = with(scheduleJoint) {
      listOf(
        CalendarLegend(
          text = schedule.label,
          icon = IconPicData(
            resId = iconUseCase.getResId(task.iconId),
            color = task.color,
          )
        )
      )
    }

    for(i in 0 until days) {
      currentTime += millisInDay
      result += getTileData(
        scheduleJoint = scheduleJoint,
        dateTime = currentTime,
        sharedCalendar = cal,
        sharedLegends = sharedLegends,
      )
    }

    return result
  }

  /**
   * Same as [getTileDataRange] but in convenient way
   * to get a list of [CalendarTileData] in a month which
   * [dateTime] is in.
   */
  override fun getTileDataRangeInMonth(
    scheduleJoint: ScheduleJoint,
    dateTime: Long,
    days: Int,
    sharedCalendar: Calendar,
  ): List<CalendarTileData> {
    sharedCalendar.timeInMillis = dateTime

    val dayOfMonth = sharedCalendar[Calendar.DAY_OF_MONTH]

    val dateTimeOfFirstDayInMonth = dateTime - TimeUnit.DAYS.toMillis(dayOfMonth.toLong())

    return getTileDataRange(
      scheduleJoint = scheduleJoint,
      startTime = dateTimeOfFirstDayInMonth,
      days = days,
      sharedCalendar = sharedCalendar,
    )
  }

  /**
   * Creates a list of [CalendarTileData] to construct
   * a view of calendar tiles with [CalendarLegend]
   * based on given [TaskJoint] from [startTime]
   * until next [days].
   */
  override fun getTileDataRange(
    taskJoint: TaskJoint,
    startTime: Long,
    days: Int,
    sharedCalendar: Calendar
  ): List<CalendarTileData> {
    TODO("Not yet implemented")
  }
 */

  /*
  /**
   * Creates list of [CalendarRange] that contains
   * [nowTime] in the middle of range of [CalendarRange] (between `start` and `end`).
   * [bufferDay] is the minimal and maximal days before and after [nowTime].
   *
   * This method assumes that [ScheduleJoint.activeDates] are already sorted
   * so sorting isn't needed here. It is important to keep in mind that
   * [ScheduleJoint.activeDates] order is important since this method get
   * the minimal and maximal date from [nowTime] as buffer.
   * So, please make sure that [ScheduleJoint.activeDates]
   * is **ALREADY SORTED** first.
   */
  override fun getCalendarRangeData(
    scheduleJoint: ScheduleJoint,
    nowTime: Long,
    bufferDay: Int
  ): List<CalendarRange> {
    val minTime = nowTime - TimeUnit.DAYS.toMillis(bufferDay.toLong())
    val maxTime = nowTime + TimeUnit.DAYS.toMillis(bufferDay.toLong())

    val activeDatesInRange = scheduleJoint.activeDates.filter {
      it.startDate <= maxTime
        && (it.endDate == null || it.endDate >= minTime)
    }

    val cal = Calendar.getInstance()

    activeDatesInRange.inSameGroup { a, b -> a overlaps b }
      .map { activeDates ->

        val start = getCalendarDate(
          activeDates.minOf { it.startDate },
          cal,
        )

        val end = if(activeDates.any { it.endDate == null }) null
          else getCalendarDate(
            activeDates.maxOf { it.endDate!! },
            cal,
          )

        CalendarRange(
          start = start,
          end = end,

        )
      }

    activeDatesInRange.map {
      CalendarRange(
        start =,

      )
    }
  }
 */
}