package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.nav.MainMenuNavGraph


@Composable
fun MainMenuPage(
  navController: NavController = rememberNavController(),
) {
  val mainMenuContentNavController = rememberNavController()
  Scaffold(
    bottomBar = {
      BottomNavBar(
        navItems = NavItem.allItems(),
        navController = mainMenuContentNavController,
      )
    }
  ) {
    MainMenuNavGraph(
      parentNavController = navController,
      navController = mainMenuContentNavController,
    )
  }
}