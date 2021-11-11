package sidev.app.android.sitracker.ui.page.task_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.*
import sidev.app.android.sitracker.ui.model.TaskItemDataUi
import sidev.app.android.sitracker.util.*

@Composable
fun TaskDetailPage(
  taskId: Int,
  navController: NavController = rememberNavController(),
  viewModel: TaskDetailViewModel = defaultViewModel(),
) {
  val isDark = isSystemInDarkTheme()
  val taskPanelData by viewModel.taskPanelData.collectAsState(
    initial = null,
  )
  LaunchedEffect(key1 = Unit) {
    viewModel.loadData(
      taskId = taskId,
      isDark = isDark,
    )
  }

  Placeholder(
    key = taskPanelData,
    placeholder = {
      LargeSurface(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
      ) {
        DefaultLoading(
          text = null,
        )
      }
    },
  ) {
    val ctx = LocalContext.current
    TaskPanel(
      data = it,
      modifier = Modifier.padding(Const.stdSpacerDp),
    ) {
      DefaultToast(ctx)
    }
  }
}

@Composable
fun TaskPanel(
  data: TaskItemDataUi,
  modifier: Modifier = Modifier,
  onEditBtnClick: ((taskId: Int) -> Unit)? = null,
) {
  Layout(
    modifier = modifier,
    content = {
      IconProgressionPic(
        modifier = Modifier.fillMaxSize(),
        icon = painterResource(id = data.icon.resId),
        mainColor = Color(data.icon.color),
        name = Texts.iconOf(data.name),
      ) //0

      LargeSurface(modifier = Modifier.fillMaxSize()) //1

      Icon(
        painter = painterResource(id = R.drawable.ic_edit),
        contentDescription = Texts.editItem(data.name),
        modifier = Modifier
          .size(Const.iconSizeStdDp)
          .let {
            if(onEditBtnClick == null) it
            else it.clickable { onEditBtnClick(data.taskId) }
          },
      ) //2

      //3
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Const.stdSpacerDp),
        modifier = Modifier.padding(
          horizontal = Const.stdSpacerDp,
        ),
      ) {
        Text(
          text = data.name,
          style = MaterialTheme.typography.h6,
          textAlign = TextAlign.Center,
        )
        IconWithText(
          icon = painterResource(id = R.drawable.ic_bookmark),
          text = data.priorityText,
        )
        Text(
          text = data.desc,
          style = MaterialTheme.typography.body2,
          textAlign = TextAlign.Start,
        )
      }
    },
    measurePolicy = { measurables, constraints ->
      val spacerPx = Const.stdSpacerDp.roundToPx()

      //Task Icon
      val maxTaskIconSize = constraints.maxSquareSideLen / 4
      val taskIconPlaceable = measurables[0].measure(
        constraints.copy(
          maxWidth = maxTaskIconSize,
          maxHeight = maxTaskIconSize,
        )
      )

      //Content
      val maxContentHeight = constraints.maxHeight -
        taskIconPlaceable.height -
        spacerPx

      val contentPlaceable = measurables[3].measure(
        constraints.copy(
          maxHeight = maxContentHeight,
        )
      )

      //Edit Btn
      val editBtnPlaceable = measurables[2].measure(constraints)

      //Surface
      val surfaceHeight = taskIconPlaceable.height / 2 +
        spacerPx +
        contentPlaceable.height +
        spacerPx

      val surfacePlaceable = measurables[1].measure(
        constraints.copy(
          maxHeight = surfaceHeight,
        )
      )

      layout(
        height = surfaceHeight + taskIconPlaceable.height / 2,
        width = surfacePlaceable.width,
      ) {
        surfacePlaceable.place(
          x = 0,
          y = taskIconPlaceable.height / 2,
        )
        taskIconPlaceable.apply {
          place(
            x = getStartCenterAligned(
              parentLen = surfacePlaceable.width,
              childLen = width,
            ),
            y = 0,
          )
        }
        contentPlaceable.place(
          x = 0,
          y = taskIconPlaceable.height + spacerPx,
        )
        editBtnPlaceable.apply {
          place(
            x = surfacePlaceable.width - width - spacerPx,
            y = taskIconPlaceable.height / 2 - spacerPx,
          )
        }
      }
    },
  )
}