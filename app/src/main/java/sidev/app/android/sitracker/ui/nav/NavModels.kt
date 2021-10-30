package sidev.app.android.sitracker.ui.nav

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

data class NavComposableData(
  val navBackStackEntry: NavBackStackEntry,
  val prevNavBackStackEntry: NavBackStackEntry?,
  val navController: NavController,
  val parentNavController: NavController?,
)