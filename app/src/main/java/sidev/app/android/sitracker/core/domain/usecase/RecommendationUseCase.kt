@file:OptIn(
  ExperimentalCoroutinesApi::class,
  FlowPreview::class,
)
package sidev.app.android.sitracker.core.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenConcat
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.SuppressLiteral
import sidev.app.android.sitracker.util.getTimeMillisInDay
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.*

/*
Note:
  - Assume less item recommended, it will be better.
    So, the max recommended schedules will be 10.
  - If there is no schedule with active dates, preferred time / day, and progress yet,
    the max recommended schedules will be 5 (Of course users don't want to get many random stuff).

TODO: Delete this redundant part of note ('Dao mechanism' part).
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
       To check whether `ScheduleProgress` is active by 'now'.

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
  [v] 4. Order by importance descending.
  [v] 5. Get some data in the head of list with a random number.
 */


interface RecommendationUseCase {

  /**
   * Map list of [ProgressJoint] into list of [ProgressImportanceJoint].
   */
  fun getProgressImportance(
    progressJoints: List<ProgressJoint>,
  ): List<ProgressImportanceJoint>

  /**
   * Calculate importance of each [ProgressImportance] in [importanceJoints] list
   * then order the list by importance descending finally take some of
   * the head.
   *
   * This method returns list of sorted [ProgressImportance].
   */
  fun getRecommendedList(
    importanceJoints: List<ProgressImportanceJoint>,
    now: Long = Date().time,
    progressGetter: (ProgressJoint) -> Long = {
       it.progress.actualProgress
    },
  ): List<ProgressImportanceJoint>
}


class RecommendationUseCaseImpl: RecommendationUseCase {

  /**
   * Map list of [ProgressJoint] into list of [ProgressImportance].
   */
  override fun getProgressImportance(
    progressJoints: List<ProgressJoint>
  ): List<ProgressImportanceJoint> = progressJoints.map { joint ->
    ProgressImportanceJoint(
      joint = joint,
      factor = joint.run {
        ProgressImportanceFactor(
          tdRanges = activeDates.map { UnclosedLongRange(it.startDate, it.endDate) },
          tPrefTimeRanges = preferredTimes.map { UnclosedLongRange(it.startTime, it.endTime) },
          tPrefDays = preferredDays.map { it.dayInWeek },
          ti0 = progress.startTimestamp,
          ti1 = progress.endTimestamp,
          pt = schedule.totalProgress,
          pr = task.priority,
        )
      },
    )
  }

  /**
   * Calculate importance of each [ProgressImportance] in [progressJoints] list
   * then order the list by importance descending finally take some of
   * the head.
   *
   * This method returns list of sorted [ProgressImportance].
   */
  override fun getRecommendedList(
    importanceJoints: List<ProgressImportanceJoint>,
    now: Long,
    progressGetter: (ProgressJoint) -> Long,
  ): List<ProgressImportanceJoint> {
    val comparator = mutableMapOf<Int, Double>()

    return importanceJoints
      .sortedByDescending { importance ->
        val progress = importance.joint.progress
        comparator[progress.id]
          ?: importance.getImportance(now, progress.actualProgress).also {
            comparator[progress.id] = it
          }
      }
      .filter { comparator[it.joint.progress.id]!! > Const.importanceScoreLowerLimit  }
      .take((7..10).random())
      //.subList(0, (7..10).random())
  }
}