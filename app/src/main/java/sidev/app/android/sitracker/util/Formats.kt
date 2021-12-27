package sidev.app.android.sitracker.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Formats {
  val dayOfWeek by lazy { 1 .. 7 }
  val dayOfMonth_start by lazy { 1 }
  val hourOfDay by lazy { 0 - 23 }
  const val dateFormat = "dd MMM yyyy"
  const val nonBlankStrMinLen = 3

  val simpleDateFormat: SimpleDateFormat
    get() = SimpleDateFormat(dateFormat, Locale.getDefault())

  fun doesDateStrFormatComply(dateStr: String): Boolean = try {
    simpleDateFormat.parse(dateStr) != null
  } catch(e: ParseException) {
    false
  }
}