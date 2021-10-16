package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.component.TaskItem


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


@Preview
@Composable
private fun TaskItem_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    val iconSize = 200.dp

    TaskItem(
      icon = Icons.Rounded.Star,
      iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
    )

    TaskItem(
      icon = Icons.Rounded.Star,
      iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
      iconMode = TaskIconMode.COLORED_ICON,
    )

    TaskItem(
      icon = Icons.Rounded.Star,
      iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
      iconMode = TaskIconMode.NO_BG,
    )
  }
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
  iconSize: Dp = 50.dp,
  iconPadding: PaddingValues = PaddingValues(iconSize / 20),
  iconMode: TaskIconMode = TaskIconMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Double? = null, //TODO: add progress to this view
) {
  Box(
    modifier = modifier.size(iconSize),
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
      modifier = Modifier
        .size(iconSize * 95 / 100)
        .padding(iconPadding),
    )
  }
}