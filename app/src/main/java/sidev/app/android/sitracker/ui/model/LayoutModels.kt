package sidev.app.android.sitracker.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.Placeable

data class ActionData(
  val icon: Painter,
  val name: String,
  val color: Color?,
  val onClick: () -> Unit,
)

data class PrefixPostfixPlaceables(
  val prefixPlaceable: Placeable?,
  val postfixPlaceable: Placeable?,
  val contentPlaceable: Placeable?,
) {
  val overallHeight: Int
    get() = if(
      prefixPlaceable == null
      && postfixPlaceable == null
      && contentPlaceable == null
    ) 0
    else maxOf(
      prefixPlaceable?.height ?: 0,
      postfixPlaceable?.height ?: 0,
      contentPlaceable?.height ?: 0,
    )
}