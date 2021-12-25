package sidev.app.android.sitracker.ui.page.main_menu.today_schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.ui.component.LoadingPlaceholder
import sidev.app.android.sitracker.ui.component.TaskGroup
import sidev.app.android.sitracker.ui.layout.MainMenuContentScope
import sidev.app.android.sitracker.ui.model.ScheduleItemGroupUi
import sidev.app.android.sitracker.ui.nav.MainMenuItemNavData
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.util.DefaultToast
import sidev.app.android.sitracker.util.defaultViewModel


@Composable
fun TodaySchedulePage(
  navData: MainMenuItemNavData,
  //navController: NavController = rememberNavController(),
  viewModel: TodayScheduleViewModel = defaultViewModel(),
  //mainScaffoldScope: MainMenuContentScope? = null,
  onItemClick: ((scheduleId: Int) -> Unit)? = null,
) {
  //DefaultText(text = "TodaySchedulePage")
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.refreshList()
  }

  val taskGroups = viewModel.taskItemScheduleGroupsUi
    .collectAsState(initial = null).value

  println("taskGroups = $taskGroups")

  Box(Modifier.fillMaxSize()) {
    MainMenuItemLayout(
      modifier = Modifier.fillMaxSize(),
      title = "Today's schedule",
      navData = navData,
    ) {
      if(taskGroups?.isNotEmpty() != true) {
        mainMenuItem {
          LoadingPlaceholder(
            key = taskGroups,
            loadingModifier = Modifier.fillParentMaxSize(),
          ) { taskGroups ->
            if(taskGroups.isEmpty()) {
              EmptyMainList()
            } else {
              // Impossibly invoked
            }
          }
        }
      } else {
        mainMenuItems(taskGroups.size) { i ->
          TaskGroupItem(
            data = taskGroups[i],
            topMargin = (if(i > 0) 15 else 0).dp,
            onItemClick = onItemClick,
          )
        }
      }
    }

    val ctx = LocalContext.current
    FloatingActionButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(15.dp),
      onClick = { DefaultToast(ctx, "FAB clicked") },
    ) {
      Icon(
        rememberVectorPainter(image = Icons.Default.Add),
        "Click", //TODO: localize
      )
    }
  }
}

@Composable
private fun TaskGroupItem(
  data: ScheduleItemGroupUi,
  topMargin: Dp = 0.dp,
  onItemClick: ((scheduleId: Int) -> Unit)? = null,
) {
  TaskGroup(
    header = data.header,
    taskData = data.schedules,
    disableScroll = true,
    modifier = Modifier.padding(top = topMargin),
    itemModifier = { itemData ->
      Modifier.let {
        if(onItemClick == null) it
        else it.clickable { onItemClick(itemData.id) }
      }
    },
  )
}

//@Composable
private fun MainMenuContentScope.MainList(
  //navController: NavController,
  //viewModel: TodayScheduleViewModel,
  taskGroups: List<ScheduleItemGroupUi>,
  //mainScaffoldScope: MainMenuContentScope,
  onItemClick: ((scheduleId: Int) -> Unit)? = null,
) {
  /*
  val taskGroups = viewModel.taskItemScheduleGroupsUi
    .collectAsState(initial = null).value
   */


  mainMenuItems(taskGroups.size) { i ->
    TaskGroupItem(
      data = taskGroups[i],
      topMargin = (if(i > 0) 15 else 0).dp,
    )
    println("TodaySchedulePage MainList mainMenuItems i= $i")
  }
/*
  LoadingPlaceholder(
    key = taskGroups,
    loadingModifier = Modifier.fillMaxSize(),
  ) { taskGroups ->
    ///*
    if(taskGroups.isEmpty()) {
      EmptyMainList()
    } else {
    }
/*
    Box {
      if(mainScaffoldScope == null) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          for(data in taskGroups) {
            InnerItem(data)
          }
        }
      } else {

        mainScaffoldScope.apply {
        }
      }
    }
 */
    // */
  }
 */
}

@Composable
private fun EmptyMainList() {
  Column(
    Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      "Hooray! No shedules today :)",
      style = MaterialTheme.typography.h5,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
    )
  }
}