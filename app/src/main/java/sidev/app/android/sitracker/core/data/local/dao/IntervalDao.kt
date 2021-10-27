package sidev.app.android.sitracker.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.Interval

@Dao
interface IntervalDao {
  @Query("SELECT * FROM intervals")
  fun getAll(): Flow<List<Interval>>
}