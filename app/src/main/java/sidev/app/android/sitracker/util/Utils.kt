package sidev.app.android.sitracker.util

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import java.util.*
import java.util.concurrent.TimeUnit

val BoxWithConstraintsScope.maxSquareSideLen: Dp
  get() = if(maxWidth <= maxHeight) maxWidth else maxHeight

val Constraints.maxSquareSideLen: Int
  get() = if(maxWidth <= maxHeight) maxWidth else maxHeight


fun getStartCenterAligned(
  parentLen: Int,
  childLen: Int,
  offset: Int = 0,
): Int = ((parentLen - childLen) / 2) + offset


val Constraints.maxSquareConstraint: Constraints
  get() {
    val maxLen = maxSquareSideLen
    return copy(
      maxHeight = maxLen,
      maxWidth = maxLen,
    )
  }

//TODO: implement time formatting algo
fun formatTimeToShortest(time: Long): String = time.toString()


fun getTimeMillisInDay(cal: Calendar): Long {
  val t0Hour = TimeUnit.HOURS.toMillis(cal[Calendar.HOUR_OF_DAY].toLong())
  val t0Min = TimeUnit.MINUTES.toMillis(cal[Calendar.MINUTE].toLong())
  val t0Sec = TimeUnit.SECONDS.toMillis(cal[Calendar.SECOND].toLong())
  val t0Milli = cal[Calendar.MILLISECOND]

  return t0Hour + t0Min + t0Sec + t0Milli
}