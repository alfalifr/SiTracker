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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
@Preview
private fun TaskItem_preview() {
  TaskItem(
    icon = Icons.Rounded.Phone,
    color = Color.Green,
    title = "Calling someone",
    contentText = "5 hours",
    progress = 74 / 100f,
  )
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
  val containerHeight = 70.dp

  Row(
    modifier = modifier
      .size(
        height = containerHeight,
        width = Dp.Infinity,
      )
      .semantics(mergeDescendants = true) {
        contentDescription =
          "$title. $contentText."
        if(onClick != null) {
          customActions = listOf(
            CustomAccessibilityAction("Task Item Click") {
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
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconProgressionPic(
      icon = icon,
      mainColor = color,
      name = null,
    )
    Spacer(Modifier.size(width = 15.dp, height = 0.dp))

    //TODO: Place this column to `Layout` so that its max width won't overlap progress icon.
    Column(
      modifier = Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = title,
        fontSize = MaterialTheme.typography.body1.fontSize,
        modifier = Modifier.clearAndSetSemantics {  },
      )
      if(contentText != null) {
        Text(
          text = contentText,
          fontSize = MaterialTheme.typography.body2.fontSize,
          modifier = Modifier.clearAndSetSemantics {  },
        )
      }
    }

    if(progress != null) {
      Layout(
        content = {
          IconProgressionText(
            text = "${String.format("%.0f", progress * 100)} %",
            mainColor = color,
            progress = progress,
          )
        },
        measurePolicy = { measurables, constraints ->
          with(constraints) {
            layout(maxWidth, maxHeight) {
              val progressPlacable = measurables
                .first()
                .measure(constraints)
              val x = maxWidth - progressPlacable.width
              progressPlacable.place(x, 0)
            }
          }
        }
      )
    }
  }
}