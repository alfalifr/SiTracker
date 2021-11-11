package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import sidev.app.android.sitracker.util.Const


@Composable
private fun AppSurface(
  shape: Shape,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colors.surface,
  shadowElevation: Dp = Const.surfaceShadowElevationDp,
  content: @Composable (BoxScope.() -> Unit)? = null,
) {
  if(content == null) {
    Box(
      modifier = modifier
        .graphicsLayer {
          this.shape = shape
          this.shadowElevation =
            shadowElevation.toPx()
          clip = true
        }
        .background(color = color),
    )
  } else {
    Box(
      modifier = modifier
        .graphicsLayer {
          this.shape = shape
          this.shadowElevation =
            shadowElevation.toPx()
          clip = true
        }
        .background(color = color),
      content = content,
    )
  }
}


@Composable
fun LargeSurface(
  modifier: Modifier = Modifier,
  content: @Composable (BoxScope.() -> Unit)? = null,
) = AppSurface(
  shape = MaterialTheme.shapes.large,
  modifier = modifier,
  content = content,
)

@Composable
fun MediumSurface(
  modifier: Modifier = Modifier,
  content: @Composable (BoxScope.() -> Unit)? = null,
) = AppSurface(
  shape = MaterialTheme.shapes.medium,
  modifier = modifier,
  content = content,
)

@Composable
fun SmallSurface(
  modifier: Modifier = Modifier,
  content: @Composable (BoxScope.() -> Unit)? = null,
) = AppSurface(
  shape = MaterialTheme.shapes.small,
  modifier = modifier,
  content = content,
)