package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress

@Dao
interface ScheduleProgressDao {
  @Query("""
    SELECT * FROM schedule_progress 
    WHERE scheduleId = :scheduleId
    ORDER BY startTimestamp DESC
    LIMIT 1
  """)
  fun getLatestProgressOfSchedule(
    scheduleId: Int,
  ): Flow<ScheduleProgress?>

  @Query("""
    SELECT * FROM schedule_progress
    WHERE startTimestamp <= :timestamp
    AND endTimestamp >= :timestamp
    AND scheduleId IN (:scheduleIds)
  """)
  fun getActiveProgressListByScheduleIds(
    timestamp: Long,
    scheduleIds: Set<Int>,
  ): Flow<List<ScheduleProgress>>

  @Query("""
    SELECT * FROM schedule_progress
    WHERE startTimestamp <= :now
    AND endTimestamp >= :now
  """)
  fun getActiveProgressListByTime(
    now: Long,
  ): Flow<List<ScheduleProgress>>


  @Query("""
    SELECT * FROM schedule_progress
    WHERE scheduleId IN (:scheduleIds)
    ORDER BY startTimestamp DESC
  """)
  fun getRecentByScheduleIds(
    scheduleIds: Set<Int>,
  ): Flow<List<ScheduleProgress>>
}