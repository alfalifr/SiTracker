package sidev.app.android.sitracker.util

import android.text.format.Time
import java.lang.Math.pow
import java.text.DateFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow

//TODO: set localization.
object Texts {
  const val defaultLoadingText = "Loading..."
  const val schedule = "Schedule"
  const val noSchedule = "No Schedule"
  const val iconButton = "Edit Button"
  const val activeDates = "Active Dates"
  const val preferredTimes = "Preferred Times"
  const val preferredDays = "Preferred Days"
  const val reset = "Reset"
  const val play = "Play"
  const val setCheckpoint = "Set Checkpoint"

  const val noPreferredTimes = "No $preferredTimes"


  fun seeOther(count: Int): String = "see $count other..."


  //TODO: implement time formatting algo
  fun formatTimeToShortest(time: Long): String = time.toString()

  /**
   * Format [time] to HH:mm:ss format.
   * [time] is in millis.
   */
  fun formatTimeToClock(time: Long): String {
    val millisInSec = 1000L //TimeUnit.SECONDS.toMillis(1)
    val millisInMin = millisInSec * 60 //TimeUnit.MINUTES.toMillis(1)
    val millisInHour = millisInMin * 60 //TimeUnit.HOURS.toMillis(1)

    var decreasingTime = time
    val hour = (decreasingTime / millisInHour).also { decreasingTime -= it * millisInHour }
    val min = (decreasingTime / millisInMin).also { decreasingTime -= it * millisInMin }
    val sec = decreasingTime / millisInSec

    return "${lenSpecifiedNumStr(hour, 2)}:${lenSpecifiedNumStr(min, 2)}:${lenSpecifiedNumStr(sec, 2)}"
  }

  //TODO: implement duration formatting algo
  fun formatDurationToShortest(time: Long): String = time.toString()

  fun formatPriority(priority: Int): String = "Priority #$priority"

  fun formatProgress(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun format(progress: Float): String = "${String.format("%.0f", progress * 100)}%"

  fun intervalStr(interval: Pair<String, String?>): String = if(interval.second == null) interval.first
    else "${interval.first} - ${interval.second}"

  fun getDayName(day: Int): String {
    val dateFormat = DateFormatSymbols.getInstance(Locale.getDefault())
    return dateFormat.weekdays[day]
  }

  fun iconOf(name: String): String = "Icon of $name"
  fun editItem(name: String): String = "Edit $name"
  //fun iconOf(name: String): String = "Icon of $name"

  fun lenSpecifiedNumStr(num: Number, len: Int): String {
    val numLen = numStrLen(num)
    if(numLen >= len) {
      return num.toString()
    }
    var str = ""
    repeat(len - numLen) {
      str += "0"
    }
    str += num.toString()
    return str
  }

  /**
   * Count the length of [num] when represented as string in decimal (base 10).
   */
  fun numStrLen(num: Number): Int =
    if(num.toDouble().compareTo(0) == 0) 1
    else log10(num.toFloat()).toInt() +1
}