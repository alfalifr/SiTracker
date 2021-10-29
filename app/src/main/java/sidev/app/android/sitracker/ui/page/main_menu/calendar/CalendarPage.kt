package sidev.app.android.sitracker.ui.page.main_menu.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.nav.Routes

@Composable
fun CalendarPage(
  navController: NavController = rememberNavController(),
) {
  DefaultText(text = "CalendarPage")
}