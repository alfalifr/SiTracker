package sidev.app.android.sitracker.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.ProgressType

@Dao
interface ProgressTypeDao {
  @Query("SELECT * FROM progress_type")
  fun getAll(): Flow<List<ProgressType>>
}