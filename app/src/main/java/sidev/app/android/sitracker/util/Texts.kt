package sidev.app.android.sitracker.util

//TODO: set localization.
object Texts {
  //TODO: implement time formatting algo
  fun formatTimeToShortest(time: Long): String = time.toString()
  //TODO: implement duration formatting algo
  fun formatDurationToShortest(time: Long): String = time.toString()

  fun formatPriority(priority: Int): String = "Priority #$priority"

  fun formatProgress(progress: Float): String = "${String.format("%.0f", progress * 100)}%"
}