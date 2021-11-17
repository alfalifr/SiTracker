package sidev.app.android.sitracker.util.dummy

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.AppIcon
import java.util.*
import java.util.concurrent.TimeUnit

object Dummy {

  val progressTypes = listOf<ProgressType>(
    ProgressType(0, "Duration"),
    ProgressType(1, "Times"),
  )

  val intervals = listOf<IntervalType>(
    IntervalType(0, "Daily", 1),
    IntervalType(1, "Weekly", 7),
    IntervalType(2, "Monthly", 30),
    IntervalType(3, "Annually", 365),
  )


  val tasks = listOf(
    Task(0, "Code 20 Lines", 1, "This is desc 1", AppIcon.Coding.id, "#0e9612"),
    Task(1, "Read Life", 2, "This is desc 2", AppIcon.Bin.id, "#17ebeb"),
    Task(2, "Runaway", 3, "This is desc 3", AppIcon.Clip.id, "#eb1726"),
  )

  /**
   * Pair of actual progress and total progress.
   */
  val scheduleProgressNumber = listOf<Pair<Long, Long>>(
    100L to 2500L,
    30L to 88L,
    10L to 50L,
  )

  val schedules = tasks.mapIndexed { i, task ->
    Schedule(
      id = i,
      taskId = task.id,
      label = "Label #$i",
      progressTypeId = 0,
      intervalId = 0,
      totalProgress = scheduleProgressNumber[i].second,
    )
  }

  //val sdf = SimpleDateFormat("dd-MM-yyyy")
  //fun getTimeLong(dateStr: String) = sdf.parse(dateStr.)

  val now = Date()
  val nowLong = Date() //1635097869874L

  fun getTimeLong(
    addDiff: Long = 0,
    unit: TimeUnit = TimeUnit.DAYS,
    now: Date = Dummy.now,
  ): Long =
    now.time + unit.toMillis(addDiff)

  val activeDates = listOf<ActiveDate>(
    ActiveDate(schedules[0].id, getTimeLong(-500, now = nowLong), getTimeLong(1, now = nowLong)),
    ActiveDate(schedules[1].id, getTimeLong(0, now = nowLong), getTimeLong(2, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(-1, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(1, now = nowLong)),
  )

  val scheduleProgress: List<ScheduleProgress>
    get() = _scheduleProgress
  private val _scheduleProgress = mutableListOf<ScheduleProgress>(
    ScheduleProgress(0, activeDates[0].scheduleId, activeDates[0].startDate, activeDates[0].endDate ?: (now.time + 100), scheduleProgressNumber[0].first),
    ScheduleProgress(1, activeDates[1].scheduleId, activeDates[1].startDate, activeDates[1].endDate ?: (now.time + 100), scheduleProgressNumber[1].first),
    ScheduleProgress(2, activeDates[2].scheduleId, activeDates[2].startDate, activeDates[2].endDate ?: (now.time + 100), scheduleProgressNumber[2].first),
    ScheduleProgress(3, activeDates[3].scheduleId, activeDates[3].startDate, activeDates[3].endDate ?: (now.time + 100), scheduleProgressNumber[2].first),
  )

  val preferredTimes = listOf<PreferredTime>(
    PreferredTime(TimeUnit.HOURS.toMillis(9), null, schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(23), schedules[0].id),
    //PreferredTime(TimeUnit.MINUTES.toMillis(20), TimeUnit.HOURS.toMillis(1), schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(23), null, schedules[2].id),
    PreferredTime(TimeUnit.HOURS.toMillis(0), TimeUnit.HOURS.toMillis(4), schedules[1].id),
  )

  val preferredDay = listOf<PreferredDay>(
    PreferredDay(3, schedules[2].id),
    PreferredDay(3, schedules[1].id),

    PreferredDay(6, schedules[1].id),
    PreferredDay(6, schedules[2].id),
    // Code 20 Lines
    ///*
    PreferredDay(0, schedules[0].id),
    PreferredDay(1, schedules[0].id),
    PreferredDay(2, schedules[0].id),
    PreferredDay(3, schedules[0].id),
    PreferredDay(4, schedules[0].id),
    PreferredDay(5, schedules[0].id),
    PreferredDay(6, schedules[0].id),
     // */
    // Runaway
    ///*
    PreferredDay(0, schedules[2].id),
    PreferredDay(1, schedules[2].id),
    PreferredDay(2, schedules[2].id),
    PreferredDay(3, schedules[2].id),
    PreferredDay(4, schedules[2].id),
    PreferredDay(5, schedules[2].id),
    PreferredDay(6, schedules[2].id),
     // */
  )

  /**
   * Returns the size of newly increased [_scheduleProgress]
   * which mimics row id.
   */
  fun addScheduleProgress(progress: ScheduleProgress): Long {
    _scheduleProgress += progress
    return _scheduleProgress.size.toLong()
  }
}