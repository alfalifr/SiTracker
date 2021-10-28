package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.ScheduleProgressDao
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.util.dummy.Dummy

object ScheduleProgressDaoDummy: ScheduleProgressDao {
  override fun getLatestProgressOfSchedule(
    scheduleId: Int
  ): Flow<ScheduleProgress?> = flow {
    emit(
      Dummy.scheduleProgress
        .filter { it.scheduleId == scheduleId }
        .minByOrNull { it.startTimestamp }
    )
  }

  override fun getActiveProgressListByScheduleIds(
    timestamp: Long,
    scheduleIds: Set<Int>
  ): Flow<List<ScheduleProgress>> = flow {
    emit(
      Dummy.scheduleProgress.filter {
        it.startTimestamp <= timestamp && timestamp <= it.endTimestamp
          && it.scheduleId in scheduleIds
      }
    )
  }

  override fun getActiveProgressListByTime(now: Long): Flow<List<ScheduleProgress>> = flow {
    emit(
      Dummy.scheduleProgress.filter {
        it.startTimestamp <= now && now <= it.endTimestamp
      }
    )
  }
}