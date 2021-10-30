package sidev.app.android.sitracker.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import sidev.app.android.sitracker.util.model.Direction

@ExperimentalAnimationApi
@Composable
fun MainMenuContentTransition(
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
      }
    ),
    exit = slideOutHorizontally(
      targetOffsetX = { fullWidth ->
        if(slidingDirection == Direction.RIGHT) 0
        else 0
      }
    ),
    content = content,
  )
}