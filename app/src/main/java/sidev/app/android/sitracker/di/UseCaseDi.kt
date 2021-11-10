package sidev.app.android.sitracker.di

import sidev.app.android.sitracker.core.domain.usecase.*

interface UseCaseDi {
  fun queryUseCase(): QueryUseCase
  fun queryJointUseCase(): QueryJointUseCase
  fun iconUseCase(): IconUseCase
  fun recommendationUseCase(): RecommendationUseCase
  fun scheduleItemUseCase(): ScheduleItemUseCase
}

class UseCaseDiImpl(private val daoDi: DaoDi): UseCaseDi {
  override fun queryUseCase(): QueryUseCase = QueryUseCaseImpl(
    activeDateDao = daoDi.activeDateDao(),
    preferredTimeDao = daoDi.preferredTimeDao(),
    preferredDayDao = daoDi.preferredDayDao(),
    scheduleDao = daoDi.scheduleDao(),
    scheduleProgressDao = daoDi.scheduleProgressDao(),
    taskDao = daoDi.taskDao(),
  )

  override fun queryJointUseCase(): QueryJointUseCase = QueryJointUseCaseImpl()
  override fun iconUseCase(): IconUseCase = IconUseCaseImpl()
  override fun recommendationUseCase(): RecommendationUseCase = RecommendationUseCaseImpl()
  override fun scheduleItemUseCase(): ScheduleItemUseCase = ScheduleItemUseCaseImpl(
    iconUseCase = iconUseCase(),
  )
}