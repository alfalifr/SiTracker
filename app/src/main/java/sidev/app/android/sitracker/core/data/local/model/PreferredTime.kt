package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
  tableName = "preferred_times",
  foreignKeys = [
    ForeignKey(
      entity = Schedule::class,
      parentColumns = ["id"],
      childColumns = ["scheduleId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class PreferredTime(
  /**
   * In millis. This property shows time from 0 - 24 hours,
   * but unnecessary to have upperbound up to 24 hours.
   */
  val startTime: Long,
  /**
   * In millis. This property shows time from 0 - 24 hours,
   * but unnecessary to have upperbound up to 24 hours.
   * If this property is null, it means there is no preferred end time.
   */
  val endTime: Long?,
  val scheduleId: Int,
)
