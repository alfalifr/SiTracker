package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgressUpdate

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
    WHERE scheduleId = :scheduleId
    AND startTimestamp <= :timestamp
    AND endTimestamp >= :timestamp
    ORDER BY startTimestamp DESC
    LIMIT 1
  """)
  fun getLatestActiveProgressOfSchedule(
    scheduleId: Int,
    timestamp: Long,
  ): Flow<ScheduleProgress?>

  @Query("""
    SELECT COUNT(id) FROM schedule_progress 
    WHERE scheduleId = :scheduleId
    AND startTimestamp <= :timestamp
    AND endTimestamp >= :timestamp
    ORDER BY startTimestamp DESC
    LIMIT 1
  """)
  fun getLatestActiveProgressOfScheduleCount(
    scheduleId: Int,
    timestamp: Long,
  ): Flow<Int>

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


  /**
   * Returns the affected row count.
   */
  @Update(entity = ScheduleProgress::class)
  fun updateProgress(
    progress: ScheduleProgressUpdate
  ): Flow<Int>

  /**
   * Returns row id of inserted row.
   */
  @Insert
  fun insert(progress: ScheduleProgress): Flow<Long>
}