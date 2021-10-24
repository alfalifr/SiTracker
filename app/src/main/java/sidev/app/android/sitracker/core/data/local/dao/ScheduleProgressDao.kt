package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress

@Dao
interface ScheduleProgressDao {
  @Query("""
    SELECT * FROM schedule_progress 
    WHERE scheduleId = :scheduleId
    ORDER BY startTimestamp DESC
    LIMIT 1
  """)
  fun getLatestProgressOfSchedule(
    scheduleId: Int,
  ): LiveData<ScheduleProgress>

  fun getScheduleOfTasks()
}