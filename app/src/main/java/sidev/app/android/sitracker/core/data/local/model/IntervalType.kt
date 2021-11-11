package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interval_types")
data class IntervalType(
  @PrimaryKey
  val id: Int,
  val label: String,
  /**
   * Length of interval. Measured in days.
   * These are some examples:
   * - 1 for daily
   * - 7 for weekly
   * - 30 for monthly
   * - 365 for annually
   */
  val length: Int,
)