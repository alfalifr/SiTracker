package sidev.app.android.sitracker.core.domain.usecase

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.core.domain.model.*

interface IconUseCase {
  fun getAllAvailableIcons(): List<AppIcon>
  @DrawableRes
  fun getResId(iconId: Int): Int
  fun getIconProgressionData(progressJoint: ProgressJoint): IconProgressionPicData
  fun getIconProgressionData(scheduleJoint: ScheduleJoint): IconProgressionPicData
}

class IconUseCaseImpl: IconUseCase {
  override fun getAllAvailableIcons(): List<AppIcon> = AppIcons.values().asList()
  override fun getResId(iconId: Int): Int = AppIcons[iconId].resId
  override fun getIconProgressionData(progressJoint: ProgressJoint): IconProgressionPicData =
    IconProgressionPicData(
      resId = getResId(progressJoint.task.iconId),
      color = progressJoint.task.color,
      progressFraction = progressJoint.progress.actualProgress.toFloat() /
        progressJoint.schedule.totalProgress,
      desc = progressJoint.task.name,
    )

  override fun getIconProgressionData(
    scheduleJoint: ScheduleJoint
  ): IconProgressionPicData =
    IconProgressionPicData(
      resId = getResId(scheduleJoint.task.iconId),
      color = scheduleJoint.task.color,
      progressFraction = scheduleJoint.progress?.let {
        it.actualProgress.toFloat() /
          scheduleJoint.schedule.totalProgress
      },
      desc = scheduleJoint.task.name,
    )
}