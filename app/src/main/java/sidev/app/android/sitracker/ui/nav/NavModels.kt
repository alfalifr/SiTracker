package sidev.app.android.sitracker.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import sidev.app.android.sitracker.ui.model.ActionData


data class ComposableNavData(
  val navBackStackEntry: NavBackStackEntry,
  val prevNavBackStackEntry: NavBackStackEntry?,
  val navController: NavController,
  val parentNavController: NavController?,
  val viewModelFactory: ViewModelProvider.Factory?,
)

data class MainMenuItemNavData(
  val navData: ComposableNavData,
  val index: Int,
  val prevIndex: Int?,
)

data class ScaffoldedComposableNavData(
  val navData: ComposableNavData,
  val routeData: ScaffoldedRouteData,
  val contentPadding: Dp,
)

data class ScaffoldedRouteData(
  val title: String? = null,
  @DrawableRes
  val iconResId: Int? = null,
  val iconColor: Color? = null,
  val actions: List<ActionData> = emptyList(),
  val ignoreContentPadding: Boolean = false,
  /*
  val scaffoldBuilder: @Composable ((content: @Composable (contentPadding: Dp) -> Unit) -> Unit)? = { content ->
    TitleIconLayout(
      title = title,
      icon = iconResId?.let { painterResource(id = it) },
      iconColor = iconColor ?: MaterialTheme.colors.primary,
      actionData = actions,
      ignoreContentPadding = ignoreContentPadding,
      content = content,
    )
  },
   */
  //val scaffoldBuilder: @Composable (() -> Unit)? = null,
)