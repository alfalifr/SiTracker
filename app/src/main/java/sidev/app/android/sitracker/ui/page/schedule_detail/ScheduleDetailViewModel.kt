@file:OptIn(ExperimentalCoroutinesApi::class)
package sidev.app.android.sitracker.ui.page.schedule_detail

import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint
import sidev.app.android.sitracker.core.domain.usecase.DbEnumUseCase
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.ui.model.ItemIcon
import sidev.app.android.sitracker.util.Color
import sidev.app.android.sitracker.util.DataMapper.toPreferredDayData
import sidev.app.android.sitracker.util.Formats
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.collect
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDetailViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val dbEnumUseCase: DbEnumUseCase,
  private val iconUseCase: IconUseCase,
  private val coroutineScope: CoroutineScope? = null,
): ViewModel() {

  private var job: Job? = null

  val scheduleId = MutableSharedFlow<Int>()
  private val _scheduleId = scheduleId.distinctUntilChanged()

  /*
  private val _scheduleIdMapped = _scheduleId.map {
    it.toString()
  }

  private val _scheduleIdMapped2 = _scheduleId.flatMapLatest {
    flow {
      //1/0
      emit(it.toString())
    }
  }
   */

  private val queryResult: Flow<ProgressQueryResult> = _scheduleId.flatMapLatest {
    println("AWAL scheduleId = $it")
    queryUseCase.queryScheduleDetail(it).also { progressQueryResult ->
      println("""
        scheduleId = $it
        progressQueryResult = $progressQueryResult
      """.trimIndent())
    }
  }
  private val queryJoint: Flow<ScheduleJoint> = queryResult.map {
    queryJointUseCase.getScheduleJoint(it).first().also { scheduleJoint ->
      println("""
        progressQueryResult = $it
        scheduleJoint = $scheduleJoint
      """.trimIndent())
    }
  }


  val scheduleLabel: Flow<String> = queryJoint.map {
    println("view model scheduleLabel label = ${it.schedule.label}")
    it.schedule.label
  }

  val taskIcon: Flow<ItemIcon> = queryJoint.map {
    val resId = iconUseCase.getResId(
      it.task.iconId
    )
    ItemIcon(
      resId,
      it.task.id
    )
  }

  val headerData: Flow<ScheduleDetailHeaderUiData> = queryJoint.map {
    ScheduleDetailHeaderUiData(
      totalProgress = dbEnumUseCase.formatProgress(it.schedule),
      interval = dbEnumUseCase.getIntervalLabel(it.schedule.intervalId),
      activeDates = it.activeDates.map { date ->
        Texts.formatTimeToShortest(date.startDate) to
          date.endDate?.let { Texts.formatTimeToShortest(it) }
      },
    )
  }

  val preferredTimes: Flow<ScheduleDetailPreferredTimeUi> = queryJoint.map { scheduleJoint ->
    ScheduleDetailPreferredTimeUi(
      scheduleJoint.preferredTimes.map { time ->
        Texts.formatTimeToShortest(time.startTime) to
          time.endTime?.let { Texts.formatTimeToShortest(it) }
      }
    )
  }

  /*
  private val dayNameFormat by lazy {
    //SimpleDateFormat("EEE", Locale.getDefault())
    DateFormatSymbols.getInstance(Locale.getDefault())
  }
   */
  val preferredDays: Flow<ScheduleDetailPreferredDayUi> = queryJoint.map { scheduleJoint ->
    scheduleJoint.toPreferredDayData()
  }


  init {
    viewModelScope.apply {
      launch {
        scheduleId.collect {
          println("in viewModel scheduleId = $it")
        }
      }
      launch {
        _scheduleId.collect {
          println("in viewModel _scheduleId = $it")
        }
      }
      /*
      launch {
        _scheduleIdMapped.collect {
          println("in viewModel _scheduleIdMapped = $it")
        }
      }
      launch {
        _scheduleIdMapped2.collect {
          println("in viewModel _scheduleIdMapped2 = $it")
        }
      }
       */
      launch {
        queryResult.collect {
          println("in viewModel queryResult = $it")
        }
      }
      launch {
        scheduleLabel.collect {
          println("in viewModel scheduleLabel = $it")
        }
      }

      collect(headerData) {
        println("VM.headerData = $it")
      }
    }
  }

  fun load(scheduleId: Int) {
    println("load AWAL scheduleId = $scheduleId coroutineScope = $coroutineScope")
    val scheduleIdFlow = this.scheduleId
    job?.cancel()
    job = (coroutineScope ?: viewModelScope).launch {
      println("load LAUNCH")
      scheduleIdFlow.emit(scheduleId)
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
    println("ScheduleDetailViewModel onCleared")
  }
}