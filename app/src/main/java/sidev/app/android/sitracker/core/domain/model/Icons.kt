package sidev.app.android.sitracker.core.domain.model

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.R

enum class AppIcon(
  val id: Int,
  @DrawableRes
  val resId: Int,
) {
  Coding(0, R.drawable.ic_coding),
  Clip(1, R.drawable.ic_clip),
  Bin(2, R.drawable.ic_bin),

  ;

  companion object {
    operator fun get(iconId: Int): AppIcon =
      values().find { it.id == iconId }
        ?: throw IllegalArgumentException(
          "Can't find `AppIcon` with `iconId` of '$iconId'"
        )
  }
}



sealed class IconProgressionData(
  open val color: String,
  open val progressFraction: Float?,
)

data class IconProgressionPicData(
  @DrawableRes
  val resId: Int,
  override val color: String,
  override val progressFraction: Float?,
): IconProgressionData(
  color = color,
  progressFraction = progressFraction,
)

data class IconProgressionTextData(
  val text: String,
  override val color: String,
  override val progressFraction: Float?,
): IconProgressionData(
  color = color,
  progressFraction = progressFraction,
)