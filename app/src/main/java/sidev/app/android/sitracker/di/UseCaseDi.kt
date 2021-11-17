package sidev.app.android.sitracker.di

import sidev.app.android.sitracker.core.domain.usecase.*

interface UseCaseDi {
  fun queryUseCase(): QueryUseCase
  fun queryJointUseCase(): QueryJointUseCase
  fun iconUseCase(): IconUseCase
  fun recommendationUseCase(): RecommendationUseCase
  fun scheduleItemUseCase(): ScheduleItemUseCase
  fun scheduleProgressUseCase(): ScheduleProgressUseCase
  fun timeUseCase(): TimeUseCase
  fun calendarUseCase(): CalendarUseCase
  fun dbEnumUseCase(): DbEnumUseCase
}

class UseCaseDiImpl(private val daoDi: DaoDi): UseCaseDi {
  override fun queryUseCase(): QueryUseCase = QueryUseCaseImpl(
    activeDateDao = daoDi.activeDateDao(),
    preferredTimeDao = daoDi.preferredTimeDao(),
    preferredDayDao = daoDi.preferredDayDao(),
    scheduleDao = daoDi.scheduleDao(),
    scheduleProgressDao = daoDi.scheduleProgressDao(),
    taskDao = daoDi.taskDao(),
    progressTypeDao = daoDi.progressTypeDao(),
    intervalTypeDao = daoDi.intervalDao(),
  )

  override fun queryJointUseCase(): QueryJointUseCase = QueryJointUseCaseImpl()
  override fun iconUseCase(): IconUseCase = IconUseCaseImpl()
  override fun recommendationUseCase(): RecommendationUseCase = RecommendationUseCaseImpl()
  override fun scheduleItemUseCase(): ScheduleItemUseCase = ScheduleItemUseCaseImpl(
    iconUseCase = iconUseCase(),
  )

  override fun scheduleProgressUseCase(): ScheduleProgressUseCase = ScheduleProgressUseCaseImpl(
    progressDao = daoDi.scheduleProgressDao(),
    scheduleDao = daoDi.scheduleDao(),
    intervalType = daoDi.intervalDao(),
  )

  override fun timeUseCase(): TimeUseCase = TimeUseCaseImpl()
  override fun calendarUseCase(): CalendarUseCase = CalendarUseCaseImpl(
    iconUseCase = iconUseCase(),
    timeUseCase = timeUseCase(),
  )

  override fun dbEnumUseCase(): DbEnumUseCase = DbEnumUseCaseImpl()
}