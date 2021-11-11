@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.nav

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.component.HorizontalSlidingTransition
import sidev.app.android.sitracker.ui.layout.*
import sidev.app.android.sitracker.ui.page.task_detail.TaskDetailPage
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuPage
import sidev.app.android.sitracker.ui.page.main_menu.calendar.CalendarPage
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage
import sidev.app.android.sitracker.ui.page.main_menu.today_schedule.TodaySchedulePage
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.DefaultToast
import sidev.app.android.sitracker.util.model.Direction
//TODO: Compiler error
sealed class Route(
  val route: String,
  open val composable: @Composable NavGraphBuilder.(ComposableNavData) -> Unit,
  val arguments: List<NamedNavArgument> = emptyList(),
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
      mainMenuItem {
        val ctx = LocalContext.current
        HomePage(
          navController = it.navData.navController,
          onItemClick = { progressId ->
            DefaultToast(ctx, "progressId = $progressId")
            //TODO("Implement `Routes.HomePage.onItemClick`")
          }
        )
      }
    },
  )
  object TodaySchedulePage: MainMenuItemRoute(
    "TodaySchedulePage",
    1,
    "Today's schedule",
    content = {
      val mainScaffoldScope = this
      mainMenuItem {
        val ctx = LocalContext.current
        TodaySchedulePage(
          navController = it.navData.navController,
          mainScaffoldScope = mainScaffoldScope,
          onItemClick = { scheduleId ->
            DefaultToast(ctx, "scheduleId = $scheduleId")
            //TODO("Implement `Routes.TodaySchedulePage.onItemClick`")
          }
        )
      }
    },
  )
  object CalendarPage: MainMenuItemRoute(
    "CalendarPage",
    2,
    "Your calendar",
    content = {
      mainMenuItem {
        CalendarPage(navController = it.navData.navController)
      }
    },
  )
  object TaskDetailPage: ScaffoldedRoute(
    "TaskDetailPage",
    data = ScaffoldedRouteData(),
    arguments = listOf(
      navArgument(Const.taskId) {
        type = NavType.IntType
      }
    ),
    content = {
      item {
        TaskDetailPage(
          navController = it.navData.navController,
          taskId = it.navData.navBackStackEntry
            .arguments!!.getInt(Const.taskId),
        )
      }
    },
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.taskId}}"

    fun go(
      navController: NavController,
      taskId: Int,
    ) {
      navController.navigate("$route/$taskId")
    }
  }


}



sealed class ScaffoldedRoute(
  route: String,
  val data: ScaffoldedRouteData = ScaffoldedRouteData(),
  open val scaffoldBuilder: @Composable NavGraphBuilder.(
    content: MainScaffoldScope.(data: ScaffoldedComposableNavData) -> Unit,
    routeData: ScaffoldedRouteData,
    navData: ComposableNavData,
  ) -> Unit =
    { content, routeData, navData ->
      //val graphBuilder = this
      with(routeData) {
        TitleIconLayout(
          title = title,
          icon = iconResId?.let { painterResource(id = it) },
          iconColor = iconColor ?: MaterialTheme.colors.primary,
          actionData = actions,
          ignoreContentPadding = ignoreContentPadding,
          content = {
            content(
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
  open val content: MainScaffoldScope.(ScaffoldedComposableNavData) -> Unit,
  arguments: List<NamedNavArgument> = emptyList(),
): Route(
  route = route,
  composable = {},
  arguments = arguments,
) {
  override val composable: @Composable NavGraphBuilder.(ComposableNavData) -> Unit =
    { scaffoldBuilder(content, data, it) }
}



sealed class MainMenuItemRoute(
  route: String,
  val index: Int,
  title: String,
  ignoreContentPadding: Boolean = false,
  content: MainMenuContentScope.(ScaffoldedComposableNavData) -> Unit,
  arguments: List<NamedNavArgument> = emptyList(),
): ScaffoldedRoute(
  route = route,
  data = ScaffoldedRouteData(
    title = title,
    ignoreContentPadding = ignoreContentPadding,
  ),
  content = { this.toMainMenContentScope(index, it.navData).content(it) },
  scaffoldBuilder = { _, _, _ -> },
  arguments = arguments,
) {
  override val scaffoldBuilder: @Composable NavGraphBuilder.(
    //Don't forget to add `@Composable` so there won't be bug.
    content: MainMenuContentScope.(data: ScaffoldedComposableNavData) -> Unit,
    routeData: ScaffoldedRouteData,
    navData: ComposableNavData,
  ) -> Unit =
    { content, routeData, navData ->
      //val navBuilder = this
      MainMenuItemLayout(
        title = title,
        index = index,
        ignoreContentPadding = ignoreContentPadding,
      ) { contentPadding ->
        //val lazyListScope = this
        content(
          ScaffoldedComposableNavData(
            routeData = routeData,
            navData = navData,
            contentPadding = contentPadding,
          )
        )
        /*
        item {
          AnimatedEnter(navComposableData = navData) {
            ///*
            lazyListScope.content(
              ScaffoldedComposableNavData(
                routeData = routeData,
                navData = navData,
                contentPadding = contentPadding,
              )
            )
            // */
            //Text(route)
            DefaultText(route)
          }
        }
         */
      }
    }

  @Composable
  private fun AnimatedEnter(
    navComposableData: ComposableNavData,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
  ) {
    HorizontalSlidingTransition(
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