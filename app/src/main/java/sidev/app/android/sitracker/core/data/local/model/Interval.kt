package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intervals")
data class Interval(
  @PrimaryKey
  val id: Int,
  val label: String,
)