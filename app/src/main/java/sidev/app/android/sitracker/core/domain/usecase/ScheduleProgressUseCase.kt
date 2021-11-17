@file:OptIn(ExperimentalCoroutinesApi::class)
package sidev.app.android.sitracker.core.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.data.local.dao.IntervalTypeDao
import sidev.app.android.sitracker.core.data.local.dao.ScheduleDao
import sidev.app.android.sitracker.core.data.local.dao.ScheduleProgressDao
import sidev.app.android.sitracker.core.data.local.dao.TaskDao
import sidev.app.android.sitracker.core.data.local.model.IntervalType
import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgressUpdate
import java.util.concurrent.TimeUnit

interface ScheduleProgressUseCase {

  /**
   * Returns the affected row count.
   */
  fun updateProgress(
    progressId: Int,
    progress: Long,
  ): Flow<Int>

  /**
   * Ensures that [Schedule] with [scheduleId]
   * at [timestamp] has [ScheduleProgress].
   *
   * If [Schedule] with [scheduleId] doesn't have
   * [ScheduleProgress], then this method inserts new
   * [ScheduleProgress] to DB and returns the new [ScheduleProgress] instance.
   *
   * If [Schedule] with [scheduleId] already has [ScheduleProgress],
   * this method returns already existing [ScheduleProgress] instance.
   */
  fun ensureScheduleHasProgress(
    scheduleId: Int,
    timestamp: Long,
  ): Flow<ScheduleProgress>
}

class ScheduleProgressUseCaseImpl(
  private val progressDao: ScheduleProgressDao,
  private val scheduleDao: ScheduleDao,
  private val intervalType: IntervalTypeDao,
): ScheduleProgressUseCase {

  /**
   * Returns the affected row count.
   */
  override fun updateProgress(
    progressId: Int,
    progress: Long
  ): Flow<Int> = progressDao.updateProgress(
    ScheduleProgressUpdate(
      progressId,
      progress
    )
  )

  /**
   * Ensures that [Schedule] with [scheduleId]
   * at [timestamp] has [ScheduleProgress].
   *
   * If [Schedule] with [scheduleId] doesn't have
   * [ScheduleProgress], then this method inserts new
   * [ScheduleProgress] to DB and returns the [ScheduleProgress] id.
   *
   * If [Schedule] with [scheduleId] already has [ScheduleProgress],
   * this method returns already existing [ScheduleProgress] id.
   */
  override fun ensureScheduleHasProgress(
    scheduleId: Int,
    timestamp: Long
  ): Flow<ScheduleProgress> = progressDao.getLatestActiveProgressOfSchedule(
    scheduleId, timestamp
  ).flatMapLatest { progress ->
    if(progress != null) {
      flow { emit(progress) }
    } else {
      val intervalTypeFlow = scheduleDao.getById(scheduleId).filterNotNull() // schedule flow
        .flatMapLatest { intervalType.getById(it.intervalId) }.filterNotNull() // interval type flow

      val newProgressFlow = intervalTypeFlow.map {
        ScheduleProgress(
          scheduleId = scheduleId,
          startTimestamp = timestamp,
          endTimestamp = timestamp + TimeUnit.DAYS.toMillis(it.length.toLong()),
          actualProgress = 0,
        )
      }

      val insertFlow = newProgressFlow.flatMapLatest {
        progressDao.insert(it)
      }

      combine(
        newProgressFlow,
        insertFlow
      ) { newProgress, rowId ->
        if(rowId < 0) throw InternalError(
          "Can't insert new progress with `scheduleId` of '$scheduleId' at `timestamp` of '$timestamp'"
        )
        newProgress
      }
    }
  }
}