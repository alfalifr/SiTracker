package sidev.app.android.sitracker.core.domain.usecase

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.core.domain.model.AppIcon
import sidev.app.android.sitracker.core.domain.model.IconProgressionPicData
import sidev.app.android.sitracker.core.domain.model.ProgressJoint
import sidev.app.android.sitracker.core.domain.model.ScheduleJoint

interface IconUseCase {
  @DrawableRes
  fun getResId(iconId: Int): Int
  fun getIconProgressionData(progressJoint: ProgressJoint): IconProgressionPicData
  fun getIconProgressionData(scheduleJoint: ScheduleJoint): IconProgressionPicData
}

class IconUseCaseImpl: IconUseCase {
  override fun getResId(iconId: Int): Int = AppIcon[iconId].resId
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