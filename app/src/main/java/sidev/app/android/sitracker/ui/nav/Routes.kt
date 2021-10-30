@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.nav

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import sidev.app.android.sitracker.ui.component.MainMenuContentTransition
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuPage
import sidev.app.android.sitracker.ui.page.main_menu.calendar.CalendarPage
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage
import sidev.app.android.sitracker.ui.page.main_menu.todays_schedule.TodaysSchedulePage
import sidev.app.android.sitracker.util.model.Direction

sealed class Routes(
  val route: String,
  open val composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
) {

  sealed class IndexedRoutes(
    route: String,
    val index: Int,
    composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
  ): Routes(
    route = route,
    composable = composable,
  )

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

  object HomePage: MainMenuItemRoute(
    "HomePage",
    0,
    "What to do?", {
      HomePage(navController = it.navController) {
        TODO("Implement `Routes.HomePage.onItemClick`")
      }
  })
  object TodaysSchedulePage: MainMenuItemRoute(
    "TodaysSchedulePage",
    1,
    "Today's schedule", {
      TodaysSchedulePage(navController = it.navController)
  })
  object CalendarPage: MainMenuItemRoute(
    "CalendarPage",
    2,
    "Your calendar", {
      CalendarPage(navController = it.navController)
  })


}



sealed class MainMenuItemRoute(
  route: String,
  index: Int,
  val title: String,
  val content: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
): Routes.IndexedRoutes(
  route, index, {}
) {
  override val composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit = {
    navComposableData ->
    MainMenuItemLayout(title = title) {
      val navBuilder = this
      AnimatedEnter(navComposableData = navComposableData) {
        content(navBuilder, navComposableData)
      }
    }
  }

  @Composable
  private fun AnimatedEnter(
    navComposableData: NavComposableData,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
  ) {
    MainMenuContentTransition(
      slidingDirection = getSlidingDirection(
        navComposableData.prevNavBackStackEntry?.destination?.route
      ),
      content = content,
    )
  }

  //TODO: Sliding effect from item #2 to item #1 still get left direction (expected is right).
  // perhaps it has something to do with `popUpTo` in BottomNavBar navigation
  private fun getSlidingDirection(
    prevRouteString: String?,
  ): Direction {
    val prevRoute =
      if(prevRouteString == null) null
      else Routes.getMainMenuContentRoutes()
        .find { it.completeRoute == prevRouteString }

    return if(prevRoute?.let { it.index > index } != false) {
      Direction.LEFT
    } else Direction.RIGHT
  }
}