package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.model.IconProgressionPicUiData
import sidev.app.android.sitracker.ui.model.IconProgressionFloatUiData
import sidev.app.android.sitracker.ui.model.IconProgressionUiData
import sidev.app.android.sitracker.ui.model.ScheduleItemDataUi
import sidev.app.android.sitracker.util.Color
import sidev.app.android.sitracker.util.DataMapper.toUiData
import sidev.app.android.sitracker.util.Texts.formatProgress


@Composable
@Preview
private fun TaskItem_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    TaskItem(
      icon = rememberVectorPainter(Icons.Rounded.Phone),
      color = Color.Green,
      title = "Calling someone nahoi af afa af af  af af  af a agoaijgoiajg aijgiaifjaijfaofjoajfojafojaofoafjojafo aoifjo aofjoajfoajfafjafojaofoafjo ahfai aijf ajfiajf aoifiafjiajfiajfiajfijafijioajfijafi jaofj oajf",
      contentText = "5 hours",
      postfixIconData = IconProgressionFloatUiData(
        color = Color.Blue,
        progress = 74 / 100f,
      ),
    )
    TaskItem(
      icon = rememberVectorPainter(Icons.Rounded.Phone),
      color = Color.Green,
      title = "Calling someone nahoi af afa af af  af af  af a agoaijgoiajg ",
      contentText = "5 hours",
    )
    TaskItem(
      icon = rememberVectorPainter(Icons.Rounded.Phone),
      color = Color.Green,
      title = "Calling someone ",
      contentText = "5 hours",
      postfixIconData = IconProgressionFloatUiData(
        color = Color.Red,
        progress = 34 / 100f,
      ),
      isPostfixIconDataColorSameAsMainColor = false,
    )
  }
}

@Composable
fun TaskItem(
  icon: Painter,
  color: Color,
  title: String,
  modifier: Modifier = Modifier,
  contentText: String? = null,
  postfixIconData: IconProgressionUiData? = null,
  isPostfixIconDataColorSameAsMainColor: Boolean = true,
  onClick: (() -> Unit)? = null,
) {
  val bgShape = MaterialTheme.shapes.medium //RoundedCornerShape(15.dp)
  val spaceWidth = 15.dp
  //val containerHeight = 70.dp

  PrefixPostfixLayout(
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
    spaceWidth = spaceWidth,
    prefix = {
      IconProgressionPic(
        icon = icon,
        mainColor = color,
        name = null,
      )
    },
    postfix = if(postfixIconData != null) {
      {
        var iconPainter: Painter? = null
        val text = postfixIconData.progress?.let {
          formatProgress(it)
        }

        if(postfixIconData is IconProgressionPicUiData) {
          iconPainter = postfixIconData.image
        }
        IconProgressionAdapt(
          icon = iconPainter,
          text = text,
          mainColor =
            if(isPostfixIconDataColorSameAsMainColor) color
            else postfixIconData.color,
          progress = postfixIconData.progress,
        )
      }
    } else null
  ) {
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
  }
/*
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

      if(postfixIconData != null) {
        var iconPainter: Painter? = null
        val text = postfixIconData.progress?.let {
          formatProgress(it)
        }

        if(postfixIconData is IconProgressionPicUiData) {
          iconPainter = postfixIconData.image
        }
        IconProgressionAdapt(
          icon = iconPainter,
          text = text,
          mainColor =
            if(isPostfixIconDataColorSameAsMainColor) color
            else postfixIconData.color,
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
 */
}

/*
@Composable
@Preview
fun TaskGroup_preview() {
  TaskGroup(
    header = "header bro",
  )
}
 */

@Composable
fun TaskGroup(
  header: String,
  taskData: List<ScheduleItemDataUi>,
  modifier: Modifier = Modifier,
  itemModifier: (ScheduleItemDataUi) -> Modifier = { Modifier },
  itemContentText: (ScheduleItemDataUi) -> String? = { it.desc },
  //isPostfixIconDataColorSameAsMainColor: (TaskCompData) -> Boolean = { true },
  disableScroll: Boolean = true,
  itemOnClick: ((ScheduleItemDataUi) -> Unit)? = null,
) {
  println("TaskGroup AWAL ====")
  @Composable
  fun InnerTaskItem(task: ScheduleItemDataUi) {
    TaskItem(
      icon = painterResource(id = task.icon.resId),
      color = Color(task.icon.color),
      title = task.title,
      modifier = itemModifier(task),
      contentText = itemContentText(task),
      postfixIconData = task.postfixIconData?.toUiData(),
      isPostfixIconDataColorSameAsMainColor = task.isPostfixIconDataColorSameAsMainColor,
      onClick = if(itemOnClick != null) {
        { itemOnClick(task) }
      } else null,
    )
  }
  val itemSpace = 10.dp

  println("TaskGroup disableScroll = $disableScroll")

  if(disableScroll) {
    Layout(
      modifier = modifier,
      content = {
        Text(
          text = header,
          style = MaterialTheme.typography.body2,
          fontWeight = FontWeight.Bold,
        )
        //Spacer(Modifier.height(10.dp))

        for(task in taskData) {
          InnerTaskItem(task = task)
        }
      },
      measurePolicy = { measurables, constraints ->
        //val measurablesItr = measurables.iterator()

        //val textPlaceable = measurablesItr.next().measure(constraints)

        val itemSpaceInPx = itemSpace.roundToPx()

        val placeables = measurables.map {
          it.measure(constraints)
        }
        layout(
          width = constraints.maxWidth,
          height = placeables.fold(0) { acc, e ->
            acc + e.height + itemSpaceInPx
          }.let {
            // to eliminate trailing space.
            if(it > itemSpaceInPx) it - itemSpaceInPx
            else it
          }
        ) {
          var accHeight = 0
          for(p in placeables) {
            with(p) {
              place(
                x = 0,
                y = accHeight.also {
                  accHeight += height + itemSpaceInPx
                },
              )
            }
          }
        }
      },
    )
  } else {
    LazyColumn {
      items(taskData.size) { i ->
        InnerTaskItem(task = taskData[i])
      }
    }
  }
}