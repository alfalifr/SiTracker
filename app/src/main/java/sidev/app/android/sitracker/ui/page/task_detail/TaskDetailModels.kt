package sidev.app.android.sitracker.ui.page.task_detail


data class TaskSchedulePanelData(
  val header: String,
  val items: List<String>,
  /**
   * The string of "see x others...".
   * Null if the size of [items] nor more than 3.
   */
  val seeOtherText: String?,
)