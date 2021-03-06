package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val name: String,
  val priority: Int,
  val desc: String,
  val iconId: Int,
  /**
   * Format #
   */
  val color: String,
)