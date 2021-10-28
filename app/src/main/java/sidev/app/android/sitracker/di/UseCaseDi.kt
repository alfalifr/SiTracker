package sidev.app.android.sitracker.di

import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.core.domain.usecase.IconUseCaseImpl
import sidev.app.android.sitracker.core.domain.usecase.RecommendationUseCase
import sidev.app.android.sitracker.core.domain.usecase.RecommendationUseCaseImpl

interface UseCaseDi {
  fun recommendationUseCase(): RecommendationUseCase
  fun iconUseCase(): IconUseCase
}

class UseCaseDiImpl(private val daoDi: DaoDi): UseCaseDi {
  override fun recommendationUseCase(): RecommendationUseCase = RecommendationUseCaseImpl(
    activeDateDao = daoDi.activeDateDao(),
    preferredTimeDao = daoDi.preferredTimeDao(),
    preferredDayDao = daoDi.preferredDayDao(),
    scheduleDao = daoDi.scheduleDao(),
    scheduleProgressDao = daoDi.scheduleProgressDao(),
    taskDao = daoDi.taskDao(),
  )
  override fun iconUseCase(): IconUseCase = IconUseCaseImpl()
}