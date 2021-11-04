package sidev.app.android.sitracker.util

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import sidev.app.android.sitracker.di.DiCenter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log
import kotlin.math.pow


fun loge(
  msg: String,
  tag: String = "TAG",
) = Log.e(tag, msg)


@Composable
inline fun <reified T: ViewModel> defaultViewModel(): T =
  viewModel(factory = DiCenter.diGraph.vmDi())

val BoxWithConstraintsScope.maxSquareSideLen: Dp
  get() = if(maxWidth <= maxHeight) maxWidth else maxHeight

val Constraints.maxSquareSideLen: Int
  get() = if(maxWidth <= maxHeight) maxWidth else maxHeight


fun Color(hexString: String): Color =
  Color(android.graphics.Color.parseColor(hexString))


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



fun getTimeMillisInDay(cal: Calendar): Long {
  val t0Hour = TimeUnit.HOURS.toMillis(cal[Calendar.HOUR_OF_DAY].toLong())
  val t0Min = TimeUnit.MINUTES.toMillis(cal[Calendar.MINUTE].toLong())
  val t0Sec = TimeUnit.SECONDS.toMillis(cal[Calendar.SECOND].toLong())
  val t0Milli = cal[Calendar.MILLISECOND]

  return t0Hour + t0Min + t0Sec + t0Milli
}

fun getDateMillis(time: Long): Long {
  val cal = Calendar.getInstance()
  cal.timeInMillis = time
  return getDateMillis(cal)
}
fun getDateMillis(cal: Calendar): Long {
  val timeInDay = getTimeMillisInDay(cal)
  return cal.timeInMillis - timeInDay
}


infix fun Int.hasMask(other: Int): Boolean = (this and other) == other
infix fun Int.notHasMask(other: Int): Boolean = !hasMask(other)

fun Int.hasMask(vararg other: Int): Boolean = other.all { this hasMask it }
//fun Int.notHasMask(vararg other: Int): Boolean = !hasMask(*other)


/**
 * [indexIgnoreNulls] true it means an array / list
 * doesn't contain null elements. Null element is represented
 * by absent of [elementMask] in [allMask] (allMask and partMask != partMask).
 */
fun getIndexWithMask(
  allMask: Int,
  elementMask: Int,
  indexIgnoreNulls: Boolean = true,
): Int? {
  if(allMask notHasMask elementMask) {
    return null
  }

  //val bitShiftDouble = log(elementMask.toDouble(), 2.0) //elementMask.toDouble().pow(1/2.0)
  val bitShift = log(elementMask.toDouble(), 2.0).toInt() //elementMask.toDouble().pow(1/2.0).toInt()

  //println("allMask = $allMask elementMask = $elementMask bitShift = $bitShift bitShiftDouble = $bitShiftDouble")

  if(!indexIgnoreNulls) {
    return bitShift
  }

  var currentIndex = 0
  for(i in 0 until bitShift) {
    //println("i = $i allMask hasMask i => ${allMask hasMask i}")
    if(allMask hasMask (1 shl i)) {
      currentIndex++
    }
  }

  //println("currentIndex = $currentIndex")

  return currentIndex
}

fun <T> List<T>.getWithMask(
  allMask: Int,
  elementMask: Int,
  indexIgnoreNulls: Boolean = true,
): T? = getIndexWithMask(
  allMask, elementMask, indexIgnoreNulls
)?.let { this[it] }