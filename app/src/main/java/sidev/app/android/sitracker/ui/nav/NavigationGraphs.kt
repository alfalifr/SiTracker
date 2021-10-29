package sidev.app.android.sitracker.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraphComp(
  navRoutes: List<Routes>,
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
        route.composable(
          this,
          NavComposableData(
            navController = navController,
            parentNavController = parentNavController,
            navBackStackEntry = navBackStackEntry,
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
    navRoutes = Routes.getMainMenuContentRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Routes.HomePage.completeRoute,
  )
}

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController(),
  parentNavController: NavController? = null,
) {
  NavGraphComp(
    navRoutes = Routes.getAppRoutes(),
    navController = navController,
    parentNavController = parentNavController,
    startDestination = Routes.MainMenuPage.completeRoute,
  )
}