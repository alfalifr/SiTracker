package sidev.app.android.sitracker.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.data.local.model.Task

/**
 * Composed of [Task] and [ScheduleProgress]
 */
data class IconProgressionUiData(
  val image: Painter,
  val color: Color,
  val progress: Float,
)