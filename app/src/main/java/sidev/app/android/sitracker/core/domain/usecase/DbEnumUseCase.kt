package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.data.local.model.Schedule
import sidev.app.android.sitracker.core.domain.model.IntervalTypes
import sidev.app.android.sitracker.core.domain.model.ProgressTypes

interface DbEnumUseCase {
  fun getIntervalLabel(id: Int): String
  fun getProgressTypeLabel(id: Int): String
  fun formatProgress(id: Int, progress: Long): String
  fun formatProgress(schedule: Schedule): String =
    formatProgress(
      id = schedule.id,
      progress = schedule.totalProgress,
    )
}

class DbEnumUseCaseImpl: DbEnumUseCase {
  override fun getIntervalLabel(id: Int): String = IntervalTypes[id].label

  override fun getProgressTypeLabel(id: Int): String = ProgressTypes[id].label

  override fun formatProgress(
    id: Int,
    progress: Long
  ): String = ProgressTypes[id].formatProgress(progress)
}