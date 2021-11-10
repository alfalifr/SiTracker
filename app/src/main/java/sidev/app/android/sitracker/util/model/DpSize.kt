package sidev.app.android.sitracker.util.model

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Dp

data class DpSize(
  val width: Dp,
  val height: Dp,
)

fun MeasureScope.sizeToDp(size: Size): DpSize = DpSize(
  width = size.width.toDp(),
  height = size.height.toDp(),
)