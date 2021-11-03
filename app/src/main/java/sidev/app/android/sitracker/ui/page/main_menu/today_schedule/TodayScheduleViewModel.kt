package sidev.app.android.sitracker.ui.page.main_menu.today_schedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.core.domain.usecase.TaskItemScheduleUseCase
import sidev.app.android.sitracker.util.DataMapper.toUiData
import sidev.app.android.sitracker.util.getDateMillis

class TodayScheduleViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val iconUseCase: IconUseCase,
  private val taskItemScheduleUseCase: TaskItemScheduleUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {
  /**
   * Measured as millis from epoch for a particular date.
   * Note that this millis must only represent the start millis
   * of a date. Any hour or smaller time unit info must not
   * be included here.
   */
  val nowDateTime = MutableSharedFlow<Long>()
  private val ensuredNowDateTime = nowDateTime.map {
    getDateMillis(it)
  }

  private val queryResult: Flow<ProgressQueryResult> = ensuredNowDateTime.flatMapLatest {
    queryUseCase.queryTodaySchedule(it)
  }
  private val scheduleJoints: Flow<List<ScheduleJoint>> = queryResult.map {
    queryJointUseCase.getScheduleJoint(it)
  }
  val taskItemSchedules: Flow<List<TaskItemSchedule>> = scheduleJoints.map {
    taskItemScheduleUseCase.getTaskItemSchedules(it)
  }

  val listOrder = MutableSharedFlow<TaskItemScheduleGroupOrder>()

  private val taskItemScheduleGroups: Flow<List<TaskItemScheduleGroup>> = combine(
    listOrder, taskItemSchedules
  ) { order, taskItemSchedules ->
    taskItemScheduleUseCase.orderTaskItemScheduleBy(
      taskItemSchedules = taskItemSchedules,
      order = order,
    )
  }
  val taskItemScheduleGroupsUi = taskItemScheduleGroups.map { groups ->
    groups.map { it.toUiData(iconUseCase = iconUseCase) }
  }
}