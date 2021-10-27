package sidev.app.android.sitracker.util

import sidev.app.android.sitracker.core.data.local.model.*

data class RecommendationQuery(
  val activeDates: List<ActiveDate>,
  val preferredTimes: List<PreferredTime>,
  val preferredDays: List<PreferredDay>,
  val progresses: List<ScheduleProgress>,
  val schedules: List<Schedule>,
  val tasks: List<Task>,
)
