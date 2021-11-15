package sidev.app.android.sitracker

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.ProgressImportance
import sidev.app.android.sitracker.core.domain.model.ProgressImportanceFactor
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.*
import java.util.concurrent.TimeUnit

object TestDummy {
  val progressTypes = listOf<ProgressType>(
    ProgressType(0, "Duration"),
    ProgressType(1, "Times"),
  )

  val intervalTypes = listOf<IntervalType>(
    IntervalType(0, "Daily", 1),
    IntervalType(1, "Weekly", 7),
    IntervalType(2, "Monthly", 30),
    IntervalType(3, "Annually", 365),
  )


  val tasks: List<Task> by lazy {
    (1..10).map {
      Task(
        id = it,
        name = faker.job().title(),
        desc = "desc $it",
        priority = faker.random().nextInt(1000),
        iconId = faker.random().nextInt(10),
        color = faker.color().hex(),
      )
    }
  }

  val schedules: List<Schedule> by lazy {
    fun createItem(id: Int, taskId: Int) = Schedule(
      id = id,
      taskId = taskId,
      label = faker.lorem().sentence(faker.random().nextInt(5)),
      progressTypeId = progressTypes.random().id,
      intervalId = intervalTypes.random().id,
      totalProgress = faker.random().nextInt(10, 200).toLong(),
    )

    val list = tasks.mapIndexed { i, it ->
      createItem(i, it.id)
    }.toMutableList()

    fun duplicateItem(index: Int) {
      list.add(
        index,
        list[index].let {
          createItem(it.id, it.taskId)
        }
      )
    }

    (0 until faker.random().nextInt(5, 20)).forEach {
      duplicateItem(faker.random().nextInt(0, list.lastIndex))
    }

    list
  }

  val scheduleProgress: List<ScheduleProgress> by lazy {
    fun getActualProgress(schedule: Schedule) =
      faker.random().nextInt(0, schedule.totalProgress.toInt()).toLong()

    fun createItem(id: Int, schedule: Schedule): ScheduleProgress {
      val start = getTimestamp()
      return ScheduleProgress(
        id = id,
        scheduleId = schedule.id,
        startTimestamp = start,
        endTimestamp = getTimestamp(start),
        actualProgress = getActualProgress(schedule),
      )
    }

    val list = schedules.mapIndexed { i, it ->
      createItem(i, it)
    }.toMutableList()


    fun duplicateItem(index: Int) {
      list.add(
        index,
        list[index].let { schProg ->
          createItem(schProg.id, schedules.find { it.id ==  schProg.scheduleId }!!)
        }
      )
    }

    (0 until faker.random().nextInt(5, 10)).forEach {
      duplicateItem(faker.random().nextInt(0, list.lastIndex))
    }

    list
  }

  val scheduleActiveDates: List<ActiveDate> by lazy {
    schedules.map {
      val start = getTimestamp()
      ActiveDate(
        scheduleId = it.id,
        startDate = start,
        endDate = getTimestamp(start),
      )
    }
  }


  fun getTimestamp(min: Long = 0): Long {
    val time =
      if(faker.random().nextBoolean()) getStartTimestamp()
      else getEndTimestamp()

    return if(time < min) time + min
    else time
  }


  fun getStartTimestamp(): Long =
    faker.date().past(100, TimeUnit.HOURS).time

  fun getEndTimestamp(): Long =
    faker.date().future(100, TimeUnit.HOURS).time


  fun filterActiveDates(
    scheduleActiveDates: List<ActiveDate> = this.scheduleActiveDates,
    nowDateLong: Long = Date().time,
  ): List<ActiveDate> {

    return scheduleActiveDates.filter {
      nowDateLong >= it.startDate
        && (it.endDate == null || nowDateLong <= it.endDate!!)
    }
  }

  fun filterActiveSchedules(
    schedules: List<Schedule> = this.schedules,
    filteredActiveDates: List<ActiveDate>? = null,
    nowDateLong: Long = Date().time,
  ): List<Schedule> {
    val activeDates = (filteredActiveDates
      ?: filterActiveDates(
        this.scheduleActiveDates,
        nowDateLong,
      )).toMutableList()

    val activeSchedules = mutableListOf<Schedule>()

    for(sch in schedules) {
      if(activeDates.isEmpty()) {
        break
      }
      val index = activeDates.indexOfFirst { it.scheduleId == sch.id }
      if(index >= 0) {
        activeSchedules += sch
        activeDates.removeAt(index)
      }
    }

    return activeSchedules
  }

  fun filterActiveTask(
    tasks: List<Task> = this.tasks,
    activeSchedules: List<Schedule>? = null,
    filteredActiveDates: List<ActiveDate>? = null,
    nowDateLong: Long = Date().time,
  ): List<Task> {
    val _activeSchedules = (activeSchedules
      ?: filterActiveSchedules(
        schedules = this.schedules,
        filteredActiveDates = filteredActiveDates,
        nowDateLong = nowDateLong,
      )).toMutableList()

    val activeTasks = mutableListOf<Task>()

    for(task in tasks) {
      if(_activeSchedules.isEmpty()) {
        break
      }
      val index = _activeSchedules.indexOfFirst { it.taskId == task.id }
      if(index >= 0) {
        activeTasks += task
        _activeSchedules.removeAt(index)
      }
    }

    return activeTasks
  }


  //TODO: Make method that filters active progress based on
  // [ScheduleProgress.startTimestamp] and [ScheduleProgress.endTimestamp]
  // and [ActiveDate].
  fun filterActiveProgress(
    progress: List<ScheduleProgress> = this.scheduleProgress,
    /*
    activeSchedule: List<Schedule>? = null,
    filteredActiveDates: List<ActiveDate>? = null,
     */
    nowDateLong: Long = Date().time,
  ): List<ScheduleProgress> {
    /*
    val _activeSchedule = (activeSchedule
      ?: filterActiveSchedules(
        this.schedules, filteredActiveDates,
        nowDateLong,
      )).toMutableList()

    val activeProgress = mutableListOf<ScheduleProgress>()

    for(prog in progress) {
      if(_activeSchedule.isEmpty()) {
        break
      }

      val index = _activeSchedule.indexOfFirst { it.id == prog.scheduleId }
      if(index >= 0) {
        activeProgress += prog
        _activeSchedule.removeAt(index)
      }
    }
     */

    //TODO: Decide whether it is really necessary
    return progress.filter {
      nowDateLong >= it.startTimestamp
        && nowDateLong <= it.endTimestamp
    }
  }


  /**
   * Factors and formula see [ProgressImportanceCalculator].
   * This method doesn't filter for the active items.
   */
  fun getProgressImportance(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    //nowDateLong: Long = Date().time,
  ): List<ProgressImportance> {
    val importanceList = mutableListOf<ProgressImportance>()

    for(progress in progresses) {
      val schedule = schedules.find { it.id == progress.scheduleId }
        ?: continue

      val pr = tasks.find { it.id == schedule.taskId }?.priority
        ?: continue

      val ti0 = progress.startTimestamp
      val ti1 = progress.endTimestamp

      val pt = schedule.totalProgress

      val tdRanges = activeDates.filterAndMap {
        take(it.scheduleId == schedule.id)
        UnclosedLongRange(it.startDate, it.endDate)
      }

      val tPrefTimeRanges = preferredTimes.filterAndMap {
        take(it.scheduleId == schedule.id)
        UnclosedLongRange(it.startTime, it.endTime)
      }

      val tPrefDays = preferredDays.filterAndMap {
        take(it.scheduleId == schedule.id)
        it.dayInWeek
      }

      importanceList +=
        ProgressImportance(
          progressId = progress.id,
          factor = ProgressImportanceFactor(
            tdRanges = tdRanges,
            tPrefTimeRanges = tPrefTimeRanges,
            tPrefDays = tPrefDays,
            ti0 = ti0,
            ti1 = ti1,
            pt = pt,
            pr = pr,
            //p = progress.actualProgress,
          )
        )
    }
    return importanceList
  }
}