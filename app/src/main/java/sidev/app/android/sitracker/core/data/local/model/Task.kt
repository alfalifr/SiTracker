package sidev.app.android.sitracker.core.data.local.model


data class Task(
  val id: Int,
  val name: String,
  val priority: Int,
  val iconId: Int,
  val color: String,
)
