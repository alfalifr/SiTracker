package sidev.app.android.sitracker.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
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