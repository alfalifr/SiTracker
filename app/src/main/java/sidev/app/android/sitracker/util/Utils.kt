package sidev.app.android.sitracker.util

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import sidev.app.android.sitracker.core.data.local.model.ActiveDate
import sidev.app.android.sitracker.core.domain.model.CalendarEvent
import sidev.app.android.sitracker.core.domain.model.CalendarMark
import sidev.app.android.sitracker.di.DiCenter
import sidev.app.android.sitracker.ui.theme.GreenLight
import sidev.app.android.sitracker.ui.theme.Red
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
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




/**
 * Get a single color in certain [point] in color gradient between [first] and [last] color.
 *
 * [point] should have range between 0.0 - 1.0.
 * If [point] is less or greater then the range, then [point] will be
 * rounded up / down to the nearest value.
 */
fun getColorPointFromLinearGradient(
  first: Color,
  last: Color,
  point: Double, // from 0.0 - 1.0
): Color {
  if(point <= 0.0) return first
  if(point >= 1.0) return last

  //first.
  //val firstARGB = ARGBColor.fromColor(first);
  //final lastARGB = ARGBColor.fromColor(last);

  val aDiff = last.alpha - first.alpha
  val rDiff = last.red - first.red
  val gDiff = last.green - first.green
  val bDiff = last.blue - first.blue

  val aRes = (aDiff * point).toFloat() + first.alpha
  val rRes = (rDiff * point).toFloat() + first.red
  val gRes = (gDiff * point).toFloat() + first.green
  val bRes = (bDiff * point).toFloat() + first.blue

  return Color(
    alpha = aRes,
    red = rRes,
    green = gRes,
    blue = bRes,
  )
}

fun getScoreColor(
  score: Number,
  scale: Int = Const.scoreScale,
): Color = getColorPointFromLinearGradient(
  first = Red,
  last = GreenLight,
  point = score.toDouble() / scale,
)

//Source: https://stackoverflow.com/a/6540378
fun getHexString(colorValue: Int) =
  "#" + Integer.toHexString(colorValue)
  //String.format("#%08X", (0xFFFFFFFF and colorValue))


fun <T, R> Iterator<T>.map(transform: (T) -> R): List<R> {
  val list = mutableListOf<R>()
  for(e in this) {
    list += transform(e)
  }
  return list
}


/**
 * Just like [indexOfFirst] but the last indexed element
 * is checked first.
 */
fun <T> List<T>.indexOfFirstFromBack(predicate: (T) -> Boolean): Int {
  for(i in lastIndex downTo 0) {
    if(predicate(this[i])) {
      return i
    }
  }
  return -1
}

/**
 * Just like [find] but the last indexed element
 * is checked first.
 */
fun <T> List<T>.findFromBack(predicate: (T) -> Boolean): T? =
  indexOfFirstFromBack(predicate).let {
    if(it >= 0) this[it] else null
  }


/**
 * Group this [List] into some group of [T].
 * If [predicate] returns `true`, then current [T] will be added to current group.
 * If [predicate] returns `false`, then current [T] will be added to new group.
 */
fun <T> Iterable<T>.inSameGroup(predicate: (a: T, b: T) -> Boolean): List<List<T>> {
  val itr = iterator()
  if(!itr.hasNext()) {
    return emptyList()
  }

  val result = mutableListOf<List<T>>()
  var currentGroup = mutableListOf<T>()

  var a = itr.next()
  currentGroup += a
  result += currentGroup

  for(b in this) {
    if(!predicate(a, b)) {
      currentGroup = mutableListOf()
      result += currentGroup
    }
    currentGroup += b
    a = b
  }

  return result
}


fun <T1, T2> combine(
  itr1: Iterable<T1>,
  itr2: Iterable<T2>,
  block: (Pair<T1, T2>) -> Unit,
) {
  val itr1 = itr1.iterator()
  val itr2 = itr2.iterator()
  if(!itr1.hasNext() || !itr2.hasNext()) {
    return
  }
  for(e1 in itr1) {
    for(e2 in itr2) {
      block(e1 to e2)
    }
  }
}

fun <T1, T2, R> combineMap(
  itr1: Iterable<T1>,
  itr2: Iterable<T2>,
  block: (Pair<T1, T2>) -> R,
): List<R> {
  val itr1 = itr1.iterator()
  val itr2 = itr2.iterator()
  if(!itr1.hasNext() || !itr2.hasNext()) {
    return emptyList()
  }
  val result = mutableListOf<R>()
  for(e1 in itr1) {
    for(e2 in itr2) {
      result += block(e1 to e2)
    }
  }
  return result
}


fun <T> Iterable<T>.mergeIf(
  condition: (a: T, b: T) -> Boolean,
  merge: (a: T, b: T) -> T,
): List<T> {
  /**
   * Returns false if there is no modification to [list].
   */
  fun mergeInOneIteration(list: MutableList<T>): Boolean {
    if(list.isEmpty()) {
      return false
    }
    val itr = list.listIterator()
    var current = itr.next()
    var mod = 0

    for(e in itr) {
      val prev = current
      current = e
      if(condition(prev, e)) {
        val merged = merge(prev, e)
        itr.remove()
        list[0] = merged
        current = merged
        mod++
      }
    }

    return mod > 0
  }

  val result = this.toMutableList()
  while(mergeInOneIteration(result)) {
    //do nothing cuz operation is done in `mergeInOneIteration`
  }
  return result
}

fun afaf() {
  val a = mutableSetOf<Int>()
  a.iterator()
}



operator fun ActiveDate.contains(time: Long): Boolean =
  startDate <= time && (
    endDate == null || time <= endDate
  )

operator fun ActiveDate.contains(other: ActiveDate): Boolean =
  scheduleId == other.scheduleId
    && startDate <= other.startDate
    && (endDate == null
      || (other.endDate != null && endDate >= other.endDate)
    )

infix fun ActiveDate.overlaps(other: ActiveDate): Boolean =
  scheduleId == other.scheduleId
    && (
    (endDate == null || other.endDate == null)
    || (startDate >= other.startDate
          && startDate <= other.endDate)
    || (other.startDate >= startDate
          && other.startDate <= endDate)
    )

fun ActiveDate.mergeIfOverlaps(other: ActiveDate): ActiveDate? =
  if(!(this overlaps other)) null
  else merge(other)

fun ActiveDate.merge(other: ActiveDate): ActiveDate = copy(
  startDate = min(startDate, other.startDate),
  endDate = if(endDate == null || other.endDate == null) null
    else max(endDate, other.endDate),
)



infix fun ActiveDate.overlaps(other: UnclosedLongRange): Boolean =
  (endDate == null || other.end == null)
    || (startDate >= other.start
        && startDate <= other.end)
    || (other.start >= startDate
        && other.start <= endDate)



infix fun CalendarEvent.inSamePeriodAs(other: CalendarEvent): Boolean =
  intervals.size == other.intervals.size
    && intervals.containsAll(other.intervals)

fun CalendarEvent.mergeIfInSamePeriod(
  other: CalendarEvent,
  mark: CalendarMark? = null,
): CalendarEvent? =
  if(!(this inSamePeriodAs other)) null
  else merge(other, mark)

fun CalendarEvent.merge(
  other: CalendarEvent,
  mark: CalendarMark? = null,
): CalendarEvent = copy(
  legends = legends + other.legends,
  mark = mark ?: this.mark ?: other.mark
)



fun colorLuminance(r: Int, g: Int, b: Int): Double {
  fun factor(colorComp: Int): Double =
    (colorComp / 255).let {
      if(it <= 0.03928) it / 12.92
      else ((it + 0.055) / 1.055).pow(2.4)
    }

  return factor(r) * 0.2126 +
    factor(g) * 0.7152 +
    factor(b) * 0.0722
}

fun colorContrast(rgb1: Triple<Int, Int, Int>, rgb2: Triple<Int, Int, Int>): Double {
  //contrast()
  val lum1 = with(rgb1) { colorLuminance(first, second, third) }
  val lum2 = with(rgb2) { colorLuminance(first, second, third) }
  val brightest = max(lum1, lum2)
  val darkest = min(lum1, lum2)
  return (brightest + 0.05) / (darkest + 0.05)
}

/*
colorContrast([255, 255, 255], [255, 255, 0]); // 1.074 for yellow
colorContrast([255, 255, 255], [0, 0, 255]); // 8.592 for blue
// minimal recommended contrast ratio is 4.5, or 3 for larger font-sizes
 */


fun getSecFromMillis(millis: Long): Long = millis / 1000L


fun <T> MutableStateFlow<T>.addSource(
  scope: CoroutineScope,
  flow: Flow<T>,
): MutableStateFlow<T> {
  scope.launch {
    flow.collect {
      value = it
    }
  }
  return this
}

fun <T> CoroutineScope.collect(flow: Flow<T>, collector: suspend (T) -> Unit) {
  launch {
    flow.collect(collector)
  }
}