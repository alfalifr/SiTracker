@file:OptIn(ExperimentalCoroutinesApi::class)
package sidev.app.android.sitracker.ui.page.schedule_detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import sidev.app.android.sitracker.core.domain.model.ProgressQueryResult
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint
import sidev.app.android.sitracker.core.domain.usecase.DbEnumUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryJointUseCase
import sidev.app.android.sitracker.core.domain.usecase.QueryUseCase
import sidev.app.android.sitracker.util.Texts

class ScheduleDetailViewModel(
  private val queryUseCase: QueryUseCase,
  private val queryJointUseCase: QueryJointUseCase,
  private val dbEnumUseCase: DbEnumUseCase,
): ViewModel() {
  val scheduleId = MutableSharedFlow<Int>()
  private val _scheduleId = scheduleId.distinctUntilChanged()

  private val queryResult: Flow<ProgressQueryResult> = _scheduleId.flatMapLatest {
    queryUseCase.queryScheduleDetail(it)
  }
  private val queryJoint: Flow<ScheduleJoint> = queryResult.map {
    queryJointUseCase.getScheduleJoint(it).first()
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

  val preferredTimes: Flow<ScheduleDetailPreferredTimeUi> = queryJoint.map {
    ScheduleDetailPreferredTimeUi(
      it.preferredTimes.map { time ->
        Texts.formatTimeToShortest(time.startTime) to
          time.endTime?.let { Texts.formatTimeToShortest(it) }
      }
    )
  }

  val preferredDays: Flow<ScheduleDetailPreferredDayUi> = queryJoint.map {
    ScheduleDetailPreferredDayUi(
      it.preferredDays.map { it.dayInWeek }
    )
  }
}