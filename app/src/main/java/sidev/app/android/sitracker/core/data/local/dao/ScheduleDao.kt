package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.core.data.local.model.Task

@Dao
interface ScheduleDao {
  @Query("""
    SELECT * FROM schedules
    WHERE id = :id
  """)
  fun getById(id: Int): Flow<Schedule?>

  @Query("""
    SELECT * FROM schedules 
    WHERE taskId = (:taskId)
  """)
  fun getByTaskId(
    taskId: Int
  ): Flow<List<Schedule>>

  @Query("""
    SELECT * FROM schedules 
    WHERE taskId IN (:taskIds)
  """)
  fun getByTaskIds(
    taskIds: Set<Int>
  ): Flow<List<Schedule>>

  @Query("""
    SELECT * FROM schedules 
    WHERE id IN (:ids)
  """)
  fun getByIds(
    ids: Set<Int>
  ): Flow<List<Schedule>>


  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(schedule: Schedule): Flow<Int>

  @Update
  fun update(newSchedule: Schedule)

  @Delete
  fun delete(schedule: Schedule): Flow<Int>

  @RawQuery //TODO: Make custom raw query for today's task
  fun getTodaySchedules(sql: SupportSQLiteQuery): Flow<List<Schedule>>
}