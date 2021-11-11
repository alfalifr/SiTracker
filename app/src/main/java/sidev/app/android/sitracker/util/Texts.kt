package sidev.app.android.sitracker.util

//TODO: set localization.
object Texts {
  const val defaultLoadingText = "Loading..."
  const val schedule = "Schedule"
  const val noSchedule = "No Schedule"
  const val iconButton = "Edit Button"

  //TODO: implement time formatting algo
  fun formatTimeToShortest(time: Long): String = time.toString()
  //TODO: implement duration formatting algo
  fun formatDurationToShortest(time: Long): String = time.toString()

  fun formatPriority(priority: Int): String = "Priority #$priority"

  fun formatProgress(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun format(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun iconOf(name: String): String = "Icon of $name"
  fun editItem(name: String): String = "Edit $name"
  //fun iconOf(name: String): String = "Icon of $name"
}