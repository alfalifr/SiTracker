package sidev.app.android.sitracker.ui.page.schedule_list


data class TaskScheduleListItem(
  val scheduleId: Int,
  val name: String,
  val preferredTime: String,
  val preferredDay: String,
)