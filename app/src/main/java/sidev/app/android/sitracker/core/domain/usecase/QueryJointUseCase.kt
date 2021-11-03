package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.ProgressJoint
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint


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
    progresses: List<ScheduleProgress>
  ): List<ProgressJoint> {
    val progressJoints = mutableListOf<ProgressJoint>()

    for(progress in progresses) {
      val schedule = schedules.find { it.id == progress.scheduleId }
        ?: continue

      val task = tasks.find { it.id == schedule.taskId }
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
    progresses: List<ScheduleProgress>
  ): List<ScheduleJoint> {
    val scheduleJoints = mutableListOf<ScheduleJoint>()

    for(schedule in schedules) {
      val task = tasks.find { it.id == schedule.taskId }
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
      )
    }
    return scheduleJoints
  }
}