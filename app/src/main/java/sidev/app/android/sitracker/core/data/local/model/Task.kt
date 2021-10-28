package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
  @PrimaryKey(autoGenerate = true)
  val id: Int,
  val name: String,
  val priority: Int,
  val iconId: Int,
  /**
   * Format #
   */
  val color: String,
)