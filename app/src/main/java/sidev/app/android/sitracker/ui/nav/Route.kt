@file:OptIn(
  ExperimentalAnimationApi::class,
  ExperimentalFoundationApi::class,
)
package sidev.app.android.sitracker.ui.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import androidx.navigation.navOptions
import sidev.app.android.sitracker.ui.layout.*
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskSchedulePage
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.schedule_info.AddEditScheduleInfoPage
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info.AddEditTaskInfoPage
import sidev.app.android.sitracker.ui.page.count_down.CountDownPage
import sidev.app.android.sitracker.ui.page.task_detail.TaskDetailPage
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuPage
import sidev.app.android.sitracker.ui.page.main_menu.calendar.CalendarPage
import sidev.app.android.sitracker.ui.page.main_menu.home.HomePage
import sidev.app.android.sitracker.ui.page.main_menu.today_schedule.TodaySchedulePage
import sidev.app.android.sitracker.ui.page.schedule_detail.ScheduleDetailPage
import sidev.app.android.sitracker.ui.page.schedule_list.ScheduleListPage
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.DefaultToast
import sidev.app.android.sitracker.util.defaultViewModel
import sidev.app.android.sitracker.util.viewModel

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
      MainMenuPage, ScheduleListPage,
      ScheduleDetailPage, TaskDetailPage, CountDownPage,
      AddEditTaskSchedulePage,
    )
    fun getMainMenuContentRoutes(): List<MainMenuItemRoute> = listOf(
      HomePage, TodaySchedulePage, CalendarPage,
    )
    fun getAddEditTaskScheduleRoutes(): List<Route> = listOf(
      AddEditTaskInfoPage, AddEditScheduleInfoPage
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
    //"What to do?",
    //ignoreContentPadding = true,
    content = {
      //val ctx = LocalContext.current
      HomePage(
        navData = it,
        //navController = it.parentNavController!!,
        /*
        onItemClick = { scheduleId ->
          println("HomePage onItemClick scheduleId = $scheduleId")
          DefaultToast(ctx, "scheduleId = $scheduleId")
          CountDownPage.go(
            it.parentNavController!!,
            scheduleId,
          )
        }
         */
      )
    },
  )
  object TodaySchedulePage: MainMenuItemRoute(
    "TodaySchedulePage",
    1,
    //"Today's schedule",
    content = {
      //val mainScaffoldScope = this

      val ctx = LocalContext.current
      TodaySchedulePage(
        navData = it,
        //navController = it.navController,
        //mainScaffoldScope = mainScaffoldScope,
        onItemClick = { scheduleId ->
          DefaultToast(ctx, "scheduleId = $scheduleId")
          ScheduleDetailPage.go(
            it.navData.parentNavController!!,
            scheduleId = scheduleId,
          )
          //TODO("Implement `Routes.TodaySchedulePage.onItemClick`")
        }
      )
      //mainMenuItem {}
    },
  )
  object CalendarPage: MainMenuItemRoute(
    "CalendarPage",
    2,
    //"Your calendar",
    content = {
      CalendarPage(navData = it)
      //mainMenuItem {}
    },
  )


  object TaskDetailPage: Route(
    "TaskDetailPage",
    arguments = listOf(
      navArgument(Const.taskId) {
        type = NavType.IntType
      }
    ),
    composable = {
      TaskDetailPage(
        navController = it.navController,
        taskId = it.navBackStackEntry
          .arguments!!.getInt(Const.taskId),
      )
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

  object ScheduleListPage: Route(
    "ScheduleListPage",
    arguments = listOf(
      navArgument(Const.taskId) {
        type = NavType.IntType
      }
    ),
    composable = {
      ScheduleListPage(
        taskId = it.navBackStackEntry.arguments!!.getInt(Const.taskId),
        navController = it.navController,
      )
    }
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

  object ScheduleDetailPage: Route(
    "ScheduleDetailPage",
    arguments = listOf(
      navArgument(Const.scheduleId) {
        type = NavType.IntType
      }
    ),
    composable = {
      ScheduleDetailPage(
        navController = it.navController,
        scheduleId = it.navBackStackEntry
          .arguments!!.getInt(Const.scheduleId),
        /*
        onIconClick = { taskId ->
          TaskDetailPage.go(
            it.navController,
            taskId = taskId,
          )
        },
         */
      )
    },
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.scheduleId}}"

    fun go(
      navController: NavController,
      scheduleId: Int,
    ) {
      navController.navigate("$route/$scheduleId")
    }
  }

  object CountDownPage: Route(
    "CountDownPage",
    arguments = listOf(
      navArgument(Const.scheduleId) {
        type = NavType.IntType
      },
    ),
    composable = {
      CountDownPage(
        scheduleId = it.navBackStackEntry
          .arguments!!.getInt(Const.scheduleId),
        navController = it.navController,
      )
    }
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.scheduleId}}"

    fun go(
      navController: NavController,
      scheduleId: Int,
    ) {
      navController.navigate("$route/$scheduleId")
    }
  }

  object AddEditTaskSchedulePage: Route(
    route = "AddEditTaskSchedulePage",
    composable = {
      AddEditTaskSchedulePage(
        scheduleId = it.navBackStackEntry.arguments
          ?.getInt(Const.scheduleId, -1)
          ?.let { id ->
            if(id >= 0) id else null
          },
        navController = it.navController,
        pagesMask = it.navBackStackEntry.arguments
          ?.getInt(Const.pagesMask, 0)
          ?: 0,
      )
    },
    arguments = listOf(
      navArgument(Const.scheduleId) {
        type = NavType.IntType
        defaultValue = -1
      },
      navArgument(Const.pagesMask) {
        type = NavType.IntType
        defaultValue = 0
      }
    ),
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.pagesMask}}/{${Const.scheduleId}}"

    fun go(
      navController: NavController,
      pagesMask: Int,
      scheduleId: Int?,
    ) {
      navController.navigate(
        "$route/$pagesMask/${scheduleId ?: -1}"
      )
    }
  }
  //@ExperimentalFoundationApi
  object AddEditTaskInfoPage: Route(
    route = "AddEditTaskInfoPage",
    composable = {
      AddEditTaskInfoPage(
        taskId = it.navBackStackEntry.arguments
          ?.getInt(Const.taskId, -1)
          ?.let { id ->
            if(id >= 0) id else null
          },
        navController = it.navController,
        viewModel = it.viewModel(),
      )
    },
    arguments = listOf(
      navArgument(Const.taskId) {
        type = NavType.IntType
        defaultValue = -1
      }
    ),
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.taskId}}"

    fun go(
      navController: NavController,
      taskId: Int?,
      singleTop: Boolean = true,
    ) {
      navController.navigate(
        "$route/${taskId ?: -1}"
      ) {
        if(singleTop) {
          launchSingleTop = true
          popUpTo(completeRoute)
        }
      }
    }
  }
  object AddEditScheduleInfoPage: Route(
    "AddEditScheduleInfoPage",
    composable = {
      AddEditScheduleInfoPage(
        scheduleId = it.navBackStackEntry.arguments
          ?.getInt(Const.scheduleId)
          ?.let { id ->
            if(id >= 0) id else null
          },
        navController = it.navController,
        viewModel = it.viewModel(),
      )
    },
    arguments = listOf(
      navArgument(Const.scheduleId) {
        type = NavType.IntType
        defaultValue = -1
      }
    ),
  ) {
    override val completeRoute: String
      get() = "$route/{${Const.scheduleId}}"

    fun go(
      navController: NavController,
      scheduleId: Int?,
      singleTop: Boolean = true,
    ) {
      navController.navigate(
        "$route/${scheduleId ?: -1}"
      ) {
        if(singleTop) {
          launchSingleTop = true
          popUpTo(AddEditTaskInfoPage.completeRoute)
        }
      }
    }
  }
}


/*
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
 */


/**
 * Route that specializes for Main Menu item.
 * It has [index] that matters for sliding transition direction.
 */
sealed class MainMenuItemRoute(
  route: String,
  val index: Int,
  //title: String,
  //ignoreContentPadding: Boolean = false,
  //content: MainMenuContentScope.(ScaffoldedComposableNavData) -> Unit,
  private val content: @Composable NavGraphBuilder.(MainMenuItemNavData) -> Unit,
  arguments: List<NamedNavArgument> = emptyList(),
): Route(
  route = route,
  arguments = listOf(
    navArgument(Const.mainMenuPrevItemIndex) {
      type = NavType.IntType
      defaultValue = -1
    }
  ) + arguments,
  composable = {},
) {
  override val completeRoute: String
    get() = "$route/{${Const.mainMenuPrevItemIndex}}"

  override val composable: @Composable NavGraphBuilder.(ComposableNavData) -> Unit = { navData ->
    val prevIndex = navData.navBackStackEntry.arguments!!.getInt(Const.mainMenuPrevItemIndex, -1)
      .let { if(it >= 0) it else null }
    println("MainMenuItemRoute route = $route prevIndex = $prevIndex index = $index")
    content(
      MainMenuItemNavData(
        navData, index, prevIndex,
      )
    )
  }

  fun go(
    navController: NavController,
    prevIndex: Int,
  ) {
    navController.navigate("$route/$prevIndex") {
      ///*
      val start = navController.graph.startDestinationRoute
      if(start != null) {
        popUpTo(start) {
          //saveState = true
        }
      }
      // */
      launchSingleTop = true
      //restoreState = true
    }
  }
}


/*
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
  content = { toMainMenuContentScope(index, it.navData).content(it) },
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

  /*
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
   */
}
 */