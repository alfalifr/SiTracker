package sidev.app.android.sitracker.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sidev.app.android.sitracker.core.data.local.model.PreferredDay

@Dao
interface PreferredDayDao {
  @Query("""
    SELECT dayInWeek FROM preferred_days
    WHERE scheduleId = :scheduleId
  """)
  fun getDayBySchedule(
    scheduleId: Int,
  ): Flow<List<Int>>

  @Query("""
    SELECT * FROM preferred_days
    WHERE scheduleId IN (:scheduleIds)
  """)
  fun getDayByScheduleIds(
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   */
  @Query("""
    SELECT * FROM preferred_days
    WHERE dayInWeek = :nowDay
    OR scheduleId IN (:scheduleIds)
  """)
  fun getDayByNowOrScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   */
  @Query("""
    SELECT * FROM preferred_days
    WHERE dayInWeek = :nowDay
    AND scheduleId IN (:scheduleIds)
  """)
  fun getDayByNowAndScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>,
  ): Flow<List<PreferredDay>>
}