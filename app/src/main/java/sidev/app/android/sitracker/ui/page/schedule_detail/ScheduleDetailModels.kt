package sidev.app.android.sitracker.ui.page.schedule_detail

import androidx.compose.ui.graphics.Color


data class ScheduleDetailHeaderUiData(
  val totalProgress: String,
  val interval: String,
  val activeDates: List<Pair<String, String?>>,
)

data class ScheduleDetailPreferredTimeUi(
  val preferredTimes: List<Pair<String, String?>>,
)

data class ScheduleDetailPreferredDayUi(
  val preferredDays: List<ScheduleDetailPreferredDayItemUi>,
  val color: Color,
)

data class ScheduleDetailPreferredDayItemUi(
  val dayNum: Int,
  val name: String,
  val isActive: Boolean,
)