package sidev.app.android.sitracker.core.data.local.model

data class ScheduleRealisation(
  val id: Int,
  val scheduleId: Int,
  val startTimestamp: Long,
  val endTimestamp: Long,

  /**
   * Actual amount of realisation
   * (can be either duration or frequency [e.g. 3 times, 4 times, 100 times, so on])
   * that users count as task.
   */
  val actualRealisation: Long,
)