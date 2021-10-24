package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import sidev.app.android.sitracker.core.data.local.model.Task

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks")
  fun getAll(): LiveData<List<Task>>

  @Query("SELECT * FROM tasks WHERE id = :id")
  fun getById(id: Int): LiveData<Task>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(task: Task): LiveData<Int>

  @Delete
  fun delete(task: Task): LiveData<Int>

  @Update
  fun update(newTask: Task)
}