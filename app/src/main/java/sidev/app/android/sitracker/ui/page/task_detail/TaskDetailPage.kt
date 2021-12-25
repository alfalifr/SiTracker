@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.page.task_detail

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.*
import sidev.app.android.sitracker.ui.layout.MainScaffold
import sidev.app.android.sitracker.ui.model.TaskItemDataUi
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.ui.theme.TransOppositeDarkColor3
import sidev.app.android.sitracker.util.*
import sidev.app.android.sitracker.util.model.Direction

@Composable
fun TaskDetailPage(
  taskId: Int,
  navController: NavController = rememberNavController(),
  viewModel: TaskDetailViewModel = defaultViewModel(),
) {
  val isDark = isSystemInDarkTheme()
  val taskPanelData by viewModel.taskPanelData
    .collectAsState(initial = null)

  val schedulePanelData by viewModel.schedulePanelData
    .collectAsState(initial = null)

  val preferredDays by viewModel.preferredDays
    .collectAsState(initial = null)


  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.loadData(
      taskId = taskId,
      isDark = isDark,
    )
  }

  MainScaffold {
    animatedSliding(Direction.LEFT) {
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
          //modifier = Modifier.padding(Const.stdSpacerDp),
        ) {
          DefaultToast(ctx)
        }
      }
    }

    item { Spacer(Modifier.height(15.dp)) }

    animatedSliding(Direction.LEFT) {
      LargeSurface {
        LoadingPlaceholder(key = schedulePanelData) {
          ScheduleListPanel(
            data = it,
            navController = navController
          )
        }
      }
    }

    item { Spacer(Modifier.height(15.dp)) }

    animatedSliding(Direction.LEFT) {
      PreferredDayPanel(data = preferredDays)
    }
  }
}

@Composable
private fun TaskPanel(
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
        tint = OppositeDark,
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
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = Const.stdSpacerDp),
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

      println("maxContentHeight = $maxContentHeight spacerPx = $spacerPx Int.MAX_VALUE = ${Int.MAX_VALUE} constraints.maxHeight = ${constraints.maxHeight}")
      val contentPlaceable = measurables[3].measure(
        try {
          constraints.copy(
            maxHeight = maxContentHeight,
          )
        } catch(e: IllegalArgumentException) {
          constraints
        }
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
            y = taskIconPlaceable.height / 2 + spacerPx,
          )
        }
      }
    },
  )
}

@Composable
private fun ScheduleListPanel(
  data: TaskSchedulePanelData,
  navController: NavController,
) {
  Column(Modifier
    .fillMaxWidth()
    .clickable {
      Route.ScheduleListPage.go(
        navController,
        data.taskId,
      )
    }
  ) {
    Text(
      text = data.header,
      style = MaterialTheme.typography.h6,
    )
    Spacer(Modifier.height(15.dp))

    /*
    fun LazyListScope.layoutItems() {
      items(min(data.items.size, 3)) {
        val bulletColor = MaterialTheme.colors.secondaryVariant
        val txtStyle = MaterialTheme.typography.body2
        Text(
          text = data.items[it],
          style = txtStyle,
          modifier = Modifier.drawWithContent {
            val radius = txtStyle.fontSize.toDp().toPx() / 2
            drawCircle(
              bulletColor,
              radius = radius,
              center = center.copy(x = radius),
            )
            translate(left = radius + 15.dp.toPx()) {
              this@drawWithContent.drawContent()
            }
          },
        )
      }
    }
     */

    Column(
      modifier = Modifier.padding(start = 15.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      val bulletColor = TransOppositeDarkColor3 //MaterialTheme.colors.secondaryVariant
      val txtStyle = MaterialTheme.typography.body2
      for(item in data.items) {
        Text(
          text = item,
          style = txtStyle,
          modifier = Modifier.drawWithContent {
            val radius = txtStyle.fontSize.toDp().toPx() / 2
            drawCircle(
              bulletColor,
              radius = radius,
              center = center.copy(x = radius),
            )
            translate(left = radius + 15.dp.toPx()) {
              this@drawWithContent.drawContent()
            }
          },
        )
      }
    }

    data.seeOtherText?.also {
      Box(Modifier.fillMaxWidth()
        .padding(top = 10.dp)
      ) {
        Text(
          text = it,
          style = MaterialTheme.typography.body2,
          color = TransOppositeDarkColor3,
          modifier = Modifier.align(Alignment.Center),
        )
      }
    }
    /*
    listScope?.layoutItems() ?: run {
      LazyColumn {
        layoutItems()
      }
    }
     */
  }
}