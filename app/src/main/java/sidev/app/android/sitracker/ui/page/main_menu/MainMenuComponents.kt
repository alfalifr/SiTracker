package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.ui.theme.FollowingDark
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.ui.theme.TransOppositeDarkColor2


@Composable
@Preview
private fun BottomNavBar_preview() {
  MainMenuBottomNavBar(
    navItems = listOf(
      MainMenuNavItem.Home,
      MainMenuNavItem.TodaySchedule,
      MainMenuNavItem.Calendar,
    )
  )
}

@Composable
fun MainMenuBottomNavBar(
  navItems: List<MainMenuNavItem>,
  navController: NavController = rememberNavController(),
) {
  BottomNavigation(
    backgroundColor = FollowingDark,
    contentColor = OppositeDark,
  ) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val selectedIconSize = with(LocalDensity.current) {
      (MaterialTheme.typography.caption.fontSize * 2.8).toDp()
    }
    val unselectedIconSize = with(LocalDensity.current) {
      (MaterialTheme.typography.caption.fontSize * 2.0).toDp()
    }


    navItems.forEachIndexed { i, item ->
      val isSelected = currentRoute == item.route

      BottomNavigationItem(
        modifier = Modifier.semantics {
          contentDescription = item.label
        },
        icon = {
          val iconSizeAnimation by animateDpAsState(
            targetValue =
              if(isSelected) selectedIconSize
              else unselectedIconSize,
          )
          Icon(
            painter = painterResource(id = item.iconSource),
            contentDescription = null,
            modifier = Modifier
              .size(iconSizeAnimation)
          )
        },
        label = {
          Text(
            text = item.label,
            modifier = Modifier.clearAndSetSemantics {  },
          )
        },
        selectedContentColor = MaterialTheme.colors.primary,
        unselectedContentColor = TransOppositeDarkColor2,
        selected = isSelected, //currentRoute == item.route,
        onClick = {
          val prevRoute = navController.currentBackStackEntry?.destination?.route
          val prevIndex = if(prevRoute != null) {
            Route.getMainMenuContentRoutes()
              .find { it.completeRoute == prevRoute }?.index
              ?: -1
          } else -1

          println("prevRoute = $prevRoute prevIndex = $prevIndex")

          item.onNavigate(navController, prevIndex)

          /*
          navController.navigate(item.route) {
            val start = navController.graph.startDestinationRoute
            if(start != null) {
              popUpTo(start) {
                saveState = true
              }
            }
            launchSingleTop = true
            restoreState = true
          }
           */
        }
      )
    }
  }
}