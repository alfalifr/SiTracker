package sidev.app.android.sitracker.core.domain.model

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.R

enum class AppIcon(
  val id: Int,
  @DrawableRes
  val resId: Int,
) {
  Coding(0, R.drawable.ic_coding)

  ;

  companion object {
    operator fun get(iconId: Int): AppIcon =
      values()[iconId]
  }
}