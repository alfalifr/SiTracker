package sidev.app.android.sitracker.ui.page.main_menu

import androidx.annotation.DrawableRes
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.nav.Routes

//TODO: change `label` of each `NavItem` to string resource.
sealed class NavItem(
  val route: String,
  @DrawableRes
  val iconSource: Int,
  val label: String,
) {
  companion object {
    fun allItems(): List<NavItem> = listOf(
      Home, TodaysSchedule, Calendar,
    )
  }

  object Home: NavItem(
    route = Routes.HomePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Home"
  )
  object TodaysSchedule: NavItem(
    route = Routes.TodaysSchedulePage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Schedule"
  )
  object Calendar: NavItem(
    route = Routes.CalendarPage.completeRoute,
    iconSource = R.drawable.ic_bookmark,
    label = "Calendar"
  )
}