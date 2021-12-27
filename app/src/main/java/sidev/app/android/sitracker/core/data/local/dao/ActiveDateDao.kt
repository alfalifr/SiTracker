package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ActiveDate
import sidev.app.android.sitracker.core.data.local.model.PreferredTime

@Dao
interface ActiveDateDao {
  @Query("""
    SELECT * FROM active_dates
    WHERE startDate <= :now
    AND endDate >= :now
  """)
  fun getActiveDateByTime(
    now: Long,
  ): Flow<List<ActiveDate>>

  @Query("""
    SELECT * FROM active_dates
    WHERE scheduleId = :scheduleId
    ORDER BY startDate DESC
  """)
  fun getRecentByScheduleId(
    scheduleId: Int
  ): Flow<List<ActiveDate>>

  @Query("""
    SELECT * FROM active_dates
    WHERE scheduleId IN (:scheduleIds)
    ORDER BY startDate DESC
  """)
  fun getRecentByScheduleIds(
    scheduleIds: Set<Int>
  ): Flow<List<ActiveDate>>

  @Insert
  fun insertAll(
    activeDates: List<ActiveDate>,
  ): Flow<LongArray>
}