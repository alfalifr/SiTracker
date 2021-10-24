package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.core.data.local.model.Task

@Dao
interface ScheduleDao {
  @Query("SELECT * FROM schedules WHERE taskId = :taskId")
  fun getByTaskId(taskId: Int): LiveData<List<Schedule>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(schedule: Schedule): LiveData<Int>

  @Update
  fun update(newSchedule: Schedule)

  @Delete
  fun delete(schedule: Schedule): LiveData<Int>

  @RawQuery //TODO: Make custom raw query for today's task
  fun getTodaysSchedules(sql: SupportSQLiteQuery): LiveData<List<Schedule>>

  fun getScheduleOfTasks(sql: SupportSQLiteQuery): LiveData<List<Schedule>>
/*
  //TODO: continue
  @Query("""
    SELECT * FROM schedules 
    WHERE
    """)
  fun getByDateRange(startDate: Long, endDate: Long): LiveData<List<Schedule>>
 */
}