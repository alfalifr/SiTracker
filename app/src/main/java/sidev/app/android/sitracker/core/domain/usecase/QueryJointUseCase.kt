package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.ProgressJoint
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint
import sidev.app.android.sitracker.core.domain.model.TaskJoint


interface QueryJointUseCase {
  /**
   * Join [ScheduleProgress] and other
   * related data so the data about schedule progress
   * will be complete, not just data of foreign keys.
   *
   * This method doesn't filter for the active items.
   */
  fun getProgressJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
    //nowDateLong: Long = Date().time,
  ): List<ProgressJoint>

  /**
   * Same as [getProgressJoint] with convenient way.
   */
  fun getProgressJoint(
    queryResults: ProgressQueryResult
  ): List<ProgressJoint> = with(queryResults) {
    getProgressJoint(
      tasks = tasks,
      schedules = schedules,
      activeDates = activeDates,
      preferredTimes = preferredTimes,
      preferredDays = preferredDays,
      progresses = progresses,
      intervalTypes = intervalTypes,
      progressTypes = progressTypes,
    )
  }


  /**
   * Join [ScheduleProgress] and other
   * related data so the data about schedule progress
   * will be complete, not just data of foreign keys.
   *
   * This method doesn't filter for the active items.
   *
   * This method is like [getProgressJoint], but
   * produces [ScheduleJoint]. See [ScheduleJoint].
   */
  fun getScheduleJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
  ): List<ScheduleJoint>

  /**
   * Same as [getScheduleJoint] with convenient way.
   */
  fun getScheduleJoint(
    queryResults: ProgressQueryResult
  ): List<ScheduleJoint> = with(queryResults) {
    getScheduleJoint(
      tasks = tasks,
      schedules = schedules,
      activeDates = activeDates,
      preferredTimes = preferredTimes,
      preferredDays = preferredDays,
      progresses = progresses,
      intervalTypes = intervalTypes,
      progressTypes = progressTypes,
    )
  }


  fun getTaskJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
  ): List<TaskJoint>

  /**
   * Same as [getScheduleJoint] with convenient way.
   */
  fun getTaskJoint(
    queryResults: ProgressQueryResult
  ): List<TaskJoint> = with(queryResults) {
    getTaskJoint(
      tasks = tasks,
      schedules = schedules,
      activeDates = activeDates,
      preferredTimes = preferredTimes,
      preferredDays = preferredDays,
      progresses = progresses,
      intervalTypes = intervalTypes,
      progressTypes = progressTypes,
    )
  }
}


class QueryJointUseCaseImpl: QueryJointUseCase {
  /**
   * Join [ScheduleProgress] and other
   * related data so the data about schedule progress
   * will be complete, not just data of foreign keys.
   *
   * This method doesn't filter for the active items.
   */
  override fun getProgressJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
  ): List<ProgressJoint> {
    val progressJoints = mutableListOf<ProgressJoint>()

    for(progress in progresses) {
      val schedule = schedules.find { it.id == progress.scheduleId }
        ?: continue

      val task = tasks.find { it.id == schedule.taskId }
        ?: continue

      val intervalType = intervalTypes.find { it.id == schedule.intervalId }
        ?: continue

      val progressType = progressTypes.find { it.id == schedule.progressTypeId }
        ?: continue

      val progressActiveDates = activeDates.filter {
        it.scheduleId == schedule.id
      }

      val prefTimes = preferredTimes.filter {
        it.scheduleId == schedule.id
      }

      val prefDays = preferredDays.filter {
        it.scheduleId == schedule.id
      }

      progressJoints += ProgressJoint(
        progress = progress,
        schedule = schedule,
        task = task,
        activeDates = progressActiveDates,
        preferredTimes = prefTimes,
        preferredDays = prefDays,
        intervalType = intervalType,
        progressType = progressType,
      )
    }
    return progressJoints
  }

  /**
   * Join [ScheduleProgress] and other
   * related data so the data about schedule progress
   * will be complete, not just data of foreign keys.
   *
   * This method doesn't filter for the active items.
   *
   * This method is like [getProgressJoint], but
   * produces [ScheduleJoint]. See [ScheduleJoint].
   */
  override fun getScheduleJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
  ): List<ScheduleJoint> {
    println("getScheduleJoint AWAL schedules = $schedules")
    val scheduleJoints = mutableListOf<ScheduleJoint>()

    for(schedule in schedules) {
      println("getScheduleJoint schedule = $schedule tasks.find { it.id == schedule.taskId } => ${tasks.find { it.id == schedule.taskId }}")
      val task = tasks.find { it.id == schedule.taskId }
        ?: continue

      val intervalType = intervalTypes.find { it.id == schedule.intervalId }
        ?: continue

      val progressType = progressTypes.find { it.id == schedule.progressTypeId }
        ?: continue

      val progress = progresses.find { it.scheduleId == schedule.id }

      val progressActiveDates = activeDates.filter {
        it.scheduleId == schedule.id
      }

      val prefTimes = preferredTimes.filter {
        it.scheduleId == schedule.id
      }

      val prefDays = preferredDays.filter {
        it.scheduleId == schedule.id
      }

      scheduleJoints += ScheduleJoint(
        schedule = schedule,
        progress = progress,
        task = task,
        activeDates = progressActiveDates,
        preferredTimes = prefTimes,
        preferredDays = prefDays,
        intervalType = intervalType,
        progressType = progressType,
      )
    }
    return scheduleJoints
  }

  override fun getTaskJoint(
    tasks: List<Task>,
    schedules: List<Schedule>,
    activeDates: List<ActiveDate>,
    preferredTimes: List<PreferredTime>,
    preferredDays: List<PreferredDay>,
    progresses: List<ScheduleProgress>,
    intervalTypes: List<IntervalType>,
    progressTypes: List<ProgressType>,
  ): List<TaskJoint> = getScheduleJoint(
    tasks, schedules, activeDates, preferredTimes, preferredDays, progresses,
    intervalTypes, progressTypes,
  ).groupBy {
    it.task
  }.map { (task, scheduleJoints) ->
    TaskJoint(
      task = task,
      scheduleJoints = scheduleJoints,
    )
  }
}