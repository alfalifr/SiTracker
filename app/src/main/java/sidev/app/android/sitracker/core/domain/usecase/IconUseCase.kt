package sidev.app.android.sitracker.core.domain.usecase

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.core.domain.model.AppIcon

interface IconUseCase {
  @DrawableRes
  fun getResId(iconId: Int): Int
}

class IconUseCaseImpl: IconUseCase {
  override fun getResId(iconId: Int): Int = AppIcon[iconId].resId
}