package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import sidev.app.android.sitracker.core.data.local.model.PreferredTime
import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.data.local.model.Task

/*
 Required data:
 - Task
 - Schedule
 - ScheduleProgress
 -
 */

/**
 * Composed of [Task], [Schedule], and [PreferredTime]
 */
data class HomeLowerDetailData(
  val duration: Long,
  val startTime: String?,
  val priority: Int,
)

/**
 * Composed of [Task] and [ScheduleProgress]
 */
data class HomeTaskIconData(
  val image: ImageVector,
  val color: Color,
  val progress: Float,
)