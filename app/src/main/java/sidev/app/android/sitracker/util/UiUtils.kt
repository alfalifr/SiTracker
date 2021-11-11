package sidev.app.android.sitracker.util

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.android.material.snackbar.BaseTransientBottomBar
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


/**
 * This modifier gives [receiver] the calculated
 * size of wrapped composable.
 */
fun Modifier.getSize(receiver: MeasureScope.(Size) -> Unit) = this.then(
  object: LayoutModifier {
    /**
     * The function used to measure the modifier. The [measurable] corresponds to the
     * wrapped content, and it can be measured with the desired constraints according
     * to the logic of the [LayoutModifier]. The modifier needs to choose its own
     * size, which can depend on the size chosen by the wrapped content (the obtained
     * [Placeable]), if the wrapped content was measured. The size needs to be returned
     * as part of a [MeasureResult], alongside the placement logic of the
     * [Placeable], which defines how the wrapped content should be positioned inside
     * the [LayoutModifier]. A convenient way to create the [MeasureResult]
     * is to use the [MeasureScope.layout] factory function.
     *
     * A [LayoutModifier] uses the same measurement and layout concepts and principles as a
     * [Layout], the only difference is that they apply to exactly one child. For a more detailed
     * explanation of measurement and layout, see [MeasurePolicy].
     */
    override fun MeasureScope.measure(
      measurable: Measurable,
      constraints: Constraints
    ): MeasureResult {
      val placeable = measurable.measure(constraints)
      receiver(
        Size(
          width = placeable.width.toFloat(),
          height = placeable.height.toFloat(),
        )
      )
      return layout(
        width = placeable.width,
        height = placeable.height,
      ) {
        placeable.place(0,0)
      }
    }
  }
)


@Composable
fun DefaultToast(
  text: String = "Toast...",
  duration: Int = Toast.LENGTH_LONG,
) {
  Toast.makeText(LocalContext.current, text, duration).show()
}

fun DefaultToast(
  context: Context,
  text: String = "Toast...",
  duration: Int = Toast.LENGTH_LONG,
) {
  Toast.makeText(context, text, duration).show()
}