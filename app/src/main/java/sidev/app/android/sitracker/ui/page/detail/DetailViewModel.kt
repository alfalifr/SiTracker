@file:OptIn(ExperimentalCoroutinesApi::class)
package sidev.app.android.sitracker.ui.page.detail

import androidx.compose.ui.text.toLowerCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.domain.model.CalendarRange
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.TaskJoint
import sidev.app.android.sitracker.core.domain.usecase.CalendarUseCase
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.ui.model.CalendarTileUiData
import sidev.app.android.sitracker.ui.model.TaskItemDataUi
import sidev.app.android.sitracker.ui.usecase.CalendarUiUseCase
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.getDateMillis
import java.util.*

class DetailViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val iconUseCase: IconUseCase,
  private val calendarUseCase: CalendarUseCase,
  private val calendarUiUseCase: CalendarUiUseCase,
) {
  val taskId = MutableSharedFlow<Int>()
  val isDarkTheme = MutableSharedFlow<Boolean>()
  val timestamp = MutableSharedFlow<Long>()
  private val onlyDateTimestamp = timestamp.map {
    getDateMillis(it)
  }.distinctUntilChanged()

  private val queryResult: Flow<ProgressQueryResult> = taskId.flatMapLatest {
    queryUseCase.queryTaskDetail(it)
  }
  private val queryJoint: Flow<TaskJoint> = queryResult.map {
    queryJointUseCase.getTaskJoint(it).first()
  }

  val taskPanelData: Flow<TaskItemDataUi> = queryJoint.map {
    with(it.task) {
      TaskItemDataUi(
        icon = IconProgressionPicData(
          resId = iconUseCase.getResId(iconId),
          color = color,
          progressFraction = null,
        ),
        name = name,
        desc = desc,
        priorityText = Texts.formatPriority(priority),
      )
    }
  }

  val scheduleItemTextList: Flow<List<String>> = queryJoint.map { taskJoint ->
    taskJoint.scheduleJoints
      .sortedByDescending { it.activeDates.maxOf { it.startDate } }
      .map { it.schedule.label }
  }
  val scheduleItemListHeader: Flow<String> = queryJoint.map {
    if(it.scheduleJoints.isEmpty()) {
      Texts.noSchedule
    } else {
      "${it.scheduleJoints.size} ${Texts.schedule.lowercase(
        Locale.getDefault()
      )}"
      ""
    }
  }

  val preferredDays: Flow<Set<Int>> = queryJoint.map { taskJoint ->
    val set = mutableSetOf<Int>()
    taskJoint.scheduleJoints.forEach { scheduleJoint ->
      scheduleJoint.preferredDays.mapTo(set) {
        it.dayInWeek
      }
    }
    set
  }


  private val calendarData: Flow<CalendarRange> = combine(
    onlyDateTimestamp,
    queryJoint,
  ) { timestamp, taskJoint ->
    calendarUseCase.getMonthCalendarRange(
      taskJoint = taskJoint,
      dateTime = timestamp,
    )
  }
  val calendarUiData: Flow<List<CalendarTileUiData>> = combine(
    calendarData,
    isDarkTheme,
  ) { calendarRange, isDark ->
    calendarUiUseCase.getCalendarTileData(
      calendarRange = calendarRange,
      isDark = isDark,
    )
  }
}