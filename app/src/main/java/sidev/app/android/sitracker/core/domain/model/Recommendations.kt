package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.util.getTimeMillisInDay
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.*
import kotlin.math.absoluteValue

/*
{
  val importanceFactor: ProgressImportanceFactor
    get() = ProgressImportanceFactor(
      tdRanges = activeDates.map { UnclosedLongRange(it.startDate, it.endDate) },
      tPrefTimeRanges = preferredTimes.map { UnclosedLongRange(it.startTime, it.endTime) },
      tPrefDays = preferredDays.map { it.dayInWeek },
      ti0 = progress.startTimestamp,
      ti1 = progress.endTimestamp,
      pt = schedule.totalProgress,
      pr = task.priority,
    )

  val importance: ProgressImportance
    get() = ProgressImportance(
      progressId = progress.id,
      factor = importanceFactor,
    )
}
 */


data class ProgressImportance(
  val progressId: Int,
  //val importance: Double,
  val factor: ProgressImportanceFactor,
) {
  fun getImportance(timeNow: Long, progress: Long): Double =
    factor.calculateImportance(timeNow, progress)
}

data class ProgressImportanceJoint(
  val joint: ProgressJoint,
  val factor: ProgressImportanceFactor,
) {
  fun getImportance(
    timeNow: Long,
    progress: Long = joint.progress.actualProgress,
  ): Double =
    factor.calculateImportance(timeNow, progress)
}


data class ProgressImportanceFactor(
  /**
   * Ranges of active date defined by [ActiveDate].
   * This consists of start (td0): [ActiveDate.startDate]
   * and optional end (td1): [ActiveDate.endDate].
   */
  val tdRanges: List<UnclosedLongRange>,

  /**
   * Start of interval (ti0): Time now until deadline defined by the end of period of [Interval].
   */
  val ti0: Long,
  /**
   * Deadline of interval (ti1): The end of period of [Interval].
   */
  val ti1: Long,
/*
  /**
   * Actual progress (p).
   */
  val p: Long,
// */
  /**
   * Total progress (pt).
   */
  val pt: Long,
  /**
   * Priority (pr): lower more important.
   */
  val pr: Int,

  /**
   * Preferred time ranges, 0-24 hours in millis.
   * This consists of start [PreferredTime.startTime]
   * and optional end [PreferredTime.endTime].
   */
  val tPrefTimeRanges: List<UnclosedLongRange>,

  /**
   * Preferred days, 1-7 day (Sunday-Saturday) in week.
   */
  val tPrefDays: List<Int>,
) {
  /**
   * Formula: (higher better / more important)
   */
  fun calculateImportance(t0: Long, p: Long): Double =
    tiFactor(t0) +
      tdFactor(t0) +
      pFactor(p) +
      prFactor +
      prefFactor(t0)

  /**
   * Factor that involves time of current period of progress.
   */
  fun tiFactor(t0: Long): Double {
    val fraction = tiFraction(t0)
    return if(fraction <= 1) fraction * 2.0
    else -2.0 //fraction > 1 means the progress has expired.
  }

  /**
   * Factor that involves time of active dates.
   */
  fun tdFactor(t0: Long): Double {
    val fraction = tdFraction(t0)
    return if(fraction <= 1) fraction * 3.0
    else -3.0 //fraction > 1 means the progress has expired.
  }

  /**
   * Factor that involves current progress of the schedule.
   */
  fun pFactor(p: Long): Double = (1 - pFraction(p)) * 4.0

  /**
   * Factor that involves priority of task.
   */
  val prFactor: Double
    get() = if(pr <= 0) 11.0 else 10.0 / pr

  /**
   * Factor that involves preferred days and times
   * of the schedule.
   */
  fun prefFactor(t0: Long): Double {
    val cal = Calendar.getInstance()
    cal.time = Date(t0)

    var score = 0.0

    val dayNow = cal[Calendar.DAY_OF_WEEK]

    if(tPrefDays.any { dayNow == it }) {
      score += 2
    }

    /*
    val t0Hour = TimeUnit.HOURS.toMillis(cal[Calendar.HOUR_OF_DAY].toLong())
    val t0Min = TimeUnit.MINUTES.toMillis(cal[Calendar.MINUTE].toLong())
    val t0Sec = TimeUnit.SECONDS.toMillis(cal[Calendar.SECOND].toLong())
    val t0Milli = cal[Calendar.MILLISECOND]
     */

    val t0InMillisInDay = getTimeMillisInDay(cal) //t0Hour + t0Min + t0Sec + t0Milli

    val closedTPrefTime = tPrefTimeRanges.find { it.end != null && t0InMillisInDay in it }
    val firstFoundTPrefTime = tPrefTimeRanges.find { t0InMillisInDay in it }

    val chosenTPrefTime = closedTPrefTime ?: firstFoundTPrefTime

    if(chosenTPrefTime != null) {
      score += 2
      if(chosenTPrefTime.end != null) {
        with(chosenTPrefTime) {
          val tPrefTimeTotal = (end!! - start
            / 2.0) /* Divided by 2 because
                  it is not always more important if `t0`
                  closer to end preferred time,
                  but rather if `t0` is closer to middle
                  of preferred time range.
                */

          // Closer to middle of preferred time range,
          // the more important a schedule becomes.
          val tPrefTimeNow = t0InMillisInDay - start
          val tPrefTimeFactor = (1 - tPrefTimeNow.toDouble() / tPrefTimeTotal).absoluteValue
          score += tPrefTimeFactor
        }
      }
    }
    return score
  }


  /**
   * Fraction of how far progress [p] to complete [pt] (0-1).
   * Lower more important.
   */
  fun pFraction(p: Long): Double = p.toDouble() / pt

  /**
   * Fraction of how far now [t0] to deadline of active date [td1]
   * from start point of active date [td0].
   * 0 if [td1] is 0 (that means [ActiveDate.endDate] is null).
   * Higher more important.
   */
  fun tdFraction(t0: Long): Double = tdRanges
    .find { t0 in it }
    ?.let { (start, end) ->
      if(end == null) {
        return@let 0.0
      }
      (t0 - start).toDouble() / (end - start)
    } ?: 0.0

  /**
   * Fraction of how far now [t0] to deadline of current period [ti1]
   * from start point of current period [ti0].
   * Higher more important.
   */
  fun tiFraction(t0: Long): Double =
    (t0 - ti0).toDouble() / (ti1 - ti0)

/*
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
 */
}