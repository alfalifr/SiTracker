package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity

@Entity(tableName = "active_dates")
data class ActiveDate(
  val scheduleId: Int,
  /**
   * It is measured in millis after epoch.
   */
  val startDate: Long,
  /**
   * It is measured in millis after epoch.
   * If this property is null, it means the schedule
   * is active forever after [startDate].
   */
  val endDate: Long?,
)