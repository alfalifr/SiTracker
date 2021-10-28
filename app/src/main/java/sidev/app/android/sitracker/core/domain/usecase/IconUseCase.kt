package sidev.app.android.sitracker.core.domain.usecase

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.core.domain.model.AppIcon
import sidev.app.android.sitracker.core.domain.model.IconProgressionData
import sidev.app.android.sitracker.core.domain.model.ProgressJoint

interface IconUseCase {
  @DrawableRes
  fun getResId(iconId: Int): Int
  fun getIconProgressionData(progressJoint: ProgressJoint): IconProgressionData
}

class IconUseCaseImpl: IconUseCase {
  override fun getResId(iconId: Int): Int = AppIcon[iconId].resId
  override fun getIconProgressionData(progressJoint: ProgressJoint): IconProgressionData =
    IconProgressionData(
      resId = getResId(progressJoint.task.iconId),
      color = progressJoint.task.color,
      progressFraction = progressJoint.progress.actualProgress.toFloat() /
        progressJoint.schedule.totalProgress
    )
}