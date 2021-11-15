package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.model.PrefixPostfixPlaceables
import sidev.app.android.sitracker.util.getStartCenterAligned


@Composable
fun PrefixPostfixLayout(
  modifier: Modifier = Modifier,
  spaceWidth: Dp = 15.dp,
  prefix: @Composable (() -> Unit)? = null,
  postfix: @Composable (() -> Unit)? = null,
  //verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
  content: @Composable (() -> Unit)? = null,
) {
  if(content == null && prefix == null && postfix == null) {
    return
  }
  Layout(
    modifier = modifier,
    content = {
      prefix?.invoke()
      content?.invoke()
      postfix?.invoke()
    },
  ) { measurables, constraints ->
    with(constraints) {
      /*
      val prefixPlaceable =
        if(prefix != null) measurables[0].measure(constraints)
        else null

      val postfixPlaceable =
        if(postfix != null) measurables[2].measure(constraints)
        else null

      val spaceWidth = 15.dp.roundToPx()

      val prefixWidth = prefixPlaceable?.width?.plus(spaceWidth) ?: 0
      val postfixWidth = postfixPlaceable?.width?.plus(spaceWidth) ?: 0

      val contentMaxWidth = maxWidth -
        prefixWidth -
        postfixWidth

      val contentPlaceable = if(content != null) {
        (if(prefix != null) measurables[1]
        else measurables[0])
          .measure(
            Constraints(
              maxHeight = maxHeight,
              maxWidth = contentMaxWidth,
            )
          )
      } else null

      val parentHeight = maxOf(
        prefixPlaceable?.height ?: 0,
        postfixPlaceable?.height ?: 0,
        contentPlaceable?.height ?: 0,
      )

       */

      val prefixMeasurable =
        if(prefix != null) measurables[0]
        else null

      val postfixMeasurable =
        if(postfix != null) measurables[2]
        else null

      val contentMeasurable = if(content != null) {
        if(prefix != null) measurables[1]
        else measurables[0]
      } else null

      val (
        prefixPlaceable,
        postfixPlaceable,
        contentPlaceable,
      ) = measurePrefixPostfixComponents(
        prefixMeasurable = prefixMeasurable,
        postfixMeasurable = postfixMeasurable,
        contentMeasurable = contentMeasurable,
        constraints = constraints,
        spaceWidth = spaceWidth,
      ) ?: return@with layout(
        0, 0,
      ) {} // Impossibly returns null because this `PrefixPostfixLayout` method return di the beginning when all composable null.

      val parentHeight = maxOf(
        prefixPlaceable?.height ?: 0,
        postfixPlaceable?.height ?: 0,
        contentPlaceable?.height ?: 0,
      )

      layout(
        width = maxWidth,
        height = parentHeight,
      ) {
        val prefixWidth = prefixPlaceable?.run {
          place(
            x = 0,
            y = getStartCenterAligned(
              parentLen = parentHeight,
              childLen = height,
            ),
          )
          width
        } ?: 0

        postfixPlaceable?.apply {
          place(
            x = maxWidth - width,
            y = getStartCenterAligned(
              parentLen = parentHeight,
              childLen = height,
            ),
          )
        }

        contentPlaceable?.apply {
          place(
            x = prefixWidth + spaceWidth.roundToPx(),
            y = getStartCenterAligned(
              parentLen = parentHeight,
              childLen = height,
            ),
          )
        }
      }
    }
  }
}


//@Composable
fun MeasureScope.measurePrefixPostfixComponents(
  prefixMeasurable: Measurable?,
  postfixMeasurable: Measurable?,
  contentMeasurable: Measurable?,
  constraints: Constraints,
  spaceWidth: Dp = 15.dp,
): PrefixPostfixPlaceables? = with(constraints) {
  if(
    contentMeasurable == null
    && prefixMeasurable == null
    && postfixMeasurable == null
  ) {
    return null
  }

  val prefixPlaceable = prefixMeasurable?.measure(constraints)
  val postfixPlaceable = postfixMeasurable?.measure(constraints)

  val spaceWidthInPx = spaceWidth.roundToPx()

  val prefixWidth = prefixPlaceable?.width?.plus(spaceWidthInPx) ?: 0
  val postfixWidth = postfixPlaceable?.width?.plus(spaceWidthInPx) ?: 0

  val contentMaxWidth = maxWidth -
    prefixWidth -
    postfixWidth

  val contentPlaceable = contentMeasurable?.measure(
    Constraints(
      maxHeight = maxHeight,
      maxWidth = contentMaxWidth,
    )
  )

  PrefixPostfixPlaceables(
    prefixPlaceable, postfixPlaceable, contentPlaceable,
  )
}