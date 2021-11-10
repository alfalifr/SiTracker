package sidev.app.android.sitracker.ui.model

import androidx.compose.ui.graphics.Color
import sidev.app.android.sitracker.core.domain.model.CalendarLegend
//import sidev.app.android.sitracker.core.domain.model.IconPicData


data class CalendarTileUiData(
  val dateText: String,
  val dateTextColor: Color,
  val bgColor: Color,
  val legend: List<CalendarLegend>?
)

/*
data class CalendarLegendUi(
  val text: String,
  val icon: IconPicData?,
)
 */