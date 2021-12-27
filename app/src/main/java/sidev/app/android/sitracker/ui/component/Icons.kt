package sidev.app.android.sitracker.ui.component

import android.accounts.AuthenticatorDescription
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.Const.textIconSizeStd
import sidev.app.android.sitracker.util.SuppressLiteral
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
  progressStrokeColor: Color = MaterialTheme.colors.primary,
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
        color = progressStrokeColor,
      )
    }

    Box(modifier = contentModifier) {
      content()
    }
  }
}


@SuppressLint(SuppressLiteral.MODIFIER_PARAMETER)
@Composable
fun IconProgression(
  mainColor: Color,
  modifier: Modifier = Modifier.size(Const.iconSizeDp),
  monoColor: Color = Color.White,
  contentPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  progressStrokeColor: Color? = null,
  content: @Composable BoxScope.(
    //bgColor: Color?,
    contentColor: Color,
  ) -> Unit,
) {
  var contentColor = mainColor
  var bgColor: Color? = null
  var usedProgressStrokeColor = progressStrokeColor

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

  if(usedProgressStrokeColor == null) {
    usedProgressStrokeColor = contentColor
  }

  IconBackground(
    modifier = modifier,
    color = bgColor,
    shape = bgShape,
    progress = progress,
    progressStrokeWidth = progressStrokeWidth,
    progressStrokeColor = usedProgressStrokeColor,
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
      icon = rememberVectorPainter(Icons.Rounded.Star),
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      //progress = progress,
    )

    IconProgressionPic(
      modifier = modifier,
      icon = rememberVectorPainter(Icons.Rounded.Star),
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      progress = progress,
      progressStrokeWidth = progressWidth,
    )
///*
    IconProgressionPic(
      modifier = modifier,
      icon = rememberVectorPainter(Icons.Rounded.Star),
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      iconMode = IconColorMode.COLORED_ICON,
      progress = progress,
      progressStrokeWidth = progressWidth,
    )

    IconProgressionPic(
      modifier = modifier,
      icon = rememberVectorPainter(Icons.Rounded.Star),
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      iconMode = IconColorMode.NO_BG,
      progress = progress,
      progressStrokeWidth = progressWidth,
    )
// */
/*
    IconProgressionPic(
      modifier = Modifier.size(2780.dp), //modifier,
      icon = rememberVectorPainter(Icons.Rounded.Star), //ColorPainter(Color.Blue),
      //iconSize = iconSize,
      mainColor = Color.Green,
      name = "Star boy",
      iconMode = IconColorMode.COLORED_ICON,
      //progress = progress,
      progressStrokeWidth = progressWidth,
    )
 */
  }
}


/**
 * Component that shows task item as icon and its color.
 *
 * [monoColor] should be white or black, but doesn't throw exception if it isn't either of them.
 * [name] can be null if this icon have a pure decorative mean, but doesn't throw exception not.
 */
@SuppressLint(SuppressLiteral.MODIFIER_PARAMETER)
@Composable
fun IconProgressionPic(
  icon: Painter,
  mainColor: Color,
  modifier: Modifier = Modifier.size(Const.iconSizeDp),
  monoColor: Color = Color.White,
  name: String?,
  iconPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  progressStrokeColor: Color? = null,
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
    progressStrokeColor = progressStrokeColor,
  ) { contentColor ->
    Icon(
      painter = icon,
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

@SuppressLint(SuppressLiteral.MODIFIER_PARAMETER)
@Composable
fun IconProgressionText(
  text: String,
  mainColor: Color,
  modifier: Modifier = Modifier.size(Const.iconSizeDp),
  monoColor: Color = Color.White,
  textSize: TextUnit = TextUnit.Unspecified,
  textPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  progressStrokeColor: Color? = null,
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
    progressStrokeColor = progressStrokeColor,
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


@SuppressLint(SuppressLiteral.MODIFIER_PARAMETER)
@Composable
fun IconProgressionAdapt(
  icon: Painter?,
  text: String?,
  mainColor: Color,
  modifier: Modifier = Modifier.size(Const.iconSizeDp),
  textSize: TextUnit = TextUnit.Unspecified,
  monoColor: Color = Color.White,
  contentPadding: PaddingValues? = null,
  iconMode: IconColorMode = IconColorMode.COLORED_BG,
  bgShape: Shape = CircleShape,
  progress: Float? = null,
  progressStrokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
  progressStrokeColor: Color? = null,
) {
  when {
    icon != null -> IconProgressionPic(
      icon = icon,
      name = text,
      mainColor = mainColor,
      monoColor = monoColor,
      modifier = modifier,
      iconPadding = contentPadding,
      iconMode = iconMode,
      bgShape = bgShape,
      progress = progress,
      progressStrokeWidth = progressStrokeWidth,
      progressStrokeColor = progressStrokeColor,
    )
    text == null -> throw IllegalArgumentException(
      "Parameter `icon` and `text` can't be both null"
    )
    else -> IconProgressionText(
      text = text,
      textSize = textSize,
      mainColor = mainColor,
      monoColor = monoColor,
      modifier = modifier,
      textPadding = contentPadding,
      iconMode = iconMode,
      bgShape = bgShape,
      progress = progress,
      progressStrokeWidth = progressStrokeWidth,
      progressStrokeColor = progressStrokeColor,
    )
  }
}




@Composable
@Preview
private fun IconWithText_preview() {
  IconWithText(
    icon = rememberVectorPainter(Icons.Rounded.Person),
    text = "Hello",
  )
}
@Composable
@Preview
private fun IconWithTexts_preview() {
  IconWithTexts(
    icon = rememberVectorPainter(Icons.Rounded.Person),
    iconContentDescription = null,
    "Hello",
    "Ho",
    "Bro",
  )
}

@Composable
fun IconWithTexts(
  icon: Painter,
  iconContentDescription: String?,
  vararg texts: String,
  modifier: Modifier = Modifier,
  iconModifier: Modifier = Modifier,
  textModifier: ((index: Int) -> Modifier)? = null,
  spaceBetween: Dp = 10.dp,
  iconSize: Dp? = null,
  textSize: TextUnit = TextUnit.Unspecified,
  //contentDescription: String = text,
) {
  val alignment = if(texts.size == 1) Alignment.CenterVertically
    else Alignment.Top

  Row(
    modifier = modifier,
    /*
    modifier
      .semantics(true) {
        this.contentDescription = contentDescription
      },
     */
    verticalAlignment = alignment,
  ) {
    val usedIconSize = iconSize
      ?: with(LocalDensity.current) {
        //(MaterialTheme.typography.body1.fontSize * 1.5).toDp()
        textIconSizeStd
      }
    Icon(
      modifier = iconModifier.size(usedIconSize),
        //.background(Color.Blue),
      painter = icon,
      tint = MaterialTheme.typography.body1.color,
      contentDescription = iconContentDescription,
    )
    Spacer(
      modifier = Modifier
        .size(width = spaceBetween, height = 0.dp)
    )
    Column(
      verticalArrangement = Arrangement.spacedBy(Const.stdSpacerDp),
    ) {
      for(i in texts.indices) {
        Text(
          text = texts[i],
          fontSize = textSize,
          modifier = textModifier?.invoke(i) ?: Modifier,
          //modifier = Modifier.clearAndSetSemantics {  }
        )
      }
    }
  }
}

@Composable
fun IconWithText(
  icon: Painter,
  text: String,
  modifier: Modifier = Modifier,
  spaceBetween: Dp = 10.dp,
  iconSize: Dp? = null,
  textSize: TextUnit = TextUnit.Unspecified,
  contentDescription: String = text,
) = IconWithTexts(
  icon = icon,
  iconContentDescription = null,
  texts = arrayOf(text),
  modifier = modifier
    .semantics(true) {
      this.contentDescription = contentDescription
    },
  spaceBetween = spaceBetween,
  iconSize = iconSize,
  textSize = textSize,
  textModifier = { Modifier.clearAndSetSemantics {  } },
)

/*
@Composable
fun IconWithTexts(
  icon: Painter,
  iconContentDescription: String,
  vararg texts: String,
  modifier: Modifier = Modifier,
  iconModifier: Modifier = Modifier,
  textModifier: ((index: Int) -> Modifier)? = null,
  spaceBetween: Dp = 10.dp,
  iconSize: Dp? = null,
  textSize: TextUnit = TextUnit.Unspecified,
) = _IconWithText(
  icon = icon,
  iconContentDescription = iconContentDescription,
  texts = texts,
  modifier = modifier,
  spaceBetween = spaceBetween,
  iconModifier = iconModifier,
  textModifier = textModifier,
  iconSize = iconSize,
  textSize = textSize,
)
 */