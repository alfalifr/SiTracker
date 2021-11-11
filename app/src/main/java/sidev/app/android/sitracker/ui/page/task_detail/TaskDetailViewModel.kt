@file:OptIn(ExperimentalCoroutinesApi::class)
package sidev.app.android.sitracker.ui.page.task_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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


class TaskDetailViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val iconUseCase: IconUseCase,
  private val calendarUseCase: CalendarUseCase,
  private val calendarUiUseCase: CalendarUiUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  private var job: Job? = null


  val taskId = MutableSharedFlow<Int>()
  private val _taskId = taskId.distinctUntilChanged()
  val isDarkTheme = MutableSharedFlow<Boolean>()
  //TODO: Make method that can change this value with monthly interval
  val timestamp = MutableSharedFlow<Long>()
  private val onlyDateTimestamp = timestamp.map {
    getDateMillis(it)
  }.distinctUntilChanged()

  private val queryResult: Flow<ProgressQueryResult> = _taskId.flatMapLatest {
    queryUseCase.queryTaskDetail(it)
  }
  private val queryJoint: Flow<TaskJoint> = queryResult.map {
    queryJointUseCase.getTaskJoint(it).first()
  }

  val taskPanelData: Flow<TaskItemDataUi> = queryJoint.map {
    with(it.task) {
      TaskItemDataUi(
        taskId = id,
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

  fun loadData(
    taskId: Int,
    isDark: Boolean,
  ) {
    val taskIdFlow = this.taskId
    job?.cancel()
    job = (coroutineScope ?: viewModelScope).launch {
      taskIdFlow.emit(taskId)
      isDarkTheme.emit(isDark)
      timestamp.emit(System.currentTimeMillis())
    }
  }
}