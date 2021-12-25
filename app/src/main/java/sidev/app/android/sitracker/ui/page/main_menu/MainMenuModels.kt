package sidev.app.android.sitracker.ui.page.main_menu

import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.nav.Route

//TODO: change `label` of each `NavItem` to string resource.
sealed class MainMenuNavItem(
  val route: String,
  @DrawableRes
  val iconSource: Int,
  val label: String,
  val onNavigate: (NavController, prevIndex: Int) -> Unit,
) {
  companion object {
    fun allItems(): List<MainMenuNavItem> = listOf(
      Home, TodaySchedule, Calendar,
    )
  }

  object Home: MainMenuNavItem(
    route = Route.HomePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Home",
    onNavigate = { navController, prevIndex ->
      Route.HomePage.go(
        navController, prevIndex,
      )
    },
  )
  object TodaySchedule: MainMenuNavItem(
    route = Route.TodaySchedulePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Schedule",
    onNavigate = { navController, prevIndex ->
      Route.TodaySchedulePage.go(
        navController, prevIndex,
      )
    },
  )
  object Calendar: MainMenuNavItem(
    route = Route.CalendarPage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Calendar",
    onNavigate = { navController, prevIndex ->
      Route.CalendarPage.go(
        navController, prevIndex,
      )
    },
  )
}