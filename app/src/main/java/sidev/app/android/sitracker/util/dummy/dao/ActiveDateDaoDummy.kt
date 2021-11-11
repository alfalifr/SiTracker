package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.ActiveDateDao
import sidev.app.android.sitracker.core.data.local.model.ActiveDate
import sidev.app.android.sitracker.util.dummy.Dummy

object ActiveDateDaoDummy: ActiveDateDao {
  override fun getRecentByScheduleId(scheduleId: Int): Flow<List<ActiveDate>> = flow {
    emit(
      Dummy.activeDates
        .filter { it.scheduleId == scheduleId }
        .sortedByDescending { it.startDate }
    )
  }

  override fun getRecentByScheduleIds(scheduleIds: Set<Int>): Flow<List<ActiveDate>> = flow {
    emit(
      Dummy.activeDates
        .filter { it.scheduleId in scheduleIds }
        .sortedByDescending { it.startDate }
    )
  }

  override fun getActiveDateByTime(now: Long): Flow<List<ActiveDate>> = flow {
    emit(
      Dummy.activeDates.filter {
        it.startDate <= now
          && (it.endDate == null || now <= it.endDate)
      }
    )
  }
}