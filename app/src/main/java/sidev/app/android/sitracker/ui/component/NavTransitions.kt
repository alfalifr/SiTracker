@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import sidev.app.android.sitracker.util.model.Direction

@Composable
fun SlidingTransition(
  slidingDirection: Direction,
  content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  AnimatedVisibility(
    visibleState = remember { MutableTransitionState(false) }
      .apply { targetState = true },
    //visible = true,
    //initiallyVisible = false,
    enter = slideIn(
      initialOffset = { fullSize ->
        var x = 0
        var y = 0
        when(slidingDirection) {
          Direction.RIGHT -> x = -fullSize.width
          Direction.LEFT -> x = fullSize.width
          Direction.UP -> y = fullSize.height // remember! android coordinate start at 0, 0 at top-left screen, it means the lower the position, the more y coordinate
          Direction.DOWN -> y = -fullSize.height
        }
        println("SlidingTransition slidingDirection= $slidingDirection x= $x y= $y")
        IntOffset(x, y)
      },
      animationSpec = tween(durationMillis = 800),
    ),
    exit = slideOut(
      targetOffset = { fullSize ->
        var x = 0
        var y = 0
        when(slidingDirection) {
          Direction.RIGHT -> x = fullSize.width
          Direction.LEFT -> x = -fullSize.width
          Direction.UP -> y = -fullSize.height
          Direction.DOWN -> y = fullSize.height
        }
        IntOffset(x, y)
      },
      animationSpec = tween(durationMillis = 800),
    ),
    content = content,
  )
}