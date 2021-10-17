package sidev.app.android.sitracker.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
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
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.util.maxSquareSideLen


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
private fun IconItem_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    val iconSize = 200.dp
    val modifier = Modifier.size(iconSize)
    val progress: Float? = 68f / 100f
    val progressWidth = 15.dp

    IconItem(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
      //progress = progress,
    )

    IconItem(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
      iconMode = TaskIconMode.COLORED_ICON,
      progress = progress,
      progressStrokeWidth = progressWidth,
    )

    IconItem(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      color = Color.Green,
      name = "Star boy",
      iconMode = TaskIconMode.NO_BG,
      progress = progress,
      progressStrokeWidth = progressWidth,
    )
  }
}

//TODO: Make the text version of it (instead of icon) and the clickable version too.
/**
 * Component that shows task item as icon and its color.
 *
 * [monoColor] should be white or black, but doesn't throw exception if it isn't either of them.
 * [name] can be null if this icon have a pure decorative mean, but doesn't throw exception not.
 */
@SuppressLint("ModifierParameter")
@Composable
fun IconItem(
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier.size(50.dp),
  monoColor: Color = Color.White,
  name: String?,
  //iconSize: Dp = 50.dp,
  iconPadding: PaddingValues? = null, //PaddingValues(iconSize / 20),
  iconMode: TaskIconMode = TaskIconMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null, //TODO: add progress to this view
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
  BoxWithConstraints(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    var iconColor = color
    var iconModifier = Modifier.size(maxSquareSideLen * 95 / 100)

    if(iconMode == TaskIconMode.COLORED_BG
      || iconMode == TaskIconMode.COLORED_ICON) {

      iconModifier = iconModifier.padding(
        iconPadding ?: PaddingValues(
          maxSquareSideLen / 20
        )
      )

      var bgColor = monoColor

      if(iconMode == TaskIconMode.COLORED_BG) {
        iconColor = monoColor
        bgColor = color
      }
      Image(
        painter = ColorPainter(bgColor),
        contentDescription = null,
        modifier = Modifier
          .padding(progressStrokeWidth)
          .clip(bgShape),
      )
    }

    iconModifier = iconModifier.padding(progressStrokeWidth)

    if(progress != null) {
      CircularProgressIndicator(
        modifier = Modifier.size(maxSquareSideLen),
        progress = progress,
        strokeWidth = progressStrokeWidth,
      )
    }

    Icon(
      imageVector = icon,
      contentDescription = name,
      tint = iconColor,
      modifier = iconModifier,
    )
  }
}


@Composable
@Preview
fun IconWithText_preview() {
  IconWithText(
    icon = Icons.Rounded.Person,
    text = "Hello",
  )
}

@Composable
fun IconWithText(
  icon: ImageVector,
  text: String,
  modifier: Modifier = Modifier,
  spaceBetween: Dp = 10.dp,
  iconSize: Dp? = null,
  textSize: TextUnit = TextUnit.Unspecified,
  contentDescription: String = text,
) {
  Row(
    modifier = modifier
      .semantics(true) {
        this.contentDescription = contentDescription
      },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      modifier =
        if(iconSize != null) Modifier.size(iconSize)
        else Modifier,
      imageVector = icon,
      contentDescription = null,
    )
    Spacer(
      modifier = Modifier
        .size(width = spaceBetween, height = 0.dp)
    )
    Text(
      text = text,
      fontSize = textSize,
      modifier = Modifier.clearAndSetSemantics {  }
    )
  }
}