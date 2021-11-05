package sidev.app.android.sitracker.ui.page.main_menu.today_schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.component.LoadingPlaceholder
import sidev.app.android.sitracker.ui.component.TaskGroup
import sidev.app.android.sitracker.util.defaultViewModel
import java.util.*


@Composable
fun TodaySchedulePage(
  navController: NavController = rememberNavController(),
  viewModel: TodayScheduleViewModel = defaultViewModel(),
) {
  //DefaultText(text = "TodaySchedulePage")
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.refreshList()
  }

  MainList(
    navController = navController,
    viewModel = viewModel,
  )
}

@Composable
private fun MainList(
  navController: NavController = rememberNavController(),
  viewModel: TodayScheduleViewModel = defaultViewModel(),
) {
  val taskGroups = viewModel.taskItemScheduleGroupsUi
    .collectAsState(initial = null).value

  println("taskGroups = $taskGroups")

  LoadingPlaceholder(
    key = taskGroups,
    loadingModifier = Modifier.fillMaxSize(),
  ) { taskGroups ->
    /*
    LazyColumn {
      items(taskGroups.size) { i ->
        val data = taskGroups[i]
        TaskGroup(
          header = data.header,
          taskData = data.schedules,
          disableScroll = true,
          modifier = Modifier.padding(vertical = 15.dp),
        )
      }
    }
    // */
    ///*
    Column {
      for(data in taskGroups) {
        TaskGroup(
          header = data.header,
          taskData = data.schedules,
          disableScroll = true,
          modifier = Modifier.padding(vertical = 15.dp),
        )
      }
    }
    // */
  }
}