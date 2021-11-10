package sidev.app.android.sitracker.di

import sidev.app.android.sitracker.ui.usecase.CalendarUiUseCase
import sidev.app.android.sitracker.ui.usecase.CalendarUiUseCaseImpl


interface UiUseCaseDi {
  fun calendarUiUseCase(): CalendarUiUseCase
}

class UiUseCaseDiImpl: UiUseCaseDi {
  override fun calendarUiUseCase(): CalendarUiUseCase = CalendarUiUseCaseImpl()
}