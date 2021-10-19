package sidev.app.android.sitracker.core.data.local.model

data class Schedule(
  val id: Int,
  val label: String,
  val taskId: Int,
  val realisation: Realisation,
  val interval: Interval,

  /**
   * Total amount of realisation
   * (can be either duration or frequency [e.g. 3 times, 4 times, 100 times, so on]).
   */
  val totalRealisation: Long,
)
