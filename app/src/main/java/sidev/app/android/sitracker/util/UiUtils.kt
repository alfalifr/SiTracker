package sidev.app.android.sitracker.util

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalPagerApi
fun Modifier.pagerTransformation(
  pagerScope: PagerScope,
  page: Int,
): Modifier = pagerScope.run {
  graphicsLayer {
    // Calculate the absolute offset for the current page from the
    // scroll position. We use the absolute value which allows us to mirror
    // any effects for both directions
    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

    // We animate the scaleX + scaleY, between 85% and 100%
    lerp(
      start = 0.5f,
      stop = 1f,
      fraction = 1f - pageOffset.coerceIn(0f, 1f)
    ).also { scale ->
      scaleX = scale
      scaleY = scale
    }

    // We animate the alpha, between 50% and 100%
    alpha = lerp(
      start = 0.5f,
      stop = 1f,
      fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )
  }
}


//Source: https://stackoverflow.com/a/66502400
fun LazyListState.disableScroll(scope: CoroutineScope): LazyListState {
  scope.launch {
    scroll(scrollPriority = MutatePriority.PreventUserInput) {
      // Await indefinitely, blocking scrolls
      awaitCancellation()
    }
  }
  return this
}

fun LazyListState.reenableScrolling(scope: CoroutineScope): LazyListState {
  scope.launch {
    scroll(scrollPriority = MutatePriority.PreventUserInput) {
      // Do nothing, just cancel the previous indefinite "scroll"
    }
  }
  return this
}