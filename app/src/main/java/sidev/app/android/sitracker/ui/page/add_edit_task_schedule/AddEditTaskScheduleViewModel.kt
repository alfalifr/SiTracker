package sidev.app.android.sitracker.ui.page.add_edit_task_schedule

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.core.domain.model.AppIcon
import sidev.app.android.sitracker.core.domain.model.IconPicData
import sidev.app.android.sitracker.core.domain.usecase.*
import sidev.app.android.sitracker.util.*
import java.util.concurrent.TimeUnit

/**
 * This is a shared ViewModel that contains data related to Task snd Schedule.
 * Why shared? To make data management easier.
 */
class AddEditTaskScheduleViewModel(
  private val formValidationUseCase: FormValidationUseCase,
  private val iconUseCase: IconUseCase,
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val dataWriteUseCase: DataWriteUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  companion object {
    const val READ_TASK_PRESET = "read_task_preset"

    const val READ_TASK = "read_task"
    const val SAVE_TASK = "save_task"

    const val READ_SCHEDULE = "read_schedule"
    const val SAVE_SCHEDULE = "save_schedule"
    const val SAVE_PREFERRED_TIMES = "save_preferred_times"
    const val SAVE_PREFERRED_DAYS = "save_preferred_days"
    const val SAVE_ACTIVE_DATES = "save_active_dates"

    const val READ_ICONS = "read_icons"

    const val EXISTING_ICON_ID_MAPPING = "existing_icon_id_mapping"

    const val TASK_INFO_MASK = 1
    const val SCHEDULE_INFO_MASK = TASK_INFO_MASK shl 1
  }

  private val scope: AppCoroutineScope
    get() = (coroutineScope ?: viewModelScope).toAppScope()
  private val jobManager = CoroutineJobManager()


  private val _isEdit = MutableSharedFlow<Boolean>()
  val isEdit: Flow<Boolean>
    get() = _isEdit

  private val _pagesMask = MutableStateFlow(0)
  val pagesMask: StateFlow<Int>
    get() = _pagesMask

  init {
    scope.launch {
      pagesMask.collect {
        println("AddEditTaskScheduleViewModel pagesMask = $it")
      }
    }
  }

  fun setPagesMask(mask: Int) {
    _pagesMask.value = mask
  }

/*
=================================
Task Page Section - Edit or add data related
=================================
 */
  val allAvailableIcons: Flow<List<AppIcon>> = flow {
    emit(iconUseCase.getAllAvailableIcons())
  }
  val selectedIcon = MutableStateFlow<AppIcon?>(null)
  private val _selectedIcon = selectedIcon.filterNotNull()
  val selectedIconValid: Flow<Boolean> = combine(allAvailableIcons, selectedIcon) {
      all, selected ->
    selected != null && selected in all
  }

  val taskColor = MutableStateFlow<Color?>(null)
  private val taskColorStr: Flow<String?> = taskColor.map { color ->
    color?.colorInt?.let {
      getHexString(it)
    }
  }
  private val _taskColorStr = taskColorStr.filterNotNull()
  val taskColorValid: Flow<Boolean> = taskColorStr.map {
    it != null
  }

  val taskIconData: Flow<IconPicData?> = combine(selectedIcon, taskColorStr) {
      icon, color ->
    if(icon == null || color == null) null
    else {
      IconPicData(
        resId = icon.resId,
        color = color,
        desc = null,
      )
    }
  }

  val taskName = MutableStateFlow<String?>(null)
  private val _taskName = taskName.filterNotNull()
  val taskNameValid: Flow<Boolean> = taskName.map {
    formValidationUseCase.validateStrLen(it, min = Formats.nonBlankStrMinLen)
  }

  val defaultPriority = MutableStateFlow<String?>(null)
  val defaultPriorityValid: Flow<Boolean> = defaultPriority.map {
    formValidationUseCase.validateInt(it)
  }
  private val _defaultPriority: Flow<Int> = combine(defaultPriority, defaultPriorityValid) {
      priority, isValid ->
    if(isValid) priority!!.toInt()
    else null
  }.filterNotNull()

  val description = MutableStateFlow<String?>(null)
  private val _description = description.filterNotNull()
  val descriptionValid: Flow<Boolean> = description.map {
    true // cuz this field is optional.
  }

  val newTask: Flow<Task> = combine(
    _selectedIcon, _taskColorStr,
    _taskName, _defaultPriority, _description
  ) { selectedIcon, taskColorStr, taskName, defaultPriority, description ->
    Task(
      name = taskName,
      priority = defaultPriority,
      desc = description,
      iconId = selectedIcon.id,
      color = taskColorStr,
    )
  }
  private val _newTaskSaveResult = MutableSharedFlow<Boolean>()
  val newTaskSaveResult: Flow<Boolean>
    get() = _newTaskSaveResult

/*
=================================
Task Page Section - Read data related
=================================
 */

  private val _taskIdRaw = MutableSharedFlow<Int?>()
  val taskIdValid: Flow<Boolean> = _taskIdRaw.map {
    it?.compareTo(0) == 1
  }
  val taskId: Flow<Int> = combine(
    taskIdValid.filter { it },
    _taskIdRaw.filterNotNull(),
  ) { _, taskId ->
    taskId
  }

  /*
  val taskId: Flow<Int>
    get() = _taskId
   */

  private val _existingTask = MutableSharedFlow<Task?>()
  val existingTask: Flow<Task?>
    get() = _existingTask

  /*
  val existingTask: Flow<Task?> = taskId.flatMapLatest {
    if(it != null) queryUseCase.queryTask(it)
    else flow {
      emit(null)
    }
  }
   */
/*
  /**
   * This is just a flow with operation that doesn't produce result.
   * The important thing is the operation logic that happens inside it.
   * Why not just collect it? Because if we define this way (as Flow),
   * the operation inside it is executed lazily.
   */
  private val readExistingTaskOp: Flow<Unit> = flow {
    existingTask.collect {
      taskName.value = it?.name
      taskColor.value = it?.let { Color(it.color) }
      defaultPriority.value = it?.priority?.toString()
      description.value = it?.desc
    }
  }
 */

  private suspend fun mapExistingTaskToFields() {
    existingTask.collect {
      taskName.value = it?.name
      taskColor.value = it?.let { Color(it.color) }
      defaultPriority.value = it?.priority?.toString()
      description.value = it?.desc
    }
  }

  private suspend fun mapExistingTaskIconToField() {
    val existingIconId = combine(existingTask, allAvailableIcons) { task, allIcons ->
      allIcons.find {
        it.id == task?.iconId
      }
    }
    existingIconId.collect {
      selectedIcon.value = it
    }
  }


  fun saveNewTask() {
    jobManager.launch(scope, SAVE_TASK) {
      newTask.collect { task ->
        dataWriteUseCase.saveTask(task).collect {
          launch {
            _taskIdRaw.emit(it)
          }
          launch {
            _newTaskSaveResult.emit(it > 0)
          }
        }
      }
    }
  }

  private fun CoroutineScope.onlyReadExistingTask(taskId: Int) {
    launch {
      _taskIdRaw.emit(taskId)
    }
    launch {
      queryUseCase.queryTask(taskId).collect {
        _existingTask.emit(it)
      }
    }
  }
  fun readExistingTask(taskId: Int) {
    jobManager.launch(scope, READ_TASK) {
      onlyReadExistingTask(taskId)
      launch {
        mapExistingTaskToFields()
      }
      launch {
        mapExistingTaskIconToField()
      }
    }
  }

  fun randomTaskColor(forceReload: Boolean = false) {
    if(taskColor.value == null || forceReload) {
      fun randomValue(): Int = (0..255).random()
      taskColor.value = Color(
        red = randomValue(),
        green = randomValue(),
        blue = randomValue(),
      )
    }
  }

/*
  init {
    scope.apply {
      jobManager.apply {
        launch(READ_TASK) {
          existingTask.collect {
            taskName.value = it?.name
            taskColor.value = it?.let { Color(it.color) }
            defaultPriority.value = it?.priority?.toString()
            description.value = it?.desc
          }
        }
        launch(EXISTING_ICON_ID_MAPPING) {
          val existingIconId = combine(existingTask, allAvailableIcons) { task, allIcons ->
            allIcons.find {
              it.id == task?.iconId
            }
          }
          existingIconId.collect {
            selectedIcon.value = it
          }
        }
      }
    }
  }
 */


/*
=================================
Schedule Page Section - Edit or add data related
=================================
 */
  private val _scheduleIdRaw = MutableSharedFlow<Int?>()
  val scheduleIdValid: Flow<Boolean> = _scheduleIdRaw.map {
    it?.compareTo(0) == 1
  }
  val scheduleId: Flow<Int> = combine(
    scheduleIdValid.filter { it },
    _scheduleIdRaw.filterNotNull(),
  ) { _, scheduleId ->
    scheduleId
  }

  val scheduleLabel = MutableStateFlow<String?>(null)
  private val _scheduleLabel= scheduleLabel.filterNotNull()
  val scheduleLabelValid: Flow<Boolean> = scheduleLabel.map {
    formValidationUseCase.validateStrLen(it, min = Formats.nonBlankStrMinLen)
  }

  private val allProgressTypes: Flow<List<ProgressType>> = queryUseCase.queryAllProgressTypes()

  val selectedProgressType = MutableStateFlow<ProgressType?>(null)
  private val _selectedProgressType = selectedProgressType.filterNotNull()
  val selectedProgressTypeValid: Flow<Boolean> = combine(allProgressTypes, selectedProgressType) {
      all, selected ->
    selected != null && selected in all
  }

  val durationSec = MutableStateFlow<String?>(null)
  val durationMin = MutableStateFlow<String?>(null)
  val durationHour = MutableStateFlow<String?>(null)

  val durationSecValid: Flow<Boolean> = durationSec.map {
    it?.toIntOrNull()?.let {
      it in 0..59
    } == true
  }
  val durationMinValid: Flow<Boolean> = durationMin.map {
    it?.toIntOrNull()?.let {
      it in 0..59
    } == true
  }
  val durationHourValid: Flow<Boolean> = durationHour.map {
    formValidationUseCase.validateInt(it)
  }

  val durationValid: Flow<Boolean> = combine(
    durationSecValid, durationMinValid, durationHourValid,
  ) { allIsValid ->
    allIsValid.all { it }
  }

  private val durationStr: Flow<String?> = combine(durationValid, durationSec, durationMin, durationHour) {
      isValid, sec, min, hour ->
    if(!isValid) null
    else Texts.run {
      "${lenSpecifiedNumStr(hour!!.toInt(), 2)}:" +
      "${lenSpecifiedNumStr(min!!.toInt(), 2)}:" +
      lenSpecifiedNumStr(sec!!.toInt(), 2)
    }
  }
  private val durationMillis: Flow<Long> = combine(durationValid, durationSec, durationMin, durationHour) {
      isValid, sec, min, hour ->
    if(!isValid) null
    else Texts.run {
      TimeUnit.SECONDS.toMillis(sec!!.toLong()) +
      TimeUnit.MINUTES.toMillis(min!!.toLong()) +
      TimeUnit.HOURS.toMillis(hour!!.toLong())
    }
  }.filterNotNull()


  private val allIntervalTypes: Flow<List<IntervalType>> = queryUseCase.queryAllIntervalTypes()

  val selectedIntervalType = MutableStateFlow<IntervalType?>(null)
  private val _selectedIntervalType = selectedIntervalType.filterNotNull()
  val selectedIntervalTypeValid: Flow<Boolean> = combine(allIntervalTypes, selectedIntervalType) {
      all, selected ->
    selected != null && selected in all
  }

  //TODO: Make class that handles validation of multiple input, like multiple preferred time.
  interface RangeDataFlow {
    val scheduleId: Flow<Int?>
    val start: MutableStateFlow<String?>
    val end: MutableStateFlow<String?>
    //private val isEndNull = MutableSharedFlow<Boolean>()

    val isStartValid: Flow<Boolean>
    val isEndValid: Flow<Boolean>
  }

  private val _preferredTimesInput = MutableSharedFlow<List<TimeRangeDataFlow>>()
  val preferredTimes: Flow<List<RangeDataFlow>>
    get() = _preferredTimesInput

  private val _activeDatesInput = MutableSharedFlow<List<DateRangeDataFlow>>()
  val activeDates: Flow<List<RangeDataFlow>>
    get() = _activeDatesInput


  //TODO: Optimize preferred day field
  private val _preferredDays = MutableSharedFlow<Map<Int, Boolean>>()

  private val newSchedule: Flow<Schedule> = combine(
    taskId, _scheduleLabel, _selectedProgressType,
    _selectedIntervalType, durationMillis,
  ) { taskId, label, progressType, intervalType, duration ->
    Schedule(
      taskId = taskId,
      label = label,
      progressTypeId = progressType.id,
      intervalId = intervalType.id,
      totalProgress = duration,
    )
  }
  private val _newScheduleSaveResult = MutableSharedFlow<Boolean>()


  private val newPreferredTimes: Flow<List<PreferredTime>> = combine(
    scheduleId, _preferredTimesInput,
  ) { scheduleId, preferredTimes ->

    val preferredTimeFlows = preferredTimes.map {
      it.preferredTime
    }

    combine(preferredTimeFlows) {
      it.asList()
    }
  }.flattenConcat()
  private val _newPreferredTimesSaveResult = MutableSharedFlow<Boolean>()

  private val newActiveDates: Flow<List<ActiveDate>> = combine(
    scheduleId, _activeDatesInput,
  ) { scheduleId, activeDates ->

    val activeDateFlows = activeDates.map {
      it.activeDate
    }

    combine(activeDateFlows) {
      it.asList()
    }
  }.flattenConcat()
  private val _newActiveDatesSaveResult = MutableSharedFlow<Boolean>()

  private val newPreferredDays: Flow<List<PreferredDay>> = combine(
    scheduleId, _preferredDays,
  ) { scheduleId, preferredDays ->
    preferredDays.asSequence()
      .filter { it.value }
      .map {
        PreferredDay(
          scheduleId = scheduleId,
          dayInWeek = it.key,
        )
      }.toList()
  }
  private val _newPreferredDaysResult = MutableSharedFlow<Boolean>()

  val newOverallScheduleSaveResult: Flow<Boolean> = combine(
    _newScheduleSaveResult, _newPreferredTimesSaveResult,
    _newPreferredDaysResult, _newActiveDatesSaveResult,
  ) { results ->
    results.all { it }
  }

/*
=================================
Schedule Page Section - Read data related
=================================
 */

  private val _existingSchedule = MutableSharedFlow<Schedule?>()
  val existingSchedule: Flow<Schedule?>
    get() = _existingSchedule

  private val _existingPreferredTimes = MutableSharedFlow<List<PreferredTime>?>()
  val existingPreferredTimes: Flow<List<PreferredTime>?>
    get() = _existingPreferredTimes

  private val _existingPreferredDays = MutableSharedFlow<List<PreferredDay>?>()
  val existingPreferredDays: Flow<List<PreferredDay>?>
    get() = _existingPreferredDays

  private val _existingActiveDates = MutableSharedFlow<List<ActiveDate>?>()
  val existingActiveDates: Flow<List<ActiveDate>?>
    get() = _existingActiveDates


  fun saveScheduleRelatedData() {
    jobManager.launch(scope, SAVE_SCHEDULE) {
      launch {
        saveOnlyNewSchedule()
      }
      launch {
        saveNewPreferredTimes()
      }
      launch {
        saveNewPreferredDays()
      }
      launch {
        saveNewActiveDates()
      }
    }
  }

  private suspend fun saveOnlyNewSchedule() {
    newSchedule.collect { schedule ->
      dataWriteUseCase.saveSchedule(schedule).collect {
        _scheduleIdRaw.emit(it)
        _newScheduleSaveResult.emit(it > 0)
      }
    }
  }
  private suspend fun saveNewPreferredTimes() {
    newPreferredTimes.collect { preferredTimes ->
      dataWriteUseCase.savePreferredTimes(preferredTimes).collect { rowIds ->
        _newPreferredTimesSaveResult.emit(rowIds.all { it > 0 })
      }
    }
  }
  private suspend fun saveNewPreferredDays() {
    newPreferredDays.collect { preferredDays ->
      dataWriteUseCase.savePreferredDays(preferredDays).collect { rowIds ->
        _newPreferredDaysResult.emit(rowIds.all { it > 0 })
      }
    }
  }
  private suspend fun saveNewActiveDates() {
    newActiveDates.collect { activeDates ->
      dataWriteUseCase.saveActiveDates(activeDates).collect { rowIds ->
        _newActiveDatesSaveResult.emit(rowIds.all { it > 0 })
      }
    }
  }


  private fun mapExistingScheduleDurationToField(duration: Long?) {
    if(duration == null) {
      durationHour.value = null
      durationMin.value = null
      durationSec.value = null
    } else {
      val units = breakTimeMillisToClockComponent(duration)

      Texts.apply {
        durationHour.value = lenSpecifiedNumStr(units[0], 2)
        durationMin.value = lenSpecifiedNumStr(units[1], 2)
        durationSec.value = lenSpecifiedNumStr(units[2], 2)
      }
    }
  }
  private fun CoroutineScope.mapTaskToScheduleFields(schedule: Schedule) {
    onlyReadExistingTask(schedule.taskId)
    launch {
      _existingTask.collect {
        taskName.value = it?.name
      }
    }
  }
  private fun CoroutineScope.mapExistingScheduleToFields() {
    launch {
      existingSchedule.collect {
        //_taskIdRaw.emit(it?.taskId) //TODO: make func that returns only Task
        scheduleLabel.value = it?.label
        mapExistingScheduleDurationToField(it?.totalProgress)
        if(it != null) {
          mapTaskToScheduleFields(it)
        } else {
          taskName.value = null
        }
      }
    }
    launch {
      combine(existingSchedule, allProgressTypes) { existingSchedule, progressTypes ->
        selectedProgressType.value = existingSchedule?.let { schedule ->
          progressTypes.find {
            it.id == schedule.progressTypeId
          }
        }
      }
    }
    launch {
      combine(existingSchedule, allIntervalTypes) { existingSchedule, intervalType ->
        selectedIntervalType.value = existingSchedule?.let { schedule ->
          intervalType.find {
            it.id == schedule.intervalId
          }
        }
      }
    }
  }
  private suspend fun mapPreferredTimesToFields() {
    val prefTimeMapperFlow = _existingPreferredTimes.map { preferredTimes ->
      preferredTimes?.map { prefTime ->
        TimeRangeDataFlow(formValidationUseCase).apply {
          this.scheduleId.value = prefTime.scheduleId
          isEndNull.emit(prefTime.endTime == null)
          start.value = Texts.formatTimeToClock(prefTime.startTime, false) //clockLongsToString(startNums)
          end.value = prefTime.endTime?.let {
            Texts.formatTimeToClock(it, false)
          }
        }
      } ?: emptyList()
    }
    prefTimeMapperFlow.collect {
      _preferredTimesInput.emit(it)
    }
  }
  private suspend fun mapPreferredDaysToFields() {
    val prefDayMapperFlow = _existingPreferredDays.map {
      it?.let { preferredDays ->
        val map = mutableMapOf<Int, Boolean>()
        preferredDays.forEach {
          if(map[it.dayInWeek] != true) {
            map[it.dayInWeek] = true
          }
        }
        map
      } ?: emptyMap()
    }
    prefDayMapperFlow.collect {
      _preferredDays.emit(it)
    }
  }
  private suspend fun mapActiveDatesToFields() {
    val activeDatesMapperFlow = _existingActiveDates.map { activeDates ->
      activeDates?.map { activeDate ->
        DateRangeDataFlow().apply {
          this.scheduleId.value = activeDate.scheduleId
          isEndNull.emit(activeDate.endDate == null)
          start.value = Texts.formatTimeToDate(activeDate.startDate)
          end.value = activeDate.endDate?.let {
            Texts.formatTimeToDate(it)
          }
        }
      } ?: emptyList()
    }
    activeDatesMapperFlow.collect {
      _activeDatesInput.emit(it)
    }
  }

  fun readExistingSchedule(scheduleId: Int) {
    jobManager.launch(scope, READ_SCHEDULE) {
      launch {
        mapExistingScheduleToFields()
      }
      launch {
        mapPreferredTimesToFields()
      }
      launch {
        mapPreferredDaysToFields()
      }
      launch {
        mapActiveDatesToFields()
      }
      launch {
        _scheduleIdRaw.emit(scheduleId)
      }
      launch {
        queryUseCase.queryScheduleDetail(scheduleId).collect {
          queryJointUseCase.getScheduleJoint(it).firstOrNull().also { scheduleJoint ->
            _existingSchedule.emit(scheduleJoint?.schedule)
            _existingPreferredTimes.emit(scheduleJoint?.preferredTimes)
            _existingPreferredDays.emit(scheduleJoint?.preferredDays)
            _existingActiveDates.emit(scheduleJoint?.activeDates)
          }
        }
      }
    }
  }


  /**
   * This method will be called when this ViewModel is no longer used and will be destroyed.
   *
   *
   * It is useful when ViewModel observes some data and you need to clear this subscription to
   * prevent a leak of this ViewModel.
   */
  override fun onCleared() {
    super.onCleared()
    jobManager.cancelAllJobs()
  }
}



private abstract class AbstractRangeDataFlow: AddEditTaskScheduleViewModel.RangeDataFlow {
  final override val scheduleId: MutableStateFlow<Int?> = MutableStateFlow(null)
  final override val start: MutableStateFlow<String?> = MutableStateFlow(null)
  final override val end: MutableStateFlow<String?> = MutableStateFlow(null)

  val isEndNull = MutableSharedFlow<Boolean>()

  /*
  override val isStartValid: Flow<Boolean> = start.map {
    formValidationUseCase.validateClockFormat(it)
  }
   */
  val startMillis: Flow<Long> by lazy {
    combine(start, isStartValid) {
        start, isValid ->
      if(!isValid) null
      else {
        //convertClockStringToMillis(start!!)
        convertInputToMillis(start!!)
      }
    }.filterNotNull()
  }

  abstract val isEndFormatValid: Flow<Boolean>
  val endMillis: Flow<Long?> by lazy {
    flow {
      val millisConversion = combine(end, isEndFormatValid) {
          end, isValid ->
        if(!isValid) null
        else {
          //convertClockStringToMillis(end!!)
          convertInputToMillis(end!!)
        }
      }//.filterNotNull()

      val finalFlow = combine(millisConversion, isEndNull) {
          millis, isNull ->
        if(isNull || millis != null) {
          emit(millis)
        }
      }

      finalFlow.collect {
        // just collect so that logic inside transform body is executed.
      }
    }
  }


  final override val isEndValid: Flow<Boolean> by lazy {
    combine(startMillis, endMillis) {
        start, end ->
      when {
        end == null
          || end < start -> false
        else -> true
      }
    }
  }

  protected abstract fun convertInputToMillis(input: String): Long
}

private class TimeRangeDataFlow(
  private val formValidationUseCase: FormValidationUseCase,
): AbstractRangeDataFlow() {
  override val isStartValid: Flow<Boolean> = start.map {
    formValidationUseCase.validateClockFormat(it)
  }
  override val isEndFormatValid: Flow<Boolean> = end.map {
    formValidationUseCase.validateClockFormat(it)
  }

  val preferredTime: Flow<PreferredTime> = combine(
    scheduleId.filterNotNull(),
    startMillis,
    endMillis,
  ) { scheduleId, start, end ->
    PreferredTime(
      scheduleId = scheduleId,
      startTime = start,
      endTime = end,
    )
  }

  override fun convertInputToMillis(input: String): Long =
    convertClockStringToMillis(input)
}

private class DateRangeDataFlow: AbstractRangeDataFlow() {
  override val isStartValid: Flow<Boolean> = start.map {
    it?.let {
      Formats.doesDateStrFormatComply(it)
    } == true
  }
  override val isEndFormatValid: Flow<Boolean> = end.map {
    it?.let {
      Formats.doesDateStrFormatComply(it)
    } == true
  }

  val activeDate: Flow<ActiveDate> = combine(
    scheduleId.filterNotNull(),
    startMillis,
    endMillis,
  ) { scheduleId, start, end ->
    ActiveDate(
      scheduleId = scheduleId,
      startDate = start,
      endDate = end,
    )
  }

  override fun convertInputToMillis(input: String): Long =
    DataMapper.parseDateStr(input).time
}