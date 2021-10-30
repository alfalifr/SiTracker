package sidev.app.android.sitracker.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuPage
import sidev.app.android.sitracker.ui.page.main_menu.calendar.CalendarPage
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage
import sidev.app.android.sitracker.ui.page.main_menu.todays_schedule.TodaysSchedulePage

sealed class Routes(
  val route: String,
  val composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
) {

  sealed class IndexedRoutes(
    route: String,
    val index: Int,
    composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
  ): Routes(
    route = route,
    composable = composable,
  ) {
    
  }

  // ===============
  companion object {
    fun getAppRoutes(): List<Routes> = listOf(
      MainMenuPage,
    )
    fun getMainMenuContentRoutes(): List<IndexedRoutes> = listOf(
      HomePage, TodaysSchedulePage, CalendarPage,
    )
  }

  open val completeRoute: String
    get() = route

  protected open fun go(navController: NavController) {
    navController.navigate(completeRoute)
  }
  // ========Child==========

  // ------- Main Menu -------
  object MainMenuPage: Routes("MainMenu", {
    MainMenuPage(navController = it.navController)
  })

  object HomePage: IndexedRoutes("HomePage", 0, {
    MainMenuItemLayout(title = "What to do?") {
      HomePage(navController = it.navController) {
        TODO("Implement `Routes.HomePage.onItemClick`")
      }
    }
  })
  object TodaysSchedulePage: IndexedRoutes("TodaysSchedulePage", 1, {
    MainMenuItemLayout(title = "Today's schedule") {
      TodaysSchedulePage(navController = it.navController)
    }
  })
  object CalendarPage: IndexedRoutes("CalendarPage", 2, {
    MainMenuItemLayout(title = "Your calendar") {
      CalendarPage(navController = it.navController)
    }
  })


}