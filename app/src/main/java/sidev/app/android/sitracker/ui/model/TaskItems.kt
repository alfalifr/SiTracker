package sidev.app.android.sitracker.ui.model

import sidev.app.android.sitracker.core.domain.model.CalendarDate
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData

data class TaskItemDataUi(
  val taskId: Int,
  val icon: IconProgressionPicData,
  val name: String,
  val desc: String,
  val priorityText: String,
)