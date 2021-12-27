package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.PreferredTime

@Dao
interface PreferredTimeDao {
  @Query("""
    SELECT * FROM preferred_times
    WHERE scheduleId = :scheduleId
  """)
  fun getTimeBySchedule(scheduleId: Int): Flow<List<PreferredTime>>

  @Query("""
    SELECT * FROM preferred_times
    WHERE scheduleId IN (:scheduleIds)
  """)
  fun getTimeByScheduleIds(
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredTime>>


  /**
   * [nowTimeInDay] is measured in millis and trimmed
   * to just only show time in a day, not time since epoch.
   */
  @Query("""
    SELECT * FROM preferred_times
    WHERE :nowTimeInDay >= startTime
    AND (endTime = NULL OR :nowTimeInDay <= endTime)
    OR scheduleId IN (:scheduleIds)
  """)
  fun getTimeByNowOrScheduleIds(
    nowTimeInDay: Long,
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredTime>>

  @Insert
  fun insertAll(
    preferredTimes: List<PreferredTime>,
  ): Flow<LongArray>
}