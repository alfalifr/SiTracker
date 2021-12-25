package sidev.app.android.sitracker.ui.page.add_edit_task_schedule

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.nav.AddEditTaskScheduleNavGraph
import sidev.app.android.sitracker.util.defaultViewModel


@Composable
fun AddEditTaskSchedulePage(
  scheduleId: Int?,
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {
  val childNavController = rememberNavController()
  AddEditTaskScheduleNavGraph(
    navController = childNavController,
    parentNavController = navController,
  )
}