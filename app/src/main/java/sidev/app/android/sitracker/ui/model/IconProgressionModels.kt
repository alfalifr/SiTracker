package sidev.app.android.sitracker.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.data.local.model.Task
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.component.IconProgressionText


interface IconPicUiData {
  val image: Painter
  val color: Color
}

data class IconPicUiDataImpl(
  override val image: Painter,
  override val color: Color,
): IconPicUiData

fun IconPicUiData(
  image: Painter,
  color: Color,
): IconPicUiData = IconPicUiDataImpl(
  image, color
)



sealed class IconProgressionUiData(
  open val color: Color,
  open val progress: Float?,
)

/**
 * Model for [IconProgressionPic].
 * Composed of [Task] and [ScheduleProgress].
 */
data class IconProgressionPicUiData(
  override val image: Painter,
  override val color: Color,
  override val progress: Float?,
): IconProgressionUiData(
  color = color,
  progress = progress,
), IconPicUiData

/**
 * Model for [IconProgressionText].
 */
data class IconProgressionTextUiData(
  val text: String,
  override val color: Color,
  override val progress: Float?,
): IconProgressionUiData(
  color = color,
  progress = progress,
)

/**
 * Model for [IconProgressionText] but the text
 * is from [progress] float.
 */
data class IconProgressionFloatUiData(
  override val color: Color,
  override val progress: Float,
): IconProgressionUiData(
  color = color,
  progress = progress,
)