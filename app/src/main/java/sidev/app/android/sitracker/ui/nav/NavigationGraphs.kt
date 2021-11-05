package sidev.app.android.sitracker.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraphComp(
  navRoutes: List<Route>,
  startDestination: String,
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
  ) {
    navRoutes.forEach { route ->
      composable(
        route = route.completeRoute,
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
          )
        )
      }
    }
  }
}



@Composable
fun MainMenuNavGraph(
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavGraphComp(
    navRoutes = Route.getMainMenuContentRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Route.HomePage.completeRoute,
  )
}

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavGraphComp(
    navRoutes = Route.getAppRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Route.MainMenuPage.completeRoute,
  )
}