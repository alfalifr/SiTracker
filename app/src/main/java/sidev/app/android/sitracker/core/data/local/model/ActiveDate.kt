package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity

@Entity(tableName = "active_dates")
data class ActiveDate(
  val scheduleId: Int,
  val startDate: Long,
  val endDate: Long?,
)