package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.page.schedule_detail.ScheduleDetailPreferredDayUi
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.Texts


private val stdEmptyPanelHeight = 100.dp

@Composable
fun PreferredDayPanel(
  data: ScheduleDetailPreferredDayUi?,
  emptyPanelHeight: Dp = stdEmptyPanelHeight,
) {
  LargeSurface(
    Modifier
      .fillMaxWidth()
      .sizeIn(minHeight = emptyPanelHeight),
  ) {
    LoadingPlaceholder(
      key = data,
      loadingText = null,
    ) { data ->
      Column(
        verticalArrangement = Arrangement.spacedBy(
          Const.stdSpacerDp),
      ) {
        Text(
          Texts.preferredDays,
          style = MaterialTheme.typography.h6,
          fontWeight = FontWeight.Bold,
        )
        //Spacer(Modifier.height(Const.stdSpacerDp))
        val arrangement = Arrangement.spacedBy(
          Const.stdSpacerDp)
        WrappingRow(
          modifier = Modifier.padding(start = Const.stdSpacerDp),
          verticalArrangement = arrangement,
          horizontalArrangement = arrangement,
        ) {
          for(day in data.preferredDays) {
            DayItem(
              text = day.name,
              isActive = day.isActive,
              color = data.color,
            )
          }
        }
      }
    }
  }
}