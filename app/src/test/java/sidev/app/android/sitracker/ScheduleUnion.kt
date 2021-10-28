package sidev.app.android.sitracker

import sidev.app.android.sitracker.core.data.local.model.*

/*
data class ScheduleUnion(
  val task: Task,
  val schedules: List<Schedule>,
  val activeDates: List<ActiveDate>,
  val preferredDays: List<PreferredDay>,
  val preferredTimes: List<PreferredTime>,
  val progresses: List<ScheduleProgress>,
) {
  fun produceProgressImportanceFactors(): List<ProgressImportanceFactor> {
    val list = mutableListOf<ProgressImportanceFactor>()

    for(progress in progresses) {
      val pt = schedules.find { it.id == progress.scheduleId }
        ?.totalProgress
        ?: continue

      val tdRanges = activeDates.map {
        UnclosedLongRange(it.startDate, it.endDate)
      }
      val tPrefTimeRanges = preferredTimes.map {
        UnclosedLongRange(it.startTime, it.endTime)
      }
      val tPrefDay = preferredDays.map {
        it.dayInWeek
      }

      ProgressImportanceFactor(
        tdRanges = tdRanges,
        tPrefTimeRanges = tPrefTimeRanges,
        tPrefDays = tPrefDay,
        ti0 = progress.startTimestamp,
        ti1 = progress.endTimestamp,
        pt = pt,
        pr = task.priority,
      )
    }
    return list
  }
}
 */