@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import sidev.app.android.sitracker.util.model.Direction

@Composable
fun HorizontalSlidingTransition(
  slidingDirection: Direction,
  content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  AnimatedVisibility(
    visibleState = remember { MutableTransitionState(false) }
      .apply { targetState = true },
    //visible = true,
    //initiallyVisible = false,
    enter = slideInHorizontally(
      initialOffsetX = { fullWidth ->
        if(slidingDirection == Direction.RIGHT) fullWidth
        else -fullWidth
      },
      animationSpec = tween(durationMillis = 800),
    ),
    exit = slideOutHorizontally(
      targetOffsetX = { fullWidth ->
        if(slidingDirection == Direction.RIGHT) 0
        else 0
      },
      animationSpec = tween(durationMillis = 800),
    ),
    content = content,
  )
}