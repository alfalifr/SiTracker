package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
  val duration: String,
  val startTime: String?,
  val priority: String,
)