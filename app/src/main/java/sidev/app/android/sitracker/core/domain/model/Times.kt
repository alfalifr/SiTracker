package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.util.Formats

data class CalendarDate(
  /**
   * Value ranges 1-7 starts with Sunday.
   * See [Formats.dayOfWeek].
   */
  val dayOfWeek: Int,
  val dayOfMonth: Int,
  val monthInYear: Int,
  val year: Int,
)

/**
 * [interval] contains the smallest time unit to make a time interval and measured in millis,
 * for example: if this data represents interval of an hour (hourly),
 * then the value of [interval] will be 1,000 * 3,600 = 3,600,000 -> 1,000 is the amount of millis in a second
 * and 3,600 is the amount of seconds in an hour.
 *
 * [start] isn't same as [min]. [start] tells at what timestamp this interval
 * starts for the next [interval]. Where [min] tells the minimum [timestamp]
 * value to be checked.
 *
 * Null [min] means this interval doesn't have a start.
 * Null [max] means this interval doesn't have an end.
 */
data class TimeInterval(
  val interval: Long,
  val min: Long?,
  val max: Long?,
  val start: Long? = null,
) {
  operator fun contains(timestamp: Long): Boolean =
    (min == null || timestamp >= min)
      && (max == null || timestamp <= max)
      && (timestamp - (start ?: 0L)) % interval == 0L
}

/*
data class CalendarTileData(
  val date: CalendarDate,
  val dateTextColor: String,
  val tileBgColor: String,
  val legends: List<CalendarLegend>?, //can null to save memory cuz there will be many data with this class
)
 */

/**
 * Contains a legend in a particular date.
 */
data class CalendarLegend(
  val text: String,
  val icon: IconPicData?,
)

/**
 * Contains a data to mark a date with given [dateTextColor], e.g. Sunday is marked as red text,
 * and background color [tileBgColor], e.g. date that outside an active month will be greyer.
 */
data class CalendarMark(
  val dateTextColor: String,
  val tileBgColor: String?,
)

/**
 * Marks [legends] and [mark] of dates in interval of [intervals].
 */
data class CalendarEvent(
  val intervals: List<TimeInterval>,
  val legends: List<CalendarLegend>,
  val mark: CalendarMark?,
)

/**
 * A range of dates with a list of [events].
 *
 * [start] and [end] is measured in millis after epoch.
 */
data class CalendarRange(
  val start: Long,
  val end: Long,
  val events: List<CalendarEvent>,
)

/*
data class CalendarRange(
  val start: CalendarDate,
  //TODO: Consider `end` be not null cuz it will be hard to calculate the exact event with given preferred days.
  val end: CalendarDate?,
  /**
   * ====DEPRECATED DOC=======
   * This property only contains date that has an event or more.
   * Dates that don't contain any event can be calculated easily
   * and aren't contained here to save memory.
   * ====DEPRECATED DOC - end=======
   *
   *
   */
  val events: List<CalendarTileData>,
)
 */