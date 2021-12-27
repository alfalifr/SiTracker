package sidev.app.android.sitracker.util.dummy

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.AppIcons
import java.util.*
import java.util.concurrent.TimeUnit

object Dummy {

  private const val secondMillis = 1000L

  val progressTypes: List<ProgressType>
    get() = _progressTypes
  private val _progressTypes = mutableListOf<ProgressType>(
    ProgressType(0, "Duration"),
    ProgressType(1, "Times"),
  )

  val intervals: List<IntervalType>
    get() = _intervals
  private val _intervals = mutableListOf<IntervalType>(
    IntervalType(0, "Daily", 1),
    IntervalType(1, "Weekly", 7),
    IntervalType(2, "Monthly", 30),
    IntervalType(3, "Annually", 365),
  )


  val tasks: List<Task>
    get() = _tasks
  private val _tasks = mutableListOf(
    Task(0, "Code 20 Lines", 1, "This is desc 1", AppIcons.Coding.id, "#0e9612"),
    Task(1, "Read Life", 2, "This is desc 2", AppIcons.Bin.id, "#17ebeb"),
    Task(2, "Runaway", 3, "This is desc 3", AppIcons.Clip.id, "#eb1726"),
  )

  /**
   * Pair of actual progress and total progress.
   */
  val scheduleProgressNumber: List<Triple<Int, Long, Long>>
    get() = _scheduleProgressNumber
  private val _scheduleProgressNumber = mutableListOf<Triple<Int, Long, Long>>(
    Triple(0, 100L * secondMillis, 2500L * secondMillis),
    Triple(1, 30L * secondMillis, 88L * secondMillis),
    Triple(2, 10L * secondMillis, 50L * secondMillis),
  )

  val schedules: List<Schedule>
    get() = _schedules
  private val _schedules = tasks.mapIndexed { i, task ->
    Schedule(
      id = i,
      taskId = task.id,
      label = "Label #$i",
      progressTypeId = 0,
      intervalId = 0,
      totalProgress = scheduleProgressNumber[i].third,
    )
  }.toMutableList()

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

  val activeDates: List<ActiveDate>
    get() = _activeDates
  private val _activeDates = mutableListOf<ActiveDate>(
    ActiveDate(schedules[0].id, getTimeLong(-500, now = nowLong), getTimeLong(1, now = nowLong)),
    ActiveDate(schedules[1].id, getTimeLong(0, now = nowLong), getTimeLong(2, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(-1, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(1, now = nowLong)),
  )

  val scheduleProgress: List<ScheduleProgress>
    get() = _scheduleProgress
  private val _scheduleProgress = mutableListOf<ScheduleProgress>(
    ScheduleProgress(0, activeDates[0].scheduleId, activeDates[0].startDate, activeDates[0].endDate ?: (now.time + 100), scheduleProgressNumber[0].second),
    ScheduleProgress(1, activeDates[1].scheduleId, activeDates[1].startDate, activeDates[1].endDate ?: (now.time + 100), scheduleProgressNumber[1].second),
    ScheduleProgress(2, activeDates[2].scheduleId, activeDates[2].startDate, activeDates[2].endDate ?: (now.time + 100), scheduleProgressNumber[2].second),
    ScheduleProgress(3, activeDates[3].scheduleId, activeDates[3].startDate, activeDates[3].endDate ?: (now.time + 100), scheduleProgressNumber[2].second),
  )

  val preferredTimes: List<PreferredTime>
    get() = _preferredTimes
  private val _preferredTimes = mutableListOf<PreferredTime>(
    PreferredTime(TimeUnit.HOURS.toMillis(9), null, schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(23), schedules[0].id),
    //PreferredTime(TimeUnit.MINUTES.toMillis(20), TimeUnit.HOURS.toMillis(1), schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(23), null, schedules[2].id),
    PreferredTime(TimeUnit.HOURS.toMillis(0), TimeUnit.HOURS.toMillis(4), schedules[1].id),
  )

  val preferredDay: List<PreferredDay>
    get() = _preferredDay
  private val _preferredDay = mutableListOf<PreferredDay>(
    PreferredDay(3, schedules[2].id),
    PreferredDay(3, schedules[1].id),

    PreferredDay(6, schedules[1].id),
    PreferredDay(6, schedules[2].id),
    PreferredDay(7, schedules[2].id),
    // Code 20 Lines
    ///*
    //PreferredDay(0, schedules[0].id),
    PreferredDay(1, schedules[0].id),
    PreferredDay(2, schedules[0].id),
    PreferredDay(3, schedules[0].id),
    PreferredDay(4, schedules[0].id),
    PreferredDay(5, schedules[0].id),
    PreferredDay(6, schedules[0].id),
    PreferredDay(7, schedules[0].id),
     // */
    // Runaway
    ///*
    //PreferredDay(0, schedules[2].id),
    PreferredDay(1, schedules[2].id),
    PreferredDay(2, schedules[2].id),
    PreferredDay(3, schedules[2].id),
    PreferredDay(4, schedules[2].id),
    PreferredDay(5, schedules[2].id),
    PreferredDay(6, schedules[2].id),
    PreferredDay(7, schedules[2].id),
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

  fun updateProgress(progress: ScheduleProgressUpdate): Boolean {
    val i = _scheduleProgress.indexOfFirst { it.id == progress.id }
    return if(i >= 0) {
      _scheduleProgressNumber[i] = scheduleProgressNumber[i].copy(second = progress.progress)
      true
    } else false
  }

  fun addTask(task: Task): Long {
    _tasks += task
    return _tasks.size.toLong()
  }
  fun addSchedule(schedule: Schedule): Long = _schedules.run {
    this += schedule
    size.toLong()
  }
  fun addPreferredTimes(prefTimes: List<PreferredTime>): LongArray = _preferredTimes.run {
    val initLen = size
    this += prefTimes
    (initLen..size).toList().map { it.toLong() }
      .toLongArray()
  }
  fun addPreferredDays(prefDays: List<PreferredDay>): LongArray = _preferredDay.run {
    val initLen = size
    this += prefDays
    (initLen..size).toList().map { it.toLong() }
      .toLongArray()
  }
  fun addActiveDates(activeDates: List<ActiveDate>): LongArray = _activeDates.run {
    val initLen = size
    this += activeDates
    (initLen..size).toList().map { it.toLong() }
      .toLongArray()
  }
}