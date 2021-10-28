package sidev.app.android.sitracker.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage

@Composable
fun Navigation() {
  val navController = rememberNavController()
  //NavHost(navController = navController, startDestination =) {}

  NavHost(
    navController = navController,
    startDestination = Routes.HomePage.completeRoute,
  ) {
    //1.
    composable(
      route = Routes.HomePage.completeRoute,
    ) {
      HomePage()
    }
  }
}