@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.nav

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import sidev.app.android.sitracker.ui.component.MainMenuContentTransition
import sidev.app.android.sitracker.ui.layout.TitleIconLayout
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuPage
import sidev.app.android.sitracker.ui.page.main_menu.calendar.CalendarPage
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage
import sidev.app.android.sitracker.ui.page.main_menu.today_schedule.TodaySchedulePage
import sidev.app.android.sitracker.util.model.Direction

sealed class Route(
  val route: String,
  open val composable: @Composable NavGraphBuilder.(ComposableNavData) -> Unit,
  //open val ignoreContentPadding: Boolean = false,
) {

  /*
  sealed class IndexedRoute(
    route: String,
    val index: Int,
    composable: @Composable NavGraphBuilder.(NavComposableData) -> Unit,
  ): Route(
    route = route,
    composable = composable,
  )
   */

  // ===============
  companion object {
    fun getAppRoutes(): List<Route> = listOf(
      MainMenuPage,
    )
    fun getMainMenuContentRoutes(): List<MainMenuItemRoute> = listOf(
      HomePage, TodaySchedulePage, CalendarPage,
    )
  }

  open val completeRoute: String
    get() = route

  protected open fun go(navController: NavController) {
    navController.navigate(completeRoute)
  }
  // ========Child==========

  // ------- Main Menu -------
  object MainMenuPage: Route("MainMenu", {
    MainMenuPage(navController = it.navController)
  })

  object HomePage: MainMenuItemRoute(
    "HomePage",
    0,
    "What to do?",
    ignoreContentPadding = true,
    content = {
      HomePage(navController = it.navData.navController) {
        TODO("Implement `Routes.HomePage.onItemClick`")
      }
    },
  )
  object TodaySchedulePage: MainMenuItemRoute(
    "TodaySchedulePage",
    1,
    "Today's schedule",
    content = {
      TodaySchedulePage(navController = it.navData.navController)
    },
  )
  object CalendarPage: MainMenuItemRoute(
    "CalendarPage",
    2,
    "Your calendar",
    content = {
      CalendarPage(navController = it.navData.navController)
    },
  )


}



sealed class ScaffoldedRoute(
  route: String,
  val data: ScaffoldedRouteData = ScaffoldedRouteData(),
  open val scaffoldBuilder: @Composable NavGraphBuilder.(
    content: @Composable NavGraphBuilder.(data: ScaffoldedComposableNavData) -> Unit,
    routeData: ScaffoldedRouteData,
    navData: ComposableNavData,
  ) -> Unit =
    { content, routeData, navData ->
      val graphBuilder = this
      with(routeData) {
        TitleIconLayout(
          title = title,
          icon = iconResId?.let { painterResource(id = it) },
          iconColor = iconColor ?: MaterialTheme.colors.primary,
          actionData = actions,
          ignoreContentPadding = ignoreContentPadding,
          content = {
            graphBuilder.content(
              ScaffoldedComposableNavData(
                navData = navData,
                routeData = routeData,
                contentPadding = it,
              )
            )
          },
        )
      }
    },
  open val content: @Composable NavGraphBuilder.(data: ScaffoldedComposableNavData) -> Unit,
): Route(
  route = route,
  composable = {},
) {
  override val composable: @Composable NavGraphBuilder.(ComposableNavData) -> Unit =
    { this.scaffoldBuilder(content, data, it) }
}



sealed class MainMenuItemRoute(
  route: String,
  val index: Int,
  title: String,
  ignoreContentPadding: Boolean = false,
  content: @Composable NavGraphBuilder.(ScaffoldedComposableNavData) -> Unit,
): ScaffoldedRoute(
  route = route,
  data = ScaffoldedRouteData(
    title = title,
    ignoreContentPadding = ignoreContentPadding,
  ),
  content = content,
  scaffoldBuilder = { _, _, _ -> }
) {
  override val scaffoldBuilder: @Composable NavGraphBuilder.(
    //Don't forget to add `@Composable` so there won't be bug.
    content: @Composable NavGraphBuilder.(data: ScaffoldedComposableNavData) -> Unit,
    routeData: ScaffoldedRouteData,
    navData: ComposableNavData,
  ) -> Unit =
    { content, routeData, navData ->
      val navBuilder = this
      MainMenuItemLayout(
        title = title,
        ignoreContentPadding = ignoreContentPadding,
      ) { contentPadding ->
        AnimatedEnter(navComposableData = navData) {
          navBuilder.content(
            ScaffoldedComposableNavData(
              routeData = routeData,
              navData = navData,
              contentPadding = contentPadding,
            )
          )
        }
      }
    }

  @Composable
  private fun AnimatedEnter(
    navComposableData: ComposableNavData,
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
      else Route.getMainMenuContentRoutes()
        .find { it.completeRoute == prevRouteString }

    return if(prevRoute?.let { it.index > index } != false) {
      Direction.LEFT
    } else Direction.RIGHT
  }
}