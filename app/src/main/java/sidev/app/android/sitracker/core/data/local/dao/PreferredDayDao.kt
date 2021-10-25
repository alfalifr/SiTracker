package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferredDayDao {
  @Query("""
    SELECT dayInWeek FROM preferred_days
    WHERE scheduleId = :scheduleId
  """)
  fun getDayBySchedule(
    scheduleId: Int,
  ): Flow<List<Int>>
}