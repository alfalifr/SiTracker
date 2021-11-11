package sidev.app.android.sitracker.ui.page.schedule_detail


data class ScheduleDetailHeaderUiData(
  val totalProgress: String,
  val interval: String,
  val activeDates: List<Pair<String, String?>>,
)

data class ScheduleDetailPreferredTimeUi(
  val preferredTimes: List<Pair<String, String?>>,
)

data class ScheduleDetailPreferredDayUi(
  val preferredDays: List<Int>,
)