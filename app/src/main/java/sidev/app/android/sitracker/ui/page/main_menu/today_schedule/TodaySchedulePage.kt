package sidev.app.android.sitracker.ui.page.main_menu.today_schedule

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.component.DefaultText


@Composable
fun TodaySchedulePage(
  navController: NavController = rememberNavController(),
) {
  DefaultText(text = "TodaySchedulePage")
}