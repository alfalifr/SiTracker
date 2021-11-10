package sidev.app.android.sitracker.util

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.core.domain.model.ProgressImportanceFactor
import sidev.app.android.sitracker.ui.theme.Red

object Const {
  const val iconSize = 50
  val iconSizeDp = iconSize.dp

  const val contentPadding = 15
  val contentPaddingDp = contentPadding.dp


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
}