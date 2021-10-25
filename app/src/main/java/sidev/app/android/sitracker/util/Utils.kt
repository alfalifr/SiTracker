package sidev.app.android.sitracker.util

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

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