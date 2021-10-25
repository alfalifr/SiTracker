package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.util.formatTimeToShortest
import java.util.*

class HomeViewModel(
  private val activeDateDao: ActiveDateDao,
  private val scheduleProgressDao: ScheduleProgressDao,
  private val preferredTimeDao: PreferredTimeDao,
  private val scheduleDao: ScheduleDao,
  private val taskDao: TaskDao,
): ViewModel() {
  //private val _taskTitle = MutableLiveData<String>()
  //val taskTitle

  var now: Long = 0
    private set

  private val _recommendedTasks = MutableStateFlow<List<Task>?>(null)

  val activeTaskIndex = MutableStateFlow(-1)
  private val _validatedActiveTaskIndex = activeTaskIndex.onEach {
    ensureStateValid()
  }

  val activeTaskTitle: Flow<String> = _validatedActiveTaskIndex.map {
    _recommendedTasks.value?.get(it)?.name ?: "<null>"
  }

  private val _activeDates = MutableSharedFlow<List<ActiveDate>>()
  private var rawActiveDateQueryJob: Job? = null
  private var rawActiveDates: Flow<List<ActiveDate>>? = null
    set(v) {
      if(field != null) {
        rawActiveDateQueryJob?.cancel()
        rawActiveDateQueryJob = null
      }
      field = v
      if(v != null) {
        rawActiveDateQueryJob = viewModelScope.launch {
          v.collect {
            _activeDates.emit(it)
          }
        }
      }
    }



  val schedules: Flow<List<Schedule>> = _activeDates.flatMapLatest {
    val ids = it.map { it.scheduleId }.toSet()
    scheduleDao.getByIds(ids)
  }

  val preferredTimes: Flow<List<PreferredTime>> = schedules.flatMapLatest {
    val ids = it.map { it.id }.toSet()
    preferredTimeDao.getTimeByScheduleIds(ids)
  }
  /*
  LiveDataTransform.rawSwitchMap(_activeDates) {
    if(it != null) {
      val ids = it.map { it.scheduleId }.toSet()
      scheduleDao.getByIds(ids)
    } else null
  }.apply {
    combine()
  }
   */

  val scheduleProgresses: Flow<List<ScheduleProgress>> = schedules.flatMapLatest {
    val scheduleIds = it.map { it.id }.toSet()
    scheduleProgressDao.getActiveProgressListByScheduleIds(now, scheduleIds)
  }

  val tasks: Flow<List<Task>> = schedules.flatMapLatest {
    val taskIds = it.map { it.taskId }.toSet()
    taskDao.getByIds(taskIds)
  }


  val activeLowerDetailData: Flow<HomeLowerDetailData> =
    combine(activeTaskIndex, scheduleProgresses, preferredTimes, schedules, tasks,) {
        pageIndex, progresses, preferredTimes, schedules, tasks ->
      val schedule = schedules[pageIndex]

      val startTime = preferredTimes.find { it.scheduleId == schedule.id }
        ?.startTime

      HomeLowerDetailData(
        duration = schedule.totalProgress,
        startTime = startTime?.let { formatTimeToShortest(it) },
        priority = tasks[pageIndex].priority,
      )
    }


/*
    Transformations.map(_validatedActiveTaskIndex) {
    _recommendedTasks.value!![it].run {
      HomeLowerDetailData(
        priority = priority,
      )
    }
  }
 */

  private fun ensureStateValid() {
    assert(_recommendedTasks.value != null) {
      "`_recommendedTasks.value` == null"
    }
    assert(activeTaskIndex.value.let {
      it != null || it in _recommendedTasks.value!!.indices
    }) {
      """`activeTaskIndex.value` == null or `activeTaskIndex.value` outside `_recommendedTasks` indices
        |current `activeTaskIndex.value` = '${activeTaskIndex.value}'
      """.trimMargin()
    }
  }

  fun getActiveSchedules() {
    val flow = flowOf(1)
    val flow2 = MutableStateFlow(1)
    val flow3: SharedFlow<Int> = MutableSharedFlow<Int>()

    flow2.value = 3
    //flow2.add
    now = Date().time
    rawActiveDates = activeDateDao.getActiveDateByTime(now)
  }
}