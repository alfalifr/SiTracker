package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
      MainMenuBottomNavBar(
        navItems = MainMenuNavItem.allItems(),
        navController = mainMenuContentNavController,
      )
    }
  ) {
    println("MainMenuPage padding = $it")
    MainMenuNavGraph(
      modifier = Modifier.padding(it),
      parentNavController = navController,
      navController = mainMenuContentNavController,
    )
  }
}