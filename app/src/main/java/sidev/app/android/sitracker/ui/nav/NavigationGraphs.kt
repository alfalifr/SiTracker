package sidev.app.android.sitracker.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.util.defaultViewModel

@Composable
fun NavGraphComp(
  navRoutes: List<Route>,
  startDestination: String,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
  viewModelFactory: ViewModelProvider.Factory? = null,
) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = startDestination,
  ) {
    navRoutes.forEach { route ->
      composable(
        route = route.completeRoute,
        arguments = route.arguments,
      ) { navBackStackEntry ->
        /*
        loge(
          """NavGraphCompNavGraphComp: 
            |navBackStackEntry = $navBackStackEntry
            |navController.currentBackStackEntry = ${navController.currentBackStackEntry}
            |isSame = ${navBackStackEntry == navController.currentBackStackEntry}
            |""".trimMargin()
        )
        loge(
          """NavGraphCompNavGraphComp: 
            |navBackStackEntry.destination.route = ${navBackStackEntry.destination.route}
            |this.route = ${this.route}
            |this.label = ${this.label}
            |navController.previousBackStackEntry?.destination?.route = ${navController.previousBackStackEntry?.destination?.route}
            |navController.currentBackStackEntry?.destination?.route = ${navController.currentBackStackEntry?.destination?.route}
            |isSame = ${navBackStackEntry == navController.currentBackStackEntry}
            |""".trimMargin()
        )
         */
        route.composable(
          this,
          ComposableNavData(
            navController = navController,
            parentNavController = parentNavController,
            navBackStackEntry = navBackStackEntry,
            prevNavBackStackEntry = navController.previousBackStackEntry,
            viewModelFactory = viewModelFactory,
          )
        )
      }
    }
  }
}


@Composable
fun AppNavGraph(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavGraphComp(
    modifier = modifier,
    navRoutes = Route.getAppRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Route.MainMenuPage.completeRoute,
  )
}

@Composable
fun MainMenuNavGraph(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavGraphComp(
    modifier = modifier,
    navRoutes = Route.getMainMenuContentRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Route.HomePage.completeRoute,
  )
}

@Composable
fun AddEditTaskScheduleNavGraph(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {
  NavGraphComp(
    modifier = modifier,
    navRoutes = Route.getAddEditTaskScheduleRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Route.AddEditTaskInfoPage.completeRoute,
  )
}

