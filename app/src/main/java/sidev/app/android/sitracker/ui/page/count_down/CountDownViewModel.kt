@file:OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
)
package sidev.app.android.sitracker.ui.page.count_down

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.combine
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.domain.model.CountDownProgressJoint
import sidev.app.android.sitracker.core.domain.model.IconPicData
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.core.domain.usecase.ScheduleProgressUseCase
import sidev.app.android.sitracker.util.*


class CountDownViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val iconUseCase: IconUseCase,
  private val scheduleProgressUseCase: ScheduleProgressUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  private var job: Job? = null
  val scope: CoroutineScope
    get() = coroutineScope ?: viewModelScope

  /**
   * Flag whether all data in this class can be reset or not.
   * It is useful when user reload the page because by default
   * `queryResult` doesn't emit data when only [ScheduleProgress.actualProgress] changes.
   */
  private var isDataReset = false

  val nowDateTime = MutableStateFlow<Long?>(null)
  val scheduleId = MutableStateFlow<Int?>(null)
  private val _nowDateTime = nowDateTime.filterNotNull()
  private val _scheduleId = scheduleId.filterNotNull()

  private val queryResult: Flow<ProgressQueryResult> = combine(_nowDateTime, _scheduleId) {
      timestamp, scheduleId ->
    queryUseCase.queryScheduleForCountDown(
      scheduleId = scheduleId,
      timestamp = timestamp,
    )
  }.flattenConcat()
    .distinctUntilChanged { old, new ->
      old.schedules == new.schedules
        && old.tasks == new.tasks
        && (isDataReset ||
        (
          old.progresses.size == new.progresses.size
            && new.progresses.all { newProgress ->
            old.progresses.any { newProgress equivalentTo it } // to check whether all progress in `new` is equivalent to `old`
          }
        ))
    }

  private val scheduleJoint: Flow<CountDownProgressJoint> = queryResult.map {
    queryJointUseCase.getProgressJointForCountDown(it).firstOrNull()
      ?: throw IllegalStateException(
        "Query result from `scheduleId` of '${scheduleId.value}' " +
          "and `nowDateTime` of '${nowDateTime.value}' " +
          "results empty list of `CountDownProgressJoint`"
      )
  }

  private val progressData: Flow<ScheduleProgress> = combine(_nowDateTime, scheduleJoint) {
      timestamp, scheduleJoint ->
    scheduleProgressUseCase.ensureScheduleHasProgress(
      scheduleId = scheduleJoint.schedule.id,
      timestamp = timestamp,
    )
  }.flattenConcat()
    .distinctUntilChanged()


  

  val iconData: Flow<IconPicData> = scheduleJoint.map { joint ->
    joint.task.let {
      IconPicData(
        resId = iconUseCase.getResId(it.iconId),
        color = it.color,
        desc = it.name,
      )
    }
  }
  /*
  val taskIconId: Flow<Int> = scheduleJoint.map {
    iconUseCase.getResId(it.task.iconId)
  }
  val taskColor: Flow<String> = scheduleJoint.map {
    it.task.color
  }
   */


  /**
   * This is for [progressData] filtering with `distinctUntilChanged()`
   * because [progressData] tends to emit everytime there is a change
   * in DB. This view model of course updates the progress frequently.
   * `distinctUntilChanged()` prevents [timerFlow] to change everytime
   * the progress gets updated. [timerFlow] only needs to change
   * when total progress of a schedule change.
   */
  private val totalProgressFlow: Flow<Long> = scheduleJoint.map {
    it.schedule.totalProgress
  }
  private val totalProgress by lazy {
    MutableStateFlow(0L).addSource(scope, totalProgressFlow)
  }

  private val initialCountDownProgressFlow: Flow<Long> = combine(totalProgressFlow, progressData) {
      totalProgress, progressData ->
    totalProgress - progressData.actualProgress
  }
  private val initialCountDownProgress by lazy {
    MutableStateFlow(0L).addSource(scope, initialCountDownProgressFlow)
  }


  private val _progress = MutableStateFlow(0f)
  val progress: Flow<Float>
    get() = _progress

  private val countDownProgress = MutableStateFlow(0L)
  val countDownProgressStr: Flow<String> = countDownProgress.map {
    Texts.formatTimeToClock(it)
  }

  private val checkpointCountDownProgress = MutableStateFlow<Long?>(null)
  val checkpointCountDownProgressStr: Flow<String?> = checkpointCountDownProgress.map {
    //println("checkpointCountDownProgressStr = $it initialCountDownProgress = ${initialCountDownProgress.value}")
    if(it == null || it == initialCountDownProgress.value) { null }
    else { Texts.formatTimeToClock(it) }
  }

  /**
   * This stores actual progress that is saved to DB.
   * The value counts from 0 not [totalProgress] (This is not time left to count down).
   */
  private val actualTimeProgress = MutableStateFlow(-1L)

  /**
   * Flag whether current progress update in DB success or not.
   */
  val updateStatus: Flow<Boolean> = combine(progressData, actualTimeProgress) {
      progressData, actualTimeProgress ->
    //println("CountDownVm.updateStatus progressData = $progressData actualTimeProgress = $actualTimeProgress")
    if(actualTimeProgress >= 0) {
      scheduleProgressUseCase.updateProgress(
        progressData.id,
        actualTimeProgress
      ).map { it >= 0 }
    } else flow {}
  }.flattenConcat()

  private val _isFinished = MutableSharedFlow<Boolean>()
  val isFinished: Flow<Boolean>
    get() = _isFinished


  private val timerMillisFlow: Flow<Long> = combine(initialCountDownProgressFlow, checkpointCountDownProgress) {
      initial, checkpoint ->
    checkpoint ?: initial
  }
  private val timerMillis by lazy {
    MutableStateFlow(0L).addSource(scope, timerMillisFlow)
  }

  private val prevTimer = MutableStateFlow<CountDownTimer?>(null)
  private val timerFlow: Flow<CountDownTimer> = timerMillis.map { timeStart ->
    prevTimer.value?.cancel()

    countDownProgress.value = timeStart
    //checkpointCountDownProgress.value = timeStart
    setProgress(timeStart)

    val newTimer = object: CountDownTimer(timeStart, Const.tickerInterval) {

      /**
       * Callback fired on regular interval.
       * @param millisUntilFinished The amount of time until finished.
       */
      override fun onTick(millisUntilFinished: Long) {
        countDownProgress.value = millisUntilFinished
        setProgress(millisUntilFinished)
        autoSave(millisUntilFinished)
      }

      /**
       * Callback fired when the time is up.
       */
      override fun onFinish() {
        scope.launch {
          _isFinished.emit(true)
        }
      }
    }
    prevTimer.value = newTimer
    newTimer
  }
  private val _isCounting = MutableStateFlow(false)
  val isCounting: Flow<Boolean>
    get() = _isCounting


  private val countingTimer = combine(timerFlow, _isCounting) { timer, isCounting ->
    if(isCounting) {
      timer.start()
    } else {
      timer.cancel()
      timerMillis.value = countDownProgress.value
    }
  }



  init {
    scope.launch {
      launch {
        progress.collect {
          println("CountDownVm.progress = $it")
        }
      }
      /*
      launch {
        progressData.collect {
          actualTimeProgress.value = it.actualProgress
        }
      }
      // */
      ///*
      launch {
        countingTimer.collect {
          // just collect so that logic inside `countingTimer` can run. TODO: Optimize
        }
      }
      //*
      //TO DO: It seems that this collect process obstacles the checkpoint string flow
      /*
      DONE
      cause: somehow it updates several time via `autoSave` because false condition (`if`)
      resolution: check whether value inside `actualProgress` is previously same as computed value in `if`
      */
      launch {
        updateStatus.collect {
          //println("CountDownVm updateStatus = $it")
          // just collect so that logic inside `countingTimer` can run. TODO: Optimize
        }
      }
      // */
      // */
      //initTimerStart()
    }
  }


  /*
  private val updateStatus: Flow<Boolean> = checkpointTimeProgress.map {

  }
   */

  /**
   * This method will be called when this ViewModel is no longer used and will be destroyed.
   *
   *
   * It is useful when ViewModel observes some data and you need to clear this subscription to
   * prevent a leak of this ViewModel.
   */
  override fun onCleared() {
    prevTimer.value?.cancel()
  }

  fun loadFromDb(
    scheduleId: Int,
    timestamp: Long = System.currentTimeMillis(),
  ) {
    val scheduleIdFlow = this.scheduleId
    job?.cancel()
    job = scope.launch {
      scheduleIdFlow.value = scheduleId
      nowDateTime.value = timestamp
    }
  }

  /**
   * [countDownTimestamp] counts from [totalProgress] to 0 (descending).
   */
  fun resetCountDown(pauseCountDown: Boolean = true) {
    countDownProgress.value = checkpointCountDownProgress.value
      ?: initialCountDownProgress.value
    timerMillis.value = countDownProgress.value
    if(pauseCountDown) {
      _isCounting.value = false
    }
  }


  fun playCountDown(play: Boolean? = null) {
    _isCounting.value = play ?: !_isCounting.value
  }


  /**
   * [countDownTimestamp] counts from [totalProgress] to 0 (descending).
   */
  fun setCheckpoint(
    countDownTimestamp: Long = countDownProgress.value,
    total: Long = totalProgress.value,
    forceSet: Boolean = false,
  ) {
    val newProgress = total - countDownTimestamp
    if(forceSet || newProgress / Const.progressAutoSaveCheckpointTolerance
      != actualTimeProgress.value / Const.progressAutoSaveCheckpointTolerance) {
      checkpointCountDownProgress.value = countDownTimestamp
      actualTimeProgress.value = newProgress
    }
  }

  /**
   * [currentElapsedTime] is in millis.
   */
  private fun autoSave(
    //currentElapsedTime: Long,
    countDownTimestamp: Long = countDownProgress.value,
  ) {
    val total = totalProgress.value
    //println("CountDownVm.autoSave() total - countDownTimestamp = ${total - countDownTimestamp}")

    if(((total - countDownTimestamp) / Const.progressAutoSaveCheckpointTolerance) %
      Const.progressAutoSaveCheckpoint == 0L
    ) {
      setCheckpoint(countDownTimestamp, total)
    }
  }


  private fun setProgress(
    countDownTimestamp: Long = countDownProgress.value,
  ) {
    val total = totalProgress.value
    _progress.value = (total - countDownTimestamp) /
      total.toFloat()
  }

  private fun setCountDownProgressFromActual(
    actualProgress: Long,
  ) {
    countDownProgress.value = totalProgress.value -
      actualProgress
  }
}