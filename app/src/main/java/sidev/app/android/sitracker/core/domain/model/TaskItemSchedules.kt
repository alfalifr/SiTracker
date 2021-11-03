package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.util.model.UnclosedLongRange


enum class TaskItemScheduleGroupOrder {
  BY_TIME,
  BY_NAME,
  BY_PRIORITY,
  BY_PROGRESS,
}


data class TaskItemSchedule(
  val scheduleJoint: ScheduleJoint,
  /**
   * null if the schedule doesn't have preferred time.
   */
  val timeRange: UnclosedLongRange?,
)


data class TaskItemScheduleGroup(
  val schedules: List<TaskItemSchedule>,
  val header: String,
)