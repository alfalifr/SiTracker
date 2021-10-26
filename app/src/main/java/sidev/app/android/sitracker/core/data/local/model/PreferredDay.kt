package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
  tableName = "preferred_days",
  foreignKeys = [
    ForeignKey(
      entity = Schedule::class,
      parentColumns = ["id"],
      childColumns = ["scheduleId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class PreferredDay(
  /**
   * Starts with 0 which corresponds to Sunday
   * until 6 which corresponds to Saturday.
   */
  val dayInWeek: Int,
  val scheduleId: Int,
)
