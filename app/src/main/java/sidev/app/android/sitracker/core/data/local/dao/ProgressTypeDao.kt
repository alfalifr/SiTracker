package sidev.app.android.sitracker.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ProgressType

@Dao
interface ProgressTypeDao {
  @Query("SELECT * FROM progress_types")
  fun getAll(): Flow<List<ProgressType>>

  @Query("SELECT * FROM progress_types WHERE id = :id")
  fun getById(id: Int): Flow<ProgressType?>

  @Query("SELECT * FROM progress_types WHERE id IN (:ids)")
  fun getByIds(ids: Set<Int>): Flow<List<ProgressType>>
}