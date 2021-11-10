package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import sidev.app.android.sitracker.util.Formats

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
   * Starts with 1 which corresponds to Sunday
   * until 7 which corresponds to Saturday.
   * See [Formats.dayOfWeek].
   */
  val dayInWeek: Int,
  val scheduleId: Int,
)
