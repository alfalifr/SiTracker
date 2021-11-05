package sidev.app.android.sitracker.ui.page.main_menu.calendar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.component.DefaultText

@Composable
fun CalendarPage(
  navController: NavController = rememberNavController(),
) {
  DefaultText(text = "CalendarPage")
}