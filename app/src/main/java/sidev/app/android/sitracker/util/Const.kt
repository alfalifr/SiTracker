package sidev.app.android.sitracker.util

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.core.domain.model.ProgressImportanceFactor
import sidev.app.android.sitracker.ui.theme.Red

object Const {
  const val iconSize = 50
  val iconSizeDp = iconSize.dp

  const val contentPadding = 15
  val contentPaddingDp = contentPadding.dp

  const val surfaceShadowElevation = 7
  val surfaceShadowElevationDp = surfaceShadowElevation.dp


  const val stdSpacer = 15
  val stdSpacerDp = stdSpacer.dp

  const val stdBorderWidth = 5
  val stdBorderWidthDp = stdBorderWidth.dp

  val Density.textIconSizeStd: Dp
    @Composable
    get() = (MaterialTheme.typography.body1.fontSize * 1.5).toDp()

  const val iconSizeStd = 25
  val iconSizeStdDp = iconSizeStd.dp


  const val scoreScale = 10 //100
  const val calendarDayInMonth = 7 * 5 //7 days, 5 weeks.
  /**
   * Any [ProgressImportanceFactor.calculateImportance] result
   * lower or equals to this constant it means that progress
   * won't be displayed.
   */
  const val importanceScoreLowerLimit = -100.0

  val redHex: String
    get() = getHexString(Red.toArgb())

  const val blackHex = "#00000000"


  const val id = "id"
  const val taskId = "taskId"
  const val scheduleId = "scheduleId"

  const val tickerInterval = 1000L //millis
  const val progressAutoSaveCheckpoint = 10L //in secs

  const val progressAutoSaveCheckpointTolerance = 1000L // 1 sec
}