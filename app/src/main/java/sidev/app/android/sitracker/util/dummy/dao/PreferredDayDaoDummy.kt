package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.PreferredDayDao
import sidev.app.android.sitracker.core.data.local.model.PreferredDay
import sidev.app.android.sitracker.util.Formats
import sidev.app.android.sitracker.util.dummy.Dummy

object PreferredDayDaoDummy: PreferredDayDao {
  override fun getDayBySchedule(scheduleId: Int): Flow<List<Int>> = flow {
    emit(
      Dummy.preferredDay
        .filter { it.scheduleId == scheduleId }
        .map { it.dayInWeek }
    )
  }

  override fun getDayByScheduleIds(scheduleIds: Set<Int>): Flow<List<PreferredDay>> = flow {
    emit(
      Dummy.preferredDay.filter { it.scheduleId in scheduleIds }
    )
  }

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   * See [Formats.dayOfWeek].
   */
  override fun getDayByNowOrScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>
  ): Flow<List<PreferredDay>> = flow {
    emit(
      Dummy.preferredDay.filter {
        it.dayInWeek == nowDay
          || it.scheduleId in scheduleIds
      }
    )
  }

  /**
   * [nowDay] is measured in day.
   * It has value between 1-7 starts with Sunday.
   * See [Formats.dayOfWeek].
   */
  override fun getDayByNowAndScheduleIds(
    nowDay: Int,
    scheduleIds: Set<Int>
  ): Flow<List<PreferredDay>> = flow {
    emit(
      Dummy.preferredDay.filter {
        it.dayInWeek == nowDay
          && it.scheduleId in scheduleIds
      }
    )
  }
}