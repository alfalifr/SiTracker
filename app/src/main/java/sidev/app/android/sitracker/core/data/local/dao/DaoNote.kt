package sidev.app.android.sitracker.core.data.local.dao

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.util.RecommendationQuery
import sidev.app.android.sitracker.util.getTimeMillisInDay
import java.util.*

/*
Note:
  - Assume less item recommended, it will be better.
    So, the max recommended schedules will be 10.
  - If there is no schedule with active dates, preferred time / day, and progress yet,
    the max recommended schedules will be 5 (Of course users don't want to get many random stuff).

Dao mechanism:
I. Schedule Recommendation:
  1. Query `ActiveDate` based on now time.

  2. Query `PreferredTime` and `PreferredDay` based on:
     - Now time:
       To check whether 'now' is a preferred time / day.
     - Set of `scheduleId` from point 1:
       This is for schedules that HAVE preferred time and day,
       but 'now' isn't that preferred time / day.

  3. Query `ScheduleProgress` based on:
     - Now time:
       To check whether 'now' is a preferred time / day.

       This query doesn't include set of `scheduleId` from `ActiveDate`,
       because if it included them, that means `ScheduleProgress`
       that is expired will be queried. That doesn't mean anything
       useful. This will be a different case in `PreferredTime` and `PreferredDay`
       query where 'now' outside the preferred time / day
       won't mean the progress is expired.

  4. Query `Schedule` based on set of `scheduleId` from query result of any (OR condition):
     - `ActiveDate` (point 1),
     - Preferred times and days (point 2),
     - `ScheduleProgress` (point 3).

  5. Query any other `Schedule` that don't have active dates, preferred times and days, and progress yet
     will be queried with necessary calculated limit (isn't always same) so that
     the query result won't waste memory. The query condition will exclude (NOT IN) ids from (AND condition):
     - set of `scheduleId` from `ActiveDate` (point 1).
     - set of `scheduleId` from `PreferredTime` and `PreferredDay` (point 2).
     - set of `scheduleId` from `ScheduleProgress` (point 3).

  6. Query `Task` based on set of `taskId` from query result of `Schedule` (point 4).
     Of course `Task` with no `Schedule` won't be queried.

//TODO: (Recommendation mechanism) overall
Recommendation mechanism:
  [v] 1. Query from DAO. It includes basic filtering like now time. It means query result is already active ones (except for the random ones).
  [v] 2. Join every data type into `ProgressJoint`.
  [v] 3. Calculate the importance of each `ProgressJoint`.
  [ ] 4. Order by importance descending.
  [ ] 5. Get some data in the head of list with a random number.
 */

@ExperimentalCoroutinesApi
@FlowPreview
fun queryRecommendations(
  activeDateDao: ActiveDateDao,
  preferredTimeDao: PreferredTimeDao,
  preferredDayDao: PreferredDayDao,
  scheduleProgressDao: ScheduleProgressDao,
  scheduleDao: ScheduleDao,
  taskDao: TaskDao,
  now: Long = Date().time,
): Flow<RecommendationQuery> {

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

  val scheduleFlow = combine(activeDateFlow, progressFlow, prefTimeFlow, prefDayFlow) {
    activeDates, progresses, prefTimes, prefDays ->

    val ids = mutableSetOf<Int>()

    activeDates.mapTo(ids) { it.scheduleId }
    progresses.mapTo(ids) { it.scheduleId }
    prefTimes.mapTo(ids) { it.scheduleId }
    prefDays.mapTo(ids) { it.scheduleId }

    scheduleDao.getByTaskIds(ids)
  }.flattenConcat()

  val taskFlow = scheduleFlow.flatMapLatest { list ->
    taskDao.getByIds(
      list.map { it.taskId }.toSet()
    )
  }

  val randomTaskFlow = scheduleFlow.flatMapLatest { schedules ->
    val limit = calculateNecessaryRandomScheduleCount(schedules.size)
    taskDao.getOrderedByPriority(limit)
  }

  val randomScheduleFlow = randomTaskFlow.flatMapLatest { tasks ->
    scheduleDao.getByTaskIds(
      tasks.map { it.id }.toSet()
    )
  }

  @Suppress("UNCHECKED_CAST")
  return combine(
    activeDateFlow, progressFlow,
    prefTimeFlow, prefDayFlow,
    scheduleFlow, taskFlow,
    randomTaskFlow,
    randomScheduleFlow,
  ) { results ->
    val tasks: List<Task> = (results[5] + results[6]) as List<Task>
    val schedules: List<Schedule> = (results[4] + results[7]) as List<Schedule>

    RecommendationQuery(
      activeDates = results[0] as List<ActiveDate>,
      progresses = results[1] as List<ScheduleProgress>,
      preferredTimes = results[2] as List<PreferredTime>,
      preferredDays = results[3] as List<PreferredDay>,
      schedules = schedules,
      tasks = tasks,
    )
  }
}


fun calculateNecessaryRandomScheduleCount(prevScheduleCount: Int): Int = when {
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