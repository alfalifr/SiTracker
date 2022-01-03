package sidev.app.android.sitracker.ui.page.add_edit_task_schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.di.DiCenter
import sidev.app.android.sitracker.ui.nav.NavGraphComp
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.util.defaultViewModel
import sidev.app.android.sitracker.util.hasMask


@Composable
fun AddEditTaskSchedulePage(
  scheduleId: Int?,
  pagesMask: Int,
  //TODO add param for distinguish edit or view
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.setPagesMask(pagesMask)
  }
  val childNavController = rememberNavController()
  /*
  AddEditTaskScheduleNavGraph(
    navController = childNavController,
    parentNavController = navController,
  )
   */

  val routes = mutableListOf<Route>()

  /*
  val pages = viewModel.pageMask
    .collectAsState(initial = null)
    .value
   */

  println("AddEditTaskSchedulePage pagesMask = $pagesMask")
  println("AddEditTaskScheduleViewModel.SCHEDULE_INFO_MASK = ${AddEditTaskScheduleViewModel.SCHEDULE_INFO_MASK}")

  if(pagesMask.hasMask(AddEditTaskScheduleViewModel.TASK_INFO_MASK)) {
    routes += Route.AddEditTaskInfoPage
  }
  if(pagesMask.hasMask(AddEditTaskScheduleViewModel.SCHEDULE_INFO_MASK)) {
    routes += Route.AddEditScheduleInfoPage
  }

  if(routes.isEmpty()) {
    return //doesn't need to draw anymore composable.
  }

  NavGraphComp(
    navRoutes = routes,
    navController = childNavController,
    parentNavController = navController,
    startDestination = routes.first().completeRoute,
    viewModelFactory = AddEditTaskScheduleParentViewModelFactory(
      viewModel,
    )
  )
}