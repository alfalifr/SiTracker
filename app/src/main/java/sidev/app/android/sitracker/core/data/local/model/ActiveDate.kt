package sidev.app.android.sitracker.core.data.local.model

data class ActiveDate(
  val scheduleId: Int,
  val startDate: Long,
  val endDate: Long?,
)