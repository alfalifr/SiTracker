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
   *    5. Query `Task` based on set of `taskId` from query result of `Schedule` (point 4).
   *       Of course `Task` with no `Schedule` won't be queried.
   *
   *    6. Query any other `Task` whose `Schedule` doesn't have active dates, preferred times and days, and progress yet
   *       will be queried with necessary calculated limit (isn't always same) so that
   *       the query result won't waste memory. The query condition will exclude (NOT IN) ids from (AND condition):
   *       - set of `scheduleId` from `ActiveDate` (point 1).
   *       - set of `scheduleId` from `PreferredTime` and `PreferredDay` (point 2).
   *       - set of `scheduleId` from `ScheduleProgress` (point 3).
   *
   *       The query result is sorted descending by `Task` priority.
   *
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

  /**
   * Query every data related to a [Task] with [taskId].
   * This method guarantees the resulting list of [ActiveDate] and [ScheduleProgress]
   * are ordered by the most recent ones defined by the biggest
   * [ActiveDate.startDate] and [ScheduleProgress.startTimestamp].
   */
  fun queryTaskDetail(
    taskId: Int,
  ): Flow<ProgressQueryResult>

  /**
   * Query every data related to a [Schedule] with [scheduleId].
   */
  fun queryScheduleDetail(
    scheduleId: Int,
  ): Flow<ProgressQueryResult>

  /**
   * Query every data related to a [Schedule] with [scheduleId]
   * for count down page.
   */
  fun queryScheduleForCountDown(
    scheduleId: Int,
    timestamp: Long,
  ): Flow<ProgressQueryResult>
}


class QueryUseCaseImpl(
  private val activeDateDao: ActiveDateDao,
  private val preferredTimeDao: PreferredTimeDao,
  private val preferredDayDao: PreferredDayDao,
  private val scheduleProgressDao: ScheduleProgressDao,
  private val scheduleDao: ScheduleDao,
  private val taskDao: TaskDao,
  private val intervalTypeDao: IntervalTypeDao,
  private val progressTypeDao: ProgressTypeDao,
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

    /*
    These random tasks and schedules don't have active dates, preferred times and days, and progress yet.
     */
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

    val intervalTypeFlow = combine(
      scheduleFlow,
      randomScheduleFlow,
    ) { schedules, randomSchedules ->
      intervalTypeDao.getByIds(
        (schedules + randomSchedules).map { it.intervalId }.toSet()
      )
    }.flattenConcat()

    val progressTypeFlow = combine(
      scheduleFlow,
      randomScheduleFlow,
    ) { schedules, randomSchedules ->
      progressTypeDao.getByIds(
        (schedules + randomSchedules).map { it.progressTypeId }.toSet()
      )
    }.flattenConcat()

    @Suppress(SuppressLiteral.UNCHECKED_CAST)
    return combine(
      activeDateFlow, progressFlow, //1
      prefTimeFlow, prefDayFlow, //3
      scheduleFlow, taskFlow, //5
      randomTaskFlow,
      randomScheduleFlow, //7
      intervalTypeFlow,
      progressTypeFlow, //9
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
        intervalTypes = results[8] as List<IntervalType>,
        progressTypes = results[9] as List<ProgressType>,
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

    val intervalTypeFlow = scheduleFlow.flatMapLatest { schedules ->
      intervalTypeDao.getByIds(
        schedules.map { it.intervalId }.toSet()
      )
    }
    val progressTypeFlow = scheduleFlow.flatMapLatest { schedules ->
      progressTypeDao.getByIds(
        schedules.map { it.progressTypeId }.toSet()
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
      intervalTypeFlow, //6
      progressTypeFlow,
    ) { results ->
      ProgressQueryResult(
        activeDates = results[0] as List<ActiveDate>,
        preferredTimes = results[2] as List<PreferredTime>,
        preferredDays = results[1] as List<PreferredDay>,
        progresses = results[3] as List<ScheduleProgress>,
        schedules = results[4] as List<Schedule>,
        tasks = results[5] as List<Task>,
        intervalTypes = results[6] as List<IntervalType>,
        progressTypes = results[7] as List<ProgressType>,
      )
    }
  }

  /**
   * Query every data related to a [Task] with [taskId].
   * This method guarantees the resulting list of [ActiveDate] and [ScheduleProgress]
   * are ordered by the most recent ones defined by the biggest
   * [ActiveDate.startDate] and [ScheduleProgress.startTimestamp].
   */
  override fun queryTaskDetail(taskId: Int): Flow<ProgressQueryResult> {
    val taskFlow = taskDao.getById(taskId).map { task ->
      task?.let { listOf(it) } ?: emptyList()
    }

    val scheduleFlow = scheduleDao.getByTaskId(taskId)

    val scheduleProgressFlow = scheduleFlow.flatMapLatest { schedules ->
      scheduleProgressDao.getRecentByScheduleIds(
        schedules.map { it.id }.toSet()
      )
    }

    val activeDateFlow = scheduleFlow.flatMapLatest { schedules ->
      activeDateDao.getRecentByScheduleIds(
        schedules.map { it.id }.toSet()
      )
    }

    val preferredDayFlow = scheduleFlow.flatMapLatest { schedules ->
      preferredDayDao.getDayByScheduleIds(
        schedules.map { it.id }.toSet()
      )
    }
    val preferredTimeFlow = scheduleFlow.flatMapLatest { schedules ->
      preferredTimeDao.getTimeByScheduleIds(
        schedules.map { it.id }.toSet()
      )
    }

    val intervalTypeFlow = scheduleFlow.flatMapLatest { schedules ->
      intervalTypeDao.getByIds(
        schedules.map { it.intervalId }.toSet()
      )
    }
    val progressTypeFlow = scheduleFlow.flatMapLatest { schedules ->
      progressTypeDao.getByIds(
        schedules.map { it.progressTypeId }.toSet()
      )
    }

    return combine(
      taskFlow, //0
      scheduleFlow,
      scheduleProgressFlow, //2
      activeDateFlow,
      preferredDayFlow, //4
      preferredTimeFlow,
      intervalTypeFlow, //6
      progressTypeFlow,
    ) { results ->
      @Suppress(SuppressLiteral.UNCHECKED_CAST)
      ProgressQueryResult(
        tasks = results[0] as List<Task>,
        schedules = results[1] as List<Schedule>,
        progresses = results[2] as List<ScheduleProgress>,
        activeDates = results[3] as List<ActiveDate>,
        preferredDays = results[4] as List<PreferredDay>,
        preferredTimes = results[5] as List<PreferredTime>,
        intervalTypes = results[6] as List<IntervalType>,
        progressTypes = results[7] as List<ProgressType>,
      )
    }
  }

  /**
   * Query every data related to a [Schedule] with [scheduleId].
   */
  override fun queryScheduleDetail(scheduleId: Int): Flow<ProgressQueryResult> {
    val scheduleFlow = scheduleDao.getById(scheduleId).filterNotNull()

    val taskFlow = scheduleFlow.flatMapLatest {
      taskDao.getById(it.taskId)
    }.filterNotNull()

    val progressFlow = scheduleFlow.flatMapLatest {
      scheduleProgressDao.getLatestProgressOfSchedule(it.id)
    }

    val activeDateFlow = scheduleFlow.flatMapLatest {
      activeDateDao.getRecentByScheduleId(it.id)
    }

    val preferredDay = scheduleFlow.flatMapLatest {
      preferredDayDao.getDayBySchedule(it.id)
    }

    val preferredTime = scheduleFlow.flatMapLatest {
      preferredTimeDao.getTimeBySchedule(it.id)
    }

    val intervalTypeFlow = scheduleFlow.flatMapLatest { schedule ->
      intervalTypeDao.getById(
        schedule.intervalId
      )
    }.filterNotNull()
    val progressTypeFlow = scheduleFlow.flatMapLatest { schedule ->
      progressTypeDao.getById(
        schedule.progressTypeId
      )
    }.filterNotNull()

    return combine(
      scheduleFlow, //0
      taskFlow,
      progressFlow, //2
      activeDateFlow,
      preferredDay, //4
      preferredTime,
      intervalTypeFlow, //6
      progressTypeFlow,
    ) { results ->

      println("""
        queryScheduleDetail results = ${results.joinToString(
          prefix = "[\n",
          postfix = "\n]",
          separator = "\n",
        )}
      """.trimIndent())
      @Suppress(SuppressLiteral.UNCHECKED_CAST)
      ProgressQueryResult(
        schedules = listOf(results[0] as Schedule),
        tasks = listOf(results[1] as Task),
        progresses = (results[2] as ScheduleProgress?)?.let { listOf(it) } ?: emptyList(),
        activeDates = results[3] as List<ActiveDate>,
        preferredDays = results[4] as List<PreferredDay>,
        preferredTimes = results[5] as List<PreferredTime>,
        intervalTypes = listOf(results[6] as IntervalType),
        progressTypes = listOf(results[7] as ProgressType),
      )
    }
  }

  /**
   * Query every data related to a [Schedule] with [scheduleId]
   * for count down page.
   */
  override fun queryScheduleForCountDown(
    scheduleId: Int,
    timestamp: Long,
  ): Flow<ProgressQueryResult> {
    val scheduleFlow = scheduleDao.getById(scheduleId).filterNotNull()

    val taskFlow = scheduleFlow.flatMapLatest {
      taskDao.getById(it.taskId)
    }.filterNotNull()

    val scheduleProgressFlow = scheduleFlow.flatMapLatest {
      scheduleProgressDao.getLatestActiveProgressOfSchedule(
        scheduleId = it.id,
        timestamp = timestamp,
      )
    }

    return combine(
      scheduleFlow,
      taskFlow,
      scheduleProgressFlow,
    ) { schedule, task, progress ->
      ProgressQueryResult(
        schedules = listOf(schedule),
        tasks = listOf(task),
        progresses = progress?.let { listOf(it) } ?: emptyList(),
        activeDates = emptyList(),
        preferredDays = emptyList(),
        preferredTimes = emptyList(),
        intervalTypes = emptyList(),
        progressTypes = emptyList(),
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