package sidev.app.android.sitracker.core.domain.model

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.R

interface AppIcon {
  val id: Int
  val resId: Int
}

enum class AppIcons(
  override val id: Int,
  @DrawableRes
  override val resId: Int,
): AppIcon {
  Coding(0, R.drawable.ic_coding),
  Clip(1, R.drawable.ic_clip),
  Bin(2, R.drawable.ic_bin),
  Edit(3, R.drawable.ic_edit),

  ;

  companion object {
    operator fun get(iconId: Int): AppIcon =
      values().find { it.id == iconId }
        ?: throw IllegalArgumentException(
          "Can't find `AppIcon` with `iconId` of '$iconId'"
        )
  }
}


interface IconPicData {
  val resId: Int
  val color: String
  val desc: String?
}

data class IconPicDataImpl(
  override val resId: Int,
  override val color: String,
  override val desc: String?,
): IconPicData

fun IconPicData(
  resId: Int,
  color: String,
  desc: String?,
): IconPicData = IconPicDataImpl(
  resId, color, desc,
)


sealed class IconProgressionData(
  open val color: String,
  open val progressFraction: Float?,
)

data class IconProgressionPicData(
  @DrawableRes
  override val resId: Int,
  override val color: String,
  override val progressFraction: Float?,
  override val desc: String?,
): IconProgressionData(
  color = color,
  progressFraction = progressFraction,
), IconPicData

data class IconProgressionTextData(
  val text: String,
  override val color: String,
  override val progressFraction: Float?,
): IconProgressionData(
  color = color,
  progressFraction = progressFraction,
)