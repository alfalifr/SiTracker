package sidev.app.android.sitracker.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.PreferredDay
import sidev.app.android.sitracker.core.data.local.model.PreferredTime
import sidev.app.android.sitracker.util.Formats

@Dao
interface PreferredDayDao {
  @Query("""
    SELECT * FROM preferred_days
    WHERE scheduleId = :scheduleId
  """)
  fun getDayBySchedule(
    scheduleId: Int,
  ): Flow<List<PreferredDay>>

  @Query("""
    SELECT * FROM preferred_days
    WHERE scheduleId IN (:scheduleIds)
  """)
  fun getDayByScheduleIds(
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   * See [Formats.dayOfWeek].
   */
  @Query("""
    SELECT * FROM preferred_days
    WHERE dayInWeek = :nowDay
    OR scheduleId IN (:scheduleIds)
  """)
  fun getDayByNowOrScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   * See [Formats.dayOfWeek].
   */
  @Query("""
    SELECT * FROM preferred_days
    WHERE dayInWeek = :nowDay
    AND scheduleId IN (:scheduleIds)
  """)
  fun getDayByNowAndScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>

  @Insert
  fun insertAll(
    preferredDays: List<PreferredDay>,
  ): Flow<LongArray>
}