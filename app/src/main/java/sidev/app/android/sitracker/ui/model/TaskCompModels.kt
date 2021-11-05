package sidev.app.android.sitracker.ui.model

import sidev.app.android.sitracker.core.domain.model.IconProgressionData
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData
import sidev.app.android.sitracker.ui.component.TaskItem


/**
 * Data model for [TaskItem].
 * [id] is for this component identifier,
 * often is schedule id.
 */
data class TaskCompData(
  val id: Int,
  val icon: IconProgressionPicData,
  val title: String,
  val desc: String?,
  val postfixIconData: IconProgressionData?,
  val isPostfixIconDataColorSameAsMainColor: Boolean,
)


data class TaskItemScheduleGroupUi(
  val schedules: List<TaskCompData>,
  val header: String,
)