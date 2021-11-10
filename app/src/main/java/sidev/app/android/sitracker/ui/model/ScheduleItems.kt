package sidev.app.android.sitracker.ui.model

import sidev.app.android.sitracker.core.domain.model.CalendarDate
import sidev.app.android.sitracker.core.domain.model.IconProgressionData
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData
import sidev.app.android.sitracker.ui.component.TaskItem


/**
 * Data model for [TaskItem].
 * [id] is for this component identifier,
 * often is schedule id.
 */
data class ScheduleItemDataUi(
  val id: Int,
  val icon: IconProgressionPicData,
  val title: String,
  val desc: String?,
  val postfixIconData: IconProgressionData?,
  val isPostfixIconDataColorSameAsMainColor: Boolean,
)


data class ScheduleItemGroupUi(
  val schedules: List<ScheduleItemDataUi>,
  val header: String,
)



/**
 * Schedule related data that shown on calendar.
 */
data class ScheduleCalendarPicDataUi(
  val date: CalendarDate,
  val scheduleIcons: List<IconProgressionPicData>,
)

/**
 * Schedule related data that shown on calendar.
 */
data class ScheduleCalendarTextDataUi(
  val date: CalendarDate,
  val scheduleIcons: List<IconProgressionPicData>,
)