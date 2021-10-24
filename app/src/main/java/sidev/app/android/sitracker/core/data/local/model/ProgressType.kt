package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_type")
data class ProgressType(
  @PrimaryKey
  val id: Int,
  val label: String,
)
