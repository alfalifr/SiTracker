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
import sidev.app.android.sitracker.core.data.local.model.ScheduleProgress
import sidev.app.android.sitracker.core.domain.model.CountDownProgressJoint
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.core.domain.usecase.ScheduleProgressUseCase
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.Stopwatch
import sidev.app.android.sitracker.util.Texts

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

  private val progressData = combine(_nowDateTime, scheduleJoint) {
      timestamp, scheduleJoint ->
    scheduleProgressUseCase.ensureScheduleHasProgress(
      scheduleId = scheduleJoint.schedule.id,
      timestamp = timestamp,
    )
  }.flattenConcat()

  private val initialProgressFlow: Flow<Long> = progressData.map {
    it.actualProgress
  }
  private val initialProgress = MutableStateFlow(0L)

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
  private val totalProgress = MutableStateFlow(0L)
  

  val taskIconId: Flow<Int> = scheduleJoint.map {
    iconUseCase.getResId(it.task.iconId)
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
    if(it == null) { null }
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

  private val stopwatch by lazy { Stopwatch() }
  private val timerFlow: Flow<CountDownTimer> = combine(initialProgressFlow, totalProgressFlow) {
      initialProgress, totalProgress ->

    val timeLeft = totalProgress - initialProgress
    countDownProgress.value = timeLeft
    setProgress(timeLeft)

    object: CountDownTimer(timeLeft, Const.tickerInterval) {
      /**
       * Callback fired on regular interval.
       * @param millisUntilFinished The amount of time until finished.
       */
      override fun onTick(millisUntilFinished: Long) {
        countDownProgress.value = millisUntilFinished
        setProgress(millisUntilFinished)
        autoSave(
          stopwatch.currentElapsedTimeInMillis,
          millisUntilFinished,
        )
      }

      /**
       * Callback fired when the time is up.
       */
      override fun onFinish() {
        scope.launch {
          _isFinished.emit(true)
          stopwatch.stop()
        }
      }
    }
  }
  private val timer = MutableStateFlow<CountDownTimer?>(null)



  init {
    scope.launch {
      launch {
        progressData.collect {
          actualTimeProgress.value = it.actualProgress
        }
      }
      launch {
        timerFlow.collect {
          timer.value = it
        }
      }
      launch {
        totalProgressFlow.collect {
          totalProgress.value = it
        }
      }
      launch {
        initialProgressFlow.collect {
          initialProgress.value = it
        }
      }
    }
  }


  /*
  private val updateStatus: Flow<Boolean> = checkpointTimeProgress.map {

  }
   */

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
  fun setCheckpoint(
    countDownTimestamp: Long = countDownProgress.value,
  ) {
    checkpointCountDownProgress.value = countDownTimestamp
    actualTimeProgress.value = totalProgress.value -
      countDownTimestamp +
      initialProgress.value
  }

  /**
   * [currentElapsedTime] is in millis.
   */
  private fun autoSave(
    currentElapsedTime: Long,
    countDownTimestamp: Long = countDownProgress.value,
  ) {
    if(currentElapsedTime %
      Const.progressAutoSaveCheckpoint == 0L
    ) {
      setCheckpoint(countDownTimestamp)
    }
  }

  fun startCount() {
    timer.value?.also {
      stopwatch.start()
      it.start()
    }
  }

  private fun setProgress(
    countDownTimestamp: Long = countDownProgress.value,
  ) {
    _progress.value = totalProgress.value.toFloat() /
      countDownTimestamp +
      initialProgress.value
  }

  private fun setCountDownProgressFromActual(
    actualProgress: Long,
  ) {
    countDownProgress.value = totalProgress.value -
      actualProgress
  }
}