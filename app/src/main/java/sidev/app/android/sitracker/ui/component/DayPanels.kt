package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import sidev.app.android.sitracker.ui.theme.OppositeBrightnessColor
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.util.Const

@Composable
fun DayItem(
  text: String,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
) {
  SmallSurface(
    modifier = modifier.let {
      if(!isActive) { it }
      else {
        it.border(
          width = Const.stdBorderWidthDp,
          color = OppositeDark,
          shape = MaterialTheme.shapes.small,
        )
      }
    }
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.body2,
      color = if(isActive) OppositeBrightnessColor(
        MaterialTheme.colors.primary
      ) else Color.Unspecified,
    )
  }
}