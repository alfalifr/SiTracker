package sidev.app.android.sitracker

import sidev.app.android.sitracker.core.data.local.model.ActiveDate
import sidev.app.android.sitracker.core.data.local.model.Interval
import sidev.app.android.sitracker.util.Formats
import java.math.BigDecimal
import java.util.*
import kotlin.math.absoluteValue


/**
 * Factor:
 *  - Time (time):
 *    -- Deadline of active date (td)
 *    -- Deadline of interval (ti)
 *    -- Now (t0)
 *  - Progress:
 *    -- Actual progress (p)
 *    -- Total progress (pt)
 *  - Priority (pr): lower more important
 *
 *  Every time in this class is measured in millis,
 *  except for the [tPrefDay].
 */
data class ProgressImportanceCalculator(
  /**
   * Start of active date (td0): [ActiveDate.startDate].
   */
  val td0: Long,
  /**
   * Deadline of active date (td1): [ActiveDate.endDate].
   * 0 if [ActiveDate.endDate] is null.
   */
  val td1: Long,

  /**
   * Start of interval (ti0): Time now until deadline defined by the end of period of [Interval].
   */
  val ti0: Long,
  /**
   * Deadline of interval (ti1): The end of period of [Interval].
   */
  val ti1: Long,

  /**
   * Now (t0).
   */
  val t0: Long,
  /**
   * Actual progress (p).
   */
  val p: Long,
  /**
   * Total progress (pt).
   */
  val pt: Long,
  /**
   * Priority (pr): lower more important.
   */
  val pr: Int,

  /**
   * Preferred start time, 0-24 hours in millis.
   */
  val tPrefTime0: Long?,
  /**
   * Preferred end time, 0-24 hours in millis.
   */
  val tPrefTime1: Long?,
  /**
   * Preferred day, 1-7 day (Sunday-Saturday) in week.
   * See [Formats.dayOfWeek].
   */
  val tPrefDay: Int?,
) {
  /**
   * Formula: (higher better / more important)
   */
  fun calculateImportance(): Double =
    tiFactor +
      tdFactor +
      pFactor +
      prFactor +
      prefFactor


  val tiFactor: Double
    get() {
      val fraction = tiFraction
      return if(fraction <= 1) fraction * 2.0
      else 0.0 //fraction > 1 means the progress has expired.
    }

  val tdFactor: Double
    get() {
      val fraction = tdFraction
      return if(fraction <= 1) fraction * 3.0
      else 0.0 //fraction > 1 means the progress has expired.
    }

  val pFactor: Double
    get() = (1 - pFraction) * 4.0

  val prFactor: Double
    get() = if(pr <= 0) 11.0 else 10.0 / pr


  /**
   * Preferred time and day factor.
   * The relative position of now, start, and end in time
   * doesn't really affect much. What does really
   * matter is whether 'now' is in between 'start' and 'end'
   * or not.
   */
  val prefFactor: Double
    get() {
      val cal = Calendar.getInstance()
      cal.time = Date(t0)

      var score = 0.0

      if(tPrefDay != null) {
        val dayNow = cal[Calendar.DAY_OF_WEEK]
        if(tPrefDay == dayNow) {
          score += 2
        }
      }

      val t0Hour = cal[Calendar.HOUR_OF_DAY]
      val t0Min = cal[Calendar.MINUTE]
      val t0Sec = cal[Calendar.SECOND]
      val t0Milli = cal[Calendar.MILLISECOND]

      val t0InMillisInDay = t0Hour + t0Min + t0Sec + t0Milli

      if(tPrefTime0 != null) {
        if(t0InMillisInDay >= tPrefTime0) {
          if(tPrefTime1 == null || t0InMillisInDay <= tPrefTime1) {
            if(tPrefTime1 != null) {
              val tPrefTimeTotal = (tPrefTime1 - tPrefTime0
                / 2.0) /* Divided by 2 because
                  it is not always more important if `t0`
                  closer to end preferred time,
                  but rather if `t0` is closer to middle
                  of preffered time range.
                */

              // Closer to middle of perferred time range,
              // the more important a schedule becomes.
              val tPrefTimeNow = t0InMillisInDay - tPrefTime0
              val tPrefTimeFactor = (1 - tPrefTimeNow.toDouble() / tPrefTimeTotal).absoluteValue
              score += tPrefTimeFactor
            }
            score += 2
          }
        }
      }
      return score
    }


  val tiDelta: Long
    get() = ti - t0

  val tdDelta: Long
    get() = td - t0


  /**
   * Fraction of how far progress [p] to complete [pt] (0-1).
   * Lower more important.
   */
  val pFraction: Double
    get() = p.toDouble() / pt

  /**
   * Fraction of how far now [t0] to deadline of active date [td1]
   * from start point of active date [td0].
   * 0 if [td1] is 0 (that means [ActiveDate.endDate] is null).
   * Higher more important.
   */
  val tdFraction: Double
    get() = if(td1 == 0L) 0.0 else t0d.toDouble() / td

  /**
   * Fraction of how far now [t0] to deadline of current period [ti1]
   * from start point of current period [ti0].
   * Higher more important.
   */
  val tiFraction: Double
    get() = t0i.toDouble() / ti


  /**
   * Difference between start of active date and now ([t0] - [td0]).
   * 0 if [ActiveDate.endDate] is null (td1 == 0).
   */
  val t0d: Long
    get() = if(td1 == 0L) 0 else t0 - td0

  /**
   * Difference between start of period of [Interval] and now ([t0] - [ti0]).
   */
  val t0i: Long
    get() = t0 - ti0

  /**
   * Difference between start and end of active date ([td1] - [td0]).
   * 0 if [ActiveDate.endDate] is null (td1 == 0).
   */
  val td: Long
    get() = if(td1 == 0L) 0 else td1 - td0

  /**
   * Difference between start and end of period of [Interval] ([ti1] - [ti0]).
   */
  val ti: Long
    get() = ti1 - ti0
}
