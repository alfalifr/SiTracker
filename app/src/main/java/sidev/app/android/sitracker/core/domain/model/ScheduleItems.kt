package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.util.model.UnclosedLongRange


enum class ScheduleItemGroupOrder {
  BY_TIME,
  BY_NAME,
  BY_PRIORITY,
  BY_PROGRESS,
}


data class ScheduleItemData(
  val scheduleJoint: ScheduleJoint,
  /**
   * null if the schedule doesn't have preferred time.
   */
  val timeRange: UnclosedLongRange?,
)


data class ScheduleItemGroupData(
  val schedules: List<ScheduleItemData>,
  val header: String,
)