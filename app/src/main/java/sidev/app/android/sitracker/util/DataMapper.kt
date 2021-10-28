package sidev.app.android.sitracker.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import sidev.app.android.sitracker.core.domain.model.IconProgressionData
import sidev.app.android.sitracker.ui.model.IconProgressionUiData

object DataMapper {
  @Composable
  fun IconProgressionData.toUiData(): IconProgressionUiData = IconProgressionUiData(
    image = painterResource(id = resId),
    color = Color(color),
    progress = progressFraction,
  )

  @Composable
  fun List<IconProgressionData>.toUiData(): List<IconProgressionUiData> = map {
    it.toUiData()
  }
}