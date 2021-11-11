package sidev.app.android.sitracker.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.IntervalType

@Dao
interface IntervalTypeDao {
  @Query("SELECT * FROM interval_types")
  fun getAll(): Flow<List<IntervalType>>

  @Query("SELECT * FROM interval_types WHERE id = :id")
  fun getById(id: Int): Flow<IntervalType?>

  @Query("SELECT * FROM interval_types WHERE id IN (:ids)")
  fun getByIds(ids: Set<Int>): Flow<List<IntervalType>>
}