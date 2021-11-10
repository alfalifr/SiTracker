package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.core.data.local.model.*


/**
 * Raw result of [ScheduleProgress] and other related
 * data from database query.
 */
data class ProgressQueryResult(
  val activeDates: List<ActiveDate>,
  val preferredTimes: List<PreferredTime>,
  val preferredDays: List<PreferredDay>,
  val progresses: List<ScheduleProgress>,
  val schedules: List<Schedule>,
  val tasks: List<Task>,
)


/**
 * A join result of [ScheduleProgress] and other
 * related data so the data about schedule progress
 * will be complete, not just data of foreign keys.
 */
data class ProgressJoint(
  val progress: ScheduleProgress,
  val schedule: Schedule,
  val task: Task,
  val activeDates: List<ActiveDate>,
  val preferredTimes: List<PreferredTime>,
  val preferredDays: List<PreferredDay>,
)

/**
 * This is like [ProgressJoint], but this class
 * prioritizes [Schedule] more than [ScheduleProgress].
 * It means, it can be a [Schedule] without [ScheduleProgress] yet.
 */
data class ScheduleJoint(
  val schedule: Schedule,
  val task: Task,
  val progress: ScheduleProgress?,
  val activeDates: List<ActiveDate>,
  val preferredTimes: List<PreferredTime>,
  val preferredDays: List<PreferredDay>,
)

/**
 * This a joint of [Task] and other related data.
 */
data class TaskJoint(
  val task: Task,
  val scheduleJoints: List<ScheduleJoint>,
)