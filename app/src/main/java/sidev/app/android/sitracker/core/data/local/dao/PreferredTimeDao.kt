package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
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
}