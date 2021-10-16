package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


enum class TaskIconMode {
  /**
   * Task icon with background. Background is colored
   * with chosen color and the icon color is black or white.
   */
  COLORED_BG,

  /**
   * Task icon with background. Icon is colored
   * with chosen color and the background color is black or white.
   */
  COLORED_ICON,

  /**
   * Task icon with no bacground.
   */
  NO_BG,
}


/**
 * Component that shows task item as icon and its color.
 *
 * [monoColor] should be white or black, but doesn't throw exception if it isn't either of them.
 */
@Composable
fun TaskItem(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  color: Color,
  monoColor: Color = Color.White,
  name: String,
  iconPadding: PaddingValues = PaddingValues(15.dp),
  iconMode: TaskIconMode = TaskIconMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Double? = null, //TODO: add progress to this view
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {

    var iconColor = color

    if(iconMode == TaskIconMode.COLORED_BG
      || iconMode == TaskIconMode.COLORED_ICON) {

      var bgColor = monoColor

      if(iconMode == TaskIconMode.COLORED_BG) {
        iconColor = monoColor
        bgColor = color
      }
      Image(
        painter = ColorPainter(bgColor),
        contentDescription = null,
        modifier = Modifier.clip(bgShape),
      )
    }
    Icon(
      imageVector = icon,
      contentDescription = name,
      tint = iconColor,
      modifier = Modifier.padding(iconPadding)
    )
  }
}