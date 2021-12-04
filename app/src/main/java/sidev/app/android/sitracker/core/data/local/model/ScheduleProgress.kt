package sidev.app.android.sitracker.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "schedule_progress",
  foreignKeys = [
    ForeignKey(
      entity = Schedule::class,
      parentColumns = ["id"],
      childColumns = ["scheduleId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class ScheduleProgress(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val scheduleId: Int,
  val startTimestamp: Long,
  /**
   * It is not null even if the schedule is still progressing.
   * The purpose of this property is to show when the schedule
   * progress expires.
   *
   * Sometime, a progress can overlap with [ActiveDate.endDate],
   * e.g. when a weekly progress start now but [ActiveDate.endDate]
   * is tomorrow. The supposed [endTimestamp] is a week later.
   * But in this case, this [endTimestamp] value will be [ActiveDate.endDate]
   * that comes tomorrow. This rule will make [ProgressImportanceFactor.calculateImportance]
   * calculation easier.
   */
  val endTimestamp: Long,

  /**
   * Actual amount of realisation
   * (can be either duration or frequency [e.g. 3 times, 4 times, 100 times, so on])
   * that users count as task.
   * For duration type, this property is measured in millis.
   */
  val actualProgress: Long,
) {
  /**
   * Checks whether all of `this` property have same value as [other]'s
   * without check [actualProgress]. This is useful in count down
   * as [actualProgress] may changes frequently.
   */
  infix fun equivalentTo(other: ScheduleProgress): Boolean =
    id == other.id
      && scheduleId == other.scheduleId
      && startTimestamp == other.startTimestamp
      && endTimestamp == other.endTimestamp
}


data class ScheduleProgressUpdate(
  val id: Int,
  val progress: Long,
)