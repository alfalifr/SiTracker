package sidev.app.android.sitracker.util

import android.content.Context
import android.text.format.Time
import androidx.annotation.StringRes
import sidev.app.android.sitracker.R
import java.lang.Math.pow
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
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
  const val next = "Next"
  const val previous = "Previous"
  const val finish = "Finish"

  const val noPreferredTimes = "No $preferredTimes"


  fun seeOther(count: Int): String = "see $count other..."


  //TODO: implement time formatting algo
  fun formatTimeToShortest(time: Long): String = time.toString()

  fun formatTimeToDate(time: Long): String {
    val format = Formats.simpleDateFormat
    return format.format(Date(time))
  }

  /**
   * Format [time] to HH:mm:ss format.
   * [time] is in millis.
   */
  fun formatTimeToClock(
    time: Long,
    withSecond: Boolean = true,
  ): String {
    val units = breakTimeMillisToClockComponent(time)

    var str = "${lenSpecifiedNumStr(units[0], 2)}:${lenSpecifiedNumStr(units[1], 2)}"
    if(withSecond) {
      str += ":${lenSpecifiedNumStr(units[2], 2)}"
    }

    return str
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

  fun getShortDayName(day: Int): String {
    val dayName = getDayName(day)
    return dayName.take(3)
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


  fun getNamedFieldErrorMsg(
    context: Context,
    @StringRes errorMsgStrId: Int,
    @StringRes fieldNameStrId: Int,
  ): String {
    val fieldNameStr = context.getString(fieldNameStrId)
    return context.getString(errorMsgStrId, fieldNameStr)
  }

  fun getBlankFieldErrorMsg(
    context: Context, @StringRes fieldNameStrId: Int,
  ): String = getNamedFieldErrorMsg(
    context, R.string.field_invalid_blank_message, fieldNameStrId,
  )
  fun getNonNumericFieldErrorMsg(
    context: Context, @StringRes fieldNameStrId: Int,
  ): String = getNamedFieldErrorMsg(
    context, R.string.field_invalid_non_numeric_message, fieldNameStrId,
  )

  fun getClockStrDelimiter(
    clockStr: String,
    elseBlock: (() -> Char?)? = null
  ): Char? = when {
    ':' in clockStr -> ':'
    '.' in clockStr -> '.'
    else -> elseBlock?.invoke()
  }
}