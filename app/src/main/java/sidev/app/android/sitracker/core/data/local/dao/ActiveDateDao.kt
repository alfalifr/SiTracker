package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ActiveDate

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
}