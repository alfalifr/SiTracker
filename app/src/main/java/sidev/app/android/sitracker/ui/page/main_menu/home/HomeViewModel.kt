package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData
import sidev.app.android.sitracker.core.domain.model.ProgressImportanceJoint
import sidev.app.android.sitracker.core.domain.model.ProgressJoint
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.usecase.*
import sidev.app.android.sitracker.util.Texts.formatDurationToShortest
import sidev.app.android.sitracker.util.Texts.formatPriority
import sidev.app.android.sitracker.util.Texts.formatTimeToShortest
import java.util.*

class HomeViewModel(
  /*
  private val activeDateDao: ActiveDateDao,
  private val scheduleProgressDao: ScheduleProgressDao,
  private val preferredTimeDao: PreferredTimeDao,
  private val scheduleDao: ScheduleDao,
  private val taskDao: TaskDao,
   */
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val recommendationUseCase: RecommendationUseCase,
  private val iconUseCase: IconUseCase,
  private val dbEnumUseCase: DbEnumUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  //private val _taskTitle = MutableLiveData<String>()
  //val taskTitle
/*
  var now: Long = 0
    private set
 */

  private var processingJob: Job? = null

  private val _nowFlow = MutableSharedFlow<Long>()
  val nowFlow: Flow<Long>
    get() = _nowFlow

  val activeTaskIndex = MutableStateFlow<Int?>(null)
  private val _validatedActiveTaskIndex: Flow<Int?> by lazy {
    combine(activeTaskIndex, sortedImportances) {
      index, importances ->
      if(index == null || importances.isEmpty()) {
        return@combine null
      }
      if(index !in importances.indices) {
        throw IllegalArgumentException(
          "`activeTaskIndex` ($index) can't have value " +
            "outside `sortedImportances` indices (${importances.indices})."
        )
      }
      index
    }
  }


  private val progressQuery: Flow<ProgressQueryResult> = _nowFlow.flatMapLatest {
    queryUseCase.queryRecommendations(it)
  }
  private val rawProgressJoints: Flow<List<ProgressJoint>> = progressQuery.map {
    queryJointUseCase.getProgressJoint(it)
  }
  val importances: Flow<List<ProgressImportanceJoint>> = rawProgressJoints.map {
    recommendationUseCase.getProgressImportance(it)
  }
  val sortedImportances: Flow<List<ProgressImportanceJoint>> = combine(importances, nowFlow) {
    importances, now ->
    recommendationUseCase.getRecommendedList(importances, now = now)
  }


//  private val _recommendedTasks = MutableStateFlow<List<Task>?>(null)

  val iconResIdData: Flow<List<IconProgressionPicData>> = sortedImportances.map { importances ->
    importances.map {
      iconUseCase.getIconProgressionData(it.joint)
    }
  }

  val activeTaskTitle: Flow<String?> = combine(_validatedActiveTaskIndex, sortedImportances) {
    index, importances ->
    if(index == null) return@combine null
    importances[index].joint.task.name
  }

  val activeLowerDetailData: Flow<HomeLowerDetailData?> =
    combine(_validatedActiveTaskIndex, sortedImportances) {
        index, importances ->
      if(index == null) return@combine null

      val importance = importances[index]

      val schedule = importance.joint.schedule

      val startTime = importance.joint.preferredTimes
        .find { it.scheduleId == schedule.id }
        ?.startTime

      HomeLowerDetailData(
        duration = dbEnumUseCase.formatProgress(schedule),
        startTime = startTime?.let { formatTimeToShortest(it) },
        priority = formatPriority(importance.joint.task.priority),
      )
    }

/*
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
 */


/*
    Transformations.map(_validatedActiveTaskIndex) {
    _recommendedTasks.value!![it].run {
      HomeLowerDetailData(
        priority = priority,
      )
    }
  }
 */

  /*
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
   */

  fun getActiveSchedules() {
    processingJob?.cancel()
    activeTaskIndex.value = null
    processingJob = (coroutineScope ?: viewModelScope).launch {
      _nowFlow.emit(Date().time)
    }
  }
}