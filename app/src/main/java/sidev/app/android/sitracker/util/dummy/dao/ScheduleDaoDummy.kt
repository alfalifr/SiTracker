package sidev.app.android.sitracker.util.dummy.dao

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.ScheduleDao
import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.util.dummy.Dummy

object ScheduleDaoDummy: ScheduleDao {
  override fun getByTaskId(taskId: Int): Flow<List<Schedule>> = flow {
    emit(
      Dummy.schedules.filter { it.taskId == taskId }
    )
  }

  override fun getByTaskIds(taskIds: Set<Int>): Flow<List<Schedule>> = flow {
    emit(
      Dummy.schedules.filter { it.taskId in taskIds }
    )
  }

  override fun getByIds(ids: Set<Int>): Flow<List<Schedule>> = flow {
    emit(
      Dummy.schedules.filter { it.id in ids }
    )
  }

  override fun insert(schedule: Schedule): Flow<Int> = flow { emit(1) }

  override fun update(newSchedule: Schedule) {}

  override fun delete(schedule: Schedule): Flow<Int> = flow { emit(1) }

  override fun getTodaySchedules(sql: SupportSQLiteQuery): Flow<List<Schedule>> = flow {
    emit(Dummy.schedules)
  }
}