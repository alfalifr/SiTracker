package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.Task

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks")
  fun getAll(): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE id = :id")
  fun getById(id: Int): Flow<Task?>

  @Query("""
    SELECT * FROM tasks 
    WHERE id IN (:ids)
  """)
  fun getByIds(ids: Set<Int>): Flow<List<Task>>


  @Query("""
    SELECT * FROM tasks
    ORDER BY priority DESC
    LIMIT :limit
  """)
  fun getOrderedByPriority(
    limit: Int = 10
  ): Flow<List<Task>>

  @Query("""
    SELECT * FROM tasks
    WHERE id NOT IN (:excludedIds)
    ORDER BY priority DESC
    LIMIT :limit
  """)
  fun getNotInIdsOrderedByPriority(
    excludedIds: Set<Int>,
    limit: Int = 10
  ): Flow<List<Task>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(task: Task): Flow<Int>

  @Delete
  fun delete(task: Task): Flow<Int>

  @Update
  fun update(newTask: Task)
}