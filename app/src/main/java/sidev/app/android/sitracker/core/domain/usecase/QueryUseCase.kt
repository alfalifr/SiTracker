@file:OptIn(
  ExperimentalCoroutinesApi::class,
  FlowPreview::class,
)
package sidev.app.android.sitracker.core.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.util.SuppressLiteral
import sidev.app.android.sitracker.util.getDateMillis
import sidev.app.android.sitracker.util.getTimeMillisInDay
import java.util.*

interface QueryUseCase {
  /**
   *
   *  I. Schedule Recommendation Query Mechanism:
   *    1. Query `ActiveDate` based on 'now' time.
   *
   *    2. Query `PreferredTime` and `PreferredDay` based on:
   *       - Now time:
   *         To check whether 'now' is a preferred time / day.
   *       - Set of `scheduleId` from point 1:
   *         This is for schedules that HAVE preferred time and day,
   *         but 'now' isn't that preferred time / day.
   *
   *    3. Query `ScheduleProgress` based on:
   *       - Now time:
   *         To check whether `ScheduleProgress` is active by 'now'.
   *
   *         This query doesn't include set of `scheduleId` from `ActiveDate`,
   *         because if it included them, that means `ScheduleProgress`
   *         that is expired will be queried. That doesn't mean anything
   *         useful. This will be a different case in `PreferredTime` and `PreferredDay`
   *         query where 'now' outside the preferred time / day
   *         won't mean the progress is expired.
   *
   *    4. Query `Schedule` based on set of `scheduleId` from query result of any (OR condition):
   *       - `ActiveDate` (point 1),
   *       - Preferred times and days (point 2),
   *       - `ScheduleProgress` (point 3).
   *
   *    5. Query any other `Task` whose `Schedule` doesn't have active dates, preferred times and days, and progress yet
   *       will be queried with necessary calculated limit (isn't always same) so that
   *       the query result won't waste memory. The query condition will exclude (NOT IN) ids from (AND condition):
   *       - set of `scheduleId` from `ActiveDate` (point 1).
   *       - set of `scheduleId` from `PreferredTime` and `PreferredDay` (point 2).
   *       - set of `scheduleId` from `ScheduleProgress` (point 3).
   *
   *       The query result is sorted descending by `Task` priority.
   *
   *    6. Query `Task` based on set of `taskId` from query result of `Schedule` (point 4).
   *       Of course `Task` with no `Schedule` won't be queried.
   */
  fun queryRecommendations(
/*
    activeDateDao: ActiveDateDao,
    preferredTimeDao: PreferredTimeDao,
    preferredDayDao: PreferredDayDao,
    scheduleProgressDao: ScheduleProgressDao,
    scheduleDao: ScheduleDao,
    taskDao: TaskDao,
 */
    now: Long = Date().time,
  ): Flow<ProgressQueryResult>

  /**
   * Query Mechanism:
   *   1. Query `ActiveDate` based on 'nowDateTime' time.
   *
   *   2. Query `PreferredDay` based on (AND condition):
   *      - Now time:
   *        To check whether 'now' is a preferred day.
   *      - set of 'scheduleId' from queried `ActiveDate`:
   *        Of course only the active ones will be queried.
   *
   *   3. Query `PreferredTime` based on:
   *      - set of 'scheduleId' from queried `PreferredDay`:
   *        This is for showing the preferred time in that preferred day of a `Schedule`.
   *
   *   4. Query `ScheduleProgress` based on (AND condition):
   *      - set of 'scheduleId' from queried `PreferredDay`:
   *        The queried `ScheduleProgress` will only be the preferred ones in that day.
   *        Of course this is for the context of 'Today' Schedule (which is based on `PreferredDay`).
   *      - Now time:
   *        Of course the queried ones will be the active ones.
   *
   *   5. Query `Schedule` based on set of `scheduleId` from query result of:
   *      - Preferred days (point 2),
   *
   *   6. Query `Task` based on set of `taskId` from queried `Schedule` (point 5).
   *
   * NOTE: this method doesn't check or convert [nowDateTime] to millis
   *  that only represents millis of that date without any hour or other smaller time unit in that date.
   */
  fun queryTodaySchedule(
    nowDateTime: Long,
  ): Flow<ProgressQueryResult>
}


class QueryUseCaseImpl(
  private val activeDateDao: ActiveDateDao,
  private val preferredTimeDao: PreferredTimeDao,
  private val preferredDayDao: PreferredDayDao,
  private val scheduleProgressDao: ScheduleProgressDao,
  private val scheduleDao: ScheduleDao,
  private val taskDao: TaskDao,
): QueryUseCase {

  override fun queryRecommendations(now: Long): Flow<ProgressQueryResult> {
    val activeDateFlow = activeDateDao.getActiveDateByTime(now)

    // Assume that only 1 progress of 1 schedule exists in 1 time.
    val progressFlow = scheduleProgressDao.getActiveProgressListByTime(now)

    val cal = Calendar.getInstance()
    cal.time = Date(now)

    val day = cal[Calendar.DAY_OF_WEEK]

    val nowTimeInDay = getTimeMillisInDay(cal)

    val prefTimeFlow = activeDateFlow.flatMapLatest { list ->
      preferredTimeDao.getTimeByNowOrScheduleIds(
        nowTimeInDay = nowTimeInDay,
        scheduleIds = list.map { it.scheduleId }.toSet(),
      )
    }

    val prefDayFlow = activeDateFlow.flatMapLatest { list ->
      preferredDayDao.getDayByNowOrScheduleIds(
        nowDay = day,
        scheduleIds = list.map { it.scheduleId }.toSet()
      )
    }

    val scheduleIdFlow = combine(activeDateFlow, progressFlow, prefTimeFlow, prefDayFlow) {
        activeDates, progresses, prefTimes, prefDays ->

      val ids = mutableSetOf<Int>()

      activeDates.mapTo(ids) { it.scheduleId }
      progresses.mapTo(ids) { it.scheduleId }
      prefTimes.mapTo(ids) { it.scheduleId }
      prefDays.mapTo(ids) { it.scheduleId }
      ids
    }

    val scheduleFlow = scheduleIdFlow.map {
      scheduleDao.getByTaskIds(it)
    }.flattenConcat()

    val taskFlow = scheduleFlow.flatMapLatest { list ->
      taskDao.getByIds(
        list.map { it.taskId }.toSet()
      )
    }

    val randomTaskFlow = scheduleIdFlow.flatMapLatest { scheduleIds ->
      val limit = calculateNecessaryRandomScheduleCount(scheduleIds.size)
      taskDao.getNotInIdsOrderedByPriority(
        excludedIds = scheduleIds,
        limit = limit,
      )
    }

    val randomScheduleFlow = randomTaskFlow.flatMapLatest { tasks ->
      scheduleDao.getByTaskIds(
        tasks.map { it.id }.toSet()
      )
    }

    @Suppress(SuppressLiteral.UNCHECKED_CAST)
    return combine(
      activeDateFlow, progressFlow,
      prefTimeFlow, prefDayFlow,
      scheduleFlow, taskFlow,
      randomTaskFlow,
      randomScheduleFlow,
    ) { results ->
      val tasks: List<Task> = (results[5] + results[6]) as List<Task>
      val schedules: List<Schedule> = (results[4] + results[7]) as List<Schedule>

      ProgressQueryResult(
        activeDates = results[0] as List<ActiveDate>,
        progresses = results[1] as List<ScheduleProgress>,
        preferredTimes = results[2] as List<PreferredTime>,
        preferredDays = results[3] as List<PreferredDay>,
        schedules = schedules,
        tasks = tasks,
      )
    }
  }

  override fun queryTodaySchedule(nowDateTime: Long): Flow<ProgressQueryResult> {
    //val nowDateTime = getDateMillis(nowDateTime) //TODO: Consider to delete this line to optimize run time.

    val cal = Calendar.getInstance().apply {
      timeInMillis = nowDateTime
    }
    val day = cal[Calendar.DAY_OF_WEEK]
    println("queryTodaySchedule day = $day nowDateTime = $nowDateTime")

    val activeDateFlow = activeDateDao.getActiveDateByTime(nowDateTime)

    val preferredDayFlow = activeDateFlow.flatMapLatest { activeDates ->

      println("queryTodaySchedule activeDates = $activeDates")

      preferredDayDao.getDayByNowAndScheduleIds(
        scheduleIds = activeDates.map { it.scheduleId }.toSet(),
        nowDay = day,
      )
    }

    val preferredTimeFlow = preferredDayFlow.flatMapLatest { preferredDays ->
      println("queryTodaySchedule preferredDays = $preferredDays")
      preferredTimeDao.getTimeByScheduleIds(
        preferredDays.map { it.scheduleId }.toSet(),
      )
    }

    val scheduleProgressFlow = preferredDayFlow.flatMapLatest { preferredDays ->
      println("queryTodaySchedule preferredDays = $preferredDays")
      scheduleProgressDao.getActiveProgressListByScheduleIds(
        timestamp = nowDateTime,
        scheduleIds = preferredDays.map { it.scheduleId }.toSet(),
      )
    }

    val scheduleFlow = preferredDayFlow.flatMapLatest { preferredDays ->
      scheduleDao.getByIds(
        preferredDays.map { it.scheduleId }.toSet()
      )
    }

    val taskFlow = scheduleFlow.flatMapLatest { schedules ->
      println("queryTodaySchedule schedules = $schedules")
      taskDao.getByIds(
        schedules.map { it.taskId }.toSet()
      )
    }

    @Suppress(SuppressLiteral.UNCHECKED_CAST)
    return combine(
      activeDateFlow,
      preferredDayFlow,
      preferredTimeFlow, //2
      scheduleProgressFlow,
      scheduleFlow, //4
      taskFlow,
    ) { results ->
      ProgressQueryResult(
        activeDates = results[0] as List<ActiveDate>,
        preferredTimes = results[2] as List<PreferredTime>,
        preferredDays = results[1] as List<PreferredDay>,
        progresses = results[3] as List<ScheduleProgress>,
        schedules = results[4] as List<Schedule>,
        tasks = results[5] as List<Task>,
      )
    }
  }


  private fun calculateNecessaryRandomScheduleCount(prevScheduleCount: Int): Int = when {
    prevScheduleCount >= 8 -> 0 //max item 10
    prevScheduleCount == 0 -> 5
    else -> {
      when(prevScheduleCount) {
        in 1..3 -> 3
        in 4..6 -> 2
        else -> 1
      }
    }
  }
}