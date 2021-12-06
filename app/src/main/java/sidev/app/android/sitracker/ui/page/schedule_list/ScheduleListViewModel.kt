@file:OptIn(
  ExperimentalCoroutinesApi::class,
)
package sidev.app.android.sitracker.ui.page.schedule_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.util.Texts

class ScheduleListViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  private val scope: CoroutineScope
    get() = coroutineScope ?: viewModelScope

  private var job: Job? = null

  private val taskId = MutableSharedFlow<Int>()
  private val filteredTaskId = taskId.distinctUntilChanged()

  private val queryResult: Flow<ProgressQueryResult> = filteredTaskId.flatMapLatest {
    queryUseCase.queryTaskScheduleList(it)
  }
  private val scheduleJoints: Flow<List<ScheduleJoint>> = queryResult.map {
    queryJointUseCase.getScheduleJoint(it)
  }

  /**
   * Why this property? Because if the task
   */
  val header: Flow<String> = queryResult.map {
    "${it.tasks.first().name} (${it.schedules.size})"
  }
  val scheduleList: Flow<List<TaskScheduleListItem>> = scheduleJoints.map { joints ->
    joints.map {
      TaskScheduleListItem(
        scheduleId = it.schedule.id,
        name = it.schedule.label,
        preferredTime = it.preferredTimes.joinToString {
          Texts.formatTimeToClock(it.startTime, withSecond = false)
        },
        preferredDay = it.preferredDays.joinToString {
          Texts.getShortDayName(it.dayInWeek)
        },
      )
    }
  }

  fun loadData(taskId: Int) {
    job?.cancel()
    job = scope.launch {
      this@ScheduleListViewModel.taskId.emit(taskId)
    }
  }
}