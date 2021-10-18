package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import sidev.app.android.sitracker.util.getStartCenterAligned


@Composable
@Preview
private fun TaskItem_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    TaskItem(
      icon = Icons.Rounded.Phone,
      color = Color.Green,
      title = "Calling someone nahoi af afa af af  af af  af a agoaijgoiajg aijgiaifjaijfaofjoajfojafojaofoafjojafo aoifjo aofjoajfoajfafjafojaofoafjo ahfai aijf ajfiajf aoifiafjiajfiajfiajfijafijioajfijafi jaofj oajf",
      contentText = "5 hours",
      progress = 74 / 100f,
    )
    TaskItem(
      icon = Icons.Rounded.Phone,
      color = Color.Green,
      title = "Calling someone nahoi af afa af af  af af  af a agoaijgoiajg ",
      contentText = "5 hours",
    )
    TaskItem(
      icon = Icons.Rounded.Phone,
      color = Color.Green,
      title = "Calling someone ",
      contentText = "5 hours",
      progress = 34 / 100f,
    )
  }
}

@Composable
fun TaskItem(
  icon: ImageVector,
  color: Color,
  title: String,
  modifier: Modifier = Modifier,
  contentText: String? = null,
  progress: Float? = null,
  onClick: (() -> Unit)? = null,
) {
  val bgShape = RoundedCornerShape(15.dp)
  //val containerHeight = 70.dp

  Layout(
    modifier = modifier
      .semantics(mergeDescendants = true) {
        contentDescription =
          "$title. $contentText."
        if(onClick != null) {
          customActions = listOf(
            CustomAccessibilityAction("Task Item Click: $title") {
              onClick()
              true
            }
          )
        }
      }
      .background(
        color = MaterialTheme.colors.surface,
        shape = bgShape,
      )
      .shadow(
        elevation = 5.dp,
        shape = bgShape,
      )
      .padding(
        horizontal = 15.dp,
        vertical = 10.dp,
      ),
    content = {
      IconProgressionPic(
        icon = icon,
        mainColor = color,
        name = null,
      )

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = title,
          fontSize = MaterialTheme.typography.body1.fontSize,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.clearAndSetSemantics {  },
        )
        if(contentText != null) {
          Spacer(Modifier.height(10.dp))
          Text(
            text = contentText,
            fontSize = MaterialTheme.typography.body2.fontSize,
            modifier = Modifier.clearAndSetSemantics {  },
          )
        }
      }

      if(progress != null) {
        IconProgressionText(
          text = "${String.format("%.0f", progress * 100)} %",
          mainColor = color,
          progress = progress,
        )
      }
    },
  ) { measurables, constraints ->
    with(constraints) {
      // 1. Measure children
      val iconPlaceable = measurables[0].measure(constraints)

      val progressPlaceable = measurables
        .getOrNull(2)
        ?.measure(constraints)

      val spacerWidth = 15.dp.roundToPx()

      val textsMaxWidth = maxWidth -
        iconPlaceable.width -
        spacerWidth -
        (progressPlaceable?.width ?: 0) -
        spacerWidth

      val textsPlaceable = measurables[1].measure(
        Constraints(
          maxHeight = maxHeight,
          maxWidth = textsMaxWidth,
        )
      )

      // 2. Draw layout and place each child inside
      val parentHeight = maxOf(
        iconPlaceable.height,
        progressPlaceable?.height ?: 0,
        textsPlaceable.height,
      )

      layout(
        width = maxWidth,
        height = parentHeight,
      ) {
        iconPlaceable.apply {
          place(
            0, getStartCenterAligned(parentHeight, height)
          )
        }
        progressPlaceable?.apply {
          place(
            maxWidth - width,
            getStartCenterAligned(parentHeight, height)
          )
        }
        textsPlaceable.apply {
          place(
            iconPlaceable.width + spacerWidth,
            getStartCenterAligned(parentHeight, height)
          )
        }
      }
    }
  }
}