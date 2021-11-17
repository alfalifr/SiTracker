package sidev.app.android.sitracker.util

//TODO: set localization.
object Texts {
  const val defaultLoadingText = "Loading..."
  const val schedule = "Schedule"
  const val noSchedule = "No Schedule"
  const val iconButton = "Edit Button"
  const val activeDates = "Active Dates"
  const val preferredTimes = "Preferred Times"
  const val preferredDays = "Preferred Days"

  const val noPreferredTimes = "No $preferredTimes"

  //TODO: implement time formatting algo
  fun formatTimeToShortest(time: Long): String = time.toString()

  /**
   * Format [time] to HH:mm:ss format
   */
  //TODO: implement time formatting algo
  fun formatTimeToClock(time: Long): String = time.toString()

  //TODO: implement duration formatting algo
  fun formatDurationToShortest(time: Long): String = time.toString()

  fun formatPriority(priority: Int): String = "Priority #$priority"

  fun formatProgress(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun format(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun intervalStr(interval: Pair<String, String?>): String = if(interval.second == null) interval.first
    else "${interval.first} - ${interval.second}"

  fun iconOf(name: String): String = "Icon of $name"
  fun editItem(name: String): String = "Edit $name"
  //fun iconOf(name: String): String = "Icon of $name"
}