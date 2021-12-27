package sidev.app.android.sitracker.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*

interface DataWriteUseCase {
  /**
   * Returns the newly inserted row id.
   */
  fun saveTask(task: Task, autoGeneratedId: Boolean = true): Flow<Int>

  fun saveSchedule(schedule: Schedule, autoGeneratedId: Boolean = true): Flow<Int>
  fun savePreferredTimes(preferredTimes: List<PreferredTime>): Flow<List<Long>>
  fun savePreferredDays(preferredDays: List<PreferredDay>): Flow<List<Long>>
  fun saveActiveDates(activeDates: List<ActiveDate>): Flow<List<Long>>
}


class DataWriteUseCaseImpl(
  private val taskDao: TaskDao,
  private val scheduleDao: ScheduleDao,
  private val preferredTimeDao: PreferredTimeDao,
  private val preferredDayDao: PreferredDayDao,
  private val activeDateDao: ActiveDateDao,
): DataWriteUseCase {

  /**
   * Returns the newly inserted row id.
   */
  override fun saveTask(
    task: Task,
    autoGeneratedId: Boolean
  ): Flow<Int> = taskDao.insert(
    if(!autoGeneratedId) task
    else task.copy(id = 0)
  )

  override fun saveSchedule(
    schedule: Schedule,
    autoGeneratedId: Boolean
  ): Flow<Int> = scheduleDao.insert(
    if(!autoGeneratedId) schedule
    else schedule.copy(id = 0)
  )

  override fun savePreferredTimes(preferredTimes: List<PreferredTime>): Flow<List<Long>> =
    preferredTimeDao.insertAll(preferredTimes).map { it.asList() }

  override fun savePreferredDays(preferredDays: List<PreferredDay>): Flow<List<Long>> =
    preferredDayDao.insertAll(preferredDays).map { it.asList() }

  override fun saveActiveDates(activeDates: List<ActiveDate>): Flow<List<Long>> =
    activeDateDao.insertAll(activeDates).map { it.asList() }
}