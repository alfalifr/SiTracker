package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "schedules",
  foreignKeys = [
    ForeignKey(
      entity = Task::class,
      parentColumns = ["id"],
      childColumns = ["taskId"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = ProgressType::class,
      parentColumns = ["id"],
      childColumns = ["progressTypeId"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = IntervalType::class,
      parentColumns = ["id"],
      childColumns = ["intervalId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class Schedule(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val label: String,
  val taskId: Int,
  val progressTypeId: Int,
  val intervalId: Int,

  /**
   * Total amount of realisation
   * (can be either duration or frequency [e.g. 3 times, 4 times, 100 times, so on]).
   * For duration type, this property is measured in millis.
   */
  val totalProgress: Long,
)
