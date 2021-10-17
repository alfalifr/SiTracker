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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sidev.app.android.sitracker.util.maxSquareSideLen


enum class IconColorMode {
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

@Composable
@Preview
private fun IconBackground_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp)
  ) {
    val sizeMod = Modifier.size(250.dp)

    IconBackground(
      color = Color.Green,
      shape = CircleShape,
      progress = null,
      modifier = sizeMod,
    ) { }

    IconBackground(
      color = Color.Green,
      shape = CircleShape,
      progress = 64 / 100f,
      progressStrokeWidth = 20.dp,
      modifier = sizeMod,
    ) { }

    IconBackground(
      color = null,
      shape = CircleShape,
      progress = 64 / 100f,
      progressStrokeWidth = 20.dp,
      modifier = sizeMod,
    ) { }
  }
}

@Composable
private fun IconBackground(
  color: Color?,
  shape: Shape?,
  progress: Float?,
  modifier: Modifier = Modifier,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  contentPadding: PaddingValues? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  BoxWithConstraints(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    var contentModifier = Modifier.size(maxSquareSideLen * 95 / 100)

    if(color != null && shape != null) {
      var bgModifier = Modifier.size(maxSquareSideLen)
      if(progress != null) {
        bgModifier = bgModifier.padding(progressStrokeWidth)
      }
      bgModifier = bgModifier.clip(shape)

      Image(
        painter = ColorPainter(color),
        contentDescription = null,
        modifier = bgModifier,
      )

      contentModifier = contentModifier.padding(
        contentPadding ?: PaddingValues(
          maxSquareSideLen / 20
        )
      )
    }

    contentModifier = contentModifier.padding(progressStrokeWidth)

    if(progress != null) {
      CircularProgressIndicator(
        modifier = Modifier.size(maxSquareSideLen),
        progress = progress,
        strokeWidth = progressStrokeWidth,
      )
    }

    Box(modifier = contentModifier) {
      content()
    }
  }
}


@SuppressLint("ModifierParameter")
@Composable
fun IconProgression(
  mainColor: Color,
  modifier: Modifier = Modifier.size(50.dp),
  monoColor: Color = Color.White,
  contentPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  content: @Composable BoxScope.(
    //bgColor: Color?,
    contentColor: Color,
  ) -> Unit,
) {
  var contentColor = mainColor
  var bgColor: Color? = null

  when(iconMode) {
    IconColorMode.COLORED_BG -> {
      contentColor = monoColor
      bgColor = mainColor
    }
    IconColorMode.COLORED_ICON -> {
      bgColor = monoColor
    }
    else -> { /* Nothing */ }
  }

  IconBackground(
    modifier = modifier,
    color = bgColor,
    shape = bgShape,
    progress = progress,
    progressStrokeWidth = progressStrokeWidth,
    contentPadding = contentPadding,
  ) {
    content(contentColor)
  }
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

    IconProgressionPic(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      //progress = progress,
    )

    IconProgressionPic(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      iconMode = IconColorMode.COLORED_ICON,
      progress = progress,
      progressStrokeWidth = progressWidth,
    )

    IconProgressionPic(
      modifier = modifier,
      icon = Icons.Rounded.Star,
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      iconMode = IconColorMode.NO_BG,
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
fun IconProgressionPic(
  icon: ImageVector,
  mainColor: Color,
  modifier: Modifier = Modifier.size(50.dp),
  monoColor: Color = Color.White,
  name: String?,
  iconPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
  IconProgression(
    mainColor = mainColor,
    monoColor = monoColor,
    modifier = modifier,
    contentPadding = iconPadding,
    iconMode = iconMode,
    bgShape = bgShape,
    progress = progress,
    progressStrokeWidth = progressStrokeWidth,
  ) { contentColor ->
    Icon(
      imageVector = icon,
      contentDescription = name,
      tint = contentColor,
      modifier = Modifier.fillMaxSize(),
      //modifier = iconModifier,
    )
  }
}


@Composable
@Preview
private fun IconProgressionText_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    val progressWidth = 15.dp
    val progress: Float? = 74 / 100f
    val textSize = 40.sp
    val sizeMod = Modifier.size(200.dp)

    IconProgressionText(
      text = "Hello",
      mainColor = Color.Green,
      textSize = textSize,
      progressStrokeWidth = progressWidth,
      iconMode = IconColorMode.COLORED_BG,
      modifier = sizeMod,
    )

    IconProgressionText(
      text = "Hello",
      mainColor = Color.Green,
      textSize = textSize,
      progressStrokeWidth = progressWidth,
      iconMode = IconColorMode.COLORED_ICON,
      progress = progress,
      modifier = sizeMod,
    )

    IconProgressionText(
      text = "Hello",
      mainColor = Color.Green,
      textSize = textSize,
      progressStrokeWidth = progressWidth,
      iconMode = IconColorMode.NO_BG,
      progress = progress,
      modifier = sizeMod,
    )
  }
}

@SuppressLint("ModifierParameter")
@Composable
fun IconProgressionText(
  text: String,
  mainColor: Color,
  modifier: Modifier = Modifier.size(50.dp),
  monoColor: Color = Color.White,
  textSize: TextUnit = TextUnit.Unspecified,
  textPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
  IconProgression(
    mainColor = mainColor,
    monoColor = monoColor,
    modifier = modifier,
    contentPadding = textPadding,
    iconMode = iconMode,
    bgShape = bgShape,
    progress = progress,
    progressStrokeWidth = progressStrokeWidth,
  ) { contentColor ->
    Text(
      text = text,
      color = contentColor,
      fontSize = textSize,
      textAlign = TextAlign.Center,
      modifier = Modifier.align(Alignment.Center),
    )
  }
}


@Composable
@Preview
private fun IconWithText_preview() {
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