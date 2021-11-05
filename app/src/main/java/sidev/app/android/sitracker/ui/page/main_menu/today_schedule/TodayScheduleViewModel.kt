package sidev.app.android.sitracker.ui.page.main_menu.today_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.core.domain.usecase.TaskItemScheduleUseCase
import sidev.app.android.sitracker.util.DataMapper.toUiData
import sidev.app.android.sitracker.util.getDateMillis
import java.util.*

class TodayScheduleViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val iconUseCase: IconUseCase,
  private val taskItemScheduleUseCase: TaskItemScheduleUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  private var processingJob: Job? = null

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
    println("queryResult now = $it")
    queryUseCase.queryTodaySchedule(it)
  }
  private val scheduleJoints: Flow<List<ScheduleJoint>> = queryResult.map {
    println("scheduleJoints queryResult = $it")
    queryJointUseCase.getScheduleJoint(it)
  }
  val taskItemSchedules: Flow<List<TaskItemSchedule>> = scheduleJoints.map {
    println("taskItemSchedules scheduleJoints = $it")
    taskItemScheduleUseCase.getTaskItemSchedules(it)
  }

  val listOrder = MutableStateFlow(TaskItemScheduleGroupOrder.BY_TIME)

  private val taskItemScheduleGroups: Flow<List<TaskItemScheduleGroup>> = combine(
    listOrder, taskItemSchedules
  ) { order, taskItemSchedules ->
    println("order = $order taskItemSchedules = $taskItemSchedules")
    taskItemScheduleUseCase.orderTaskItemScheduleBy(
      taskItemSchedules = taskItemSchedules,
      order = order,
    )
  }
  val taskItemScheduleGroupsUi = taskItemScheduleGroups.map { groups ->
    groups.map { it.toUiData(iconUseCase = iconUseCase) }
  }

  init {
    viewModelScope.launch {
      launch {
        listOrder.collect {
          println("listOrder flow = $it")
        }
      }
      launch {
        taskItemSchedules.collect {
          println("taskItemSchedules flow = $it")
        }
      }
      launch {
        nowDateTime.collect {
          println("nowDateTime flow = $it")
        }
      }
    }
  }

  fun refreshList() {
    processingJob?.cancel()
    processingJob = (coroutineScope ?: viewModelScope).launch {
      nowDateTime.emit(Date().time)
    }
  }
}