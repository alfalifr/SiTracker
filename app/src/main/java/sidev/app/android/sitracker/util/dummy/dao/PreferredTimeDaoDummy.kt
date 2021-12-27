package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.PreferredTimeDao
import sidev.app.android.sitracker.core.data.local.model.PreferredTime
import sidev.app.android.sitracker.util.dummy.Dummy

object PreferredTimeDaoDummy: PreferredTimeDao {
  override fun insertAll(preferredTimes: List<PreferredTime>): Flow<LongArray> = flow {
    emit(
      Dummy.addPreferredTimes(preferredTimes)
    )
  }

  override fun getTimeBySchedule(scheduleId: Int): Flow<List<PreferredTime>> = flow {
    emit(
      Dummy.preferredTimes.filter { it.scheduleId == scheduleId }
    )
  }

  override fun getTimeByScheduleIds(scheduleIds: Set<Int>): Flow<List<PreferredTime>> = flow {
    emit(
      Dummy.preferredTimes.filter { it.scheduleId in scheduleIds }
    )
  }

  /**
   * [nowTimeInDay] is measured in millis and trimmed
   * to just only show time in a day, not time since epoch.
   */
  override fun getTimeByNowOrScheduleIds(
    nowTimeInDay: Long,
    scheduleIds: Set<Int>
  ): Flow<List<PreferredTime>> = flow {
    emit(
      Dummy.preferredTimes.filter {
        it.startTime <= nowTimeInDay
          && (it.endTime == null || nowTimeInDay <= it.endTime)
          || it.scheduleId in scheduleIds
      }
    )
  }
}