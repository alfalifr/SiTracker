package sidev.app.android.sitracker.ui.nav

import androidx.navigation.NavController

sealed class Routes(val route: String) {
  // ===============
  open val completeRoute: String
    get() = route

  protected open fun go(navController: NavController) {
    navController.navigate(completeRoute)
  }
  // ========Child==========

  object HomePage: Routes("HomePage")
}