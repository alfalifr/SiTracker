package sidev.app.android.sitracker.ui.page.main_menu

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.nav.Route

//TODO: change `label` of each `NavItem` to string resource.
sealed class NavItem(
  val route: String,
  @DrawableRes
  val iconSource: Int,
  val label: String,
) {
  companion object {
    fun allItems(): List<NavItem> = listOf(
      Home, TodaySchedule, Calendar,
    )
  }

  object Home: NavItem(
    route = Route.HomePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Home"
  )
  object TodaySchedule: NavItem(
    route = Route.TodaySchedulePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Schedule"
  )
  object Calendar: NavItem(
    route = Route.CalendarPage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Calendar"
  )
}