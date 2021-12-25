package sidev.app.android.sitracker.ui.page.main_menu.calendar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.nav.MainMenuItemNavData
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout

@Composable
fun CalendarPage(
  navData: MainMenuItemNavData,
  //navController: NavController = rememberNavController(),
) {
  MainMenuItemLayout(
    title = "Your Calendar",
    navData = navData,
  ) {
    mainMenuItem {
      DefaultText(text = "CalendarPage")
    }
  }
}