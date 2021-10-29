package sidev.app.android.sitracker.ui.page.main_menu.todays_schedule

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
fun TodaysSchedulePage(
  navController: NavController = rememberNavController(),
) {
  DefaultText(text = "TodaysSchedulePage")
}