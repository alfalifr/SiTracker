package sidev.app.android.sitracker.ui.component

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun SystemOverlay(
  statusBarColor: Color = MaterialTheme.colors.primary,
  statusBarUseDarkIcon: Boolean? = null,
  navBarColor: Color = Color.Black,
  navBarUseDarkIcon: Boolean? = null,
) {
  val systemController = rememberSystemUiController()
  val useDarkIcons = MaterialTheme.colors.isLight

  systemController.apply {
    //statusBarDarkContentEnabled = true
    setStatusBarColor(
      color = statusBarColor,
      darkIcons = statusBarUseDarkIcon ?: useDarkIcons,
    )
    setNavigationBarColor(
      color = navBarColor,
      darkIcons = navBarUseDarkIcon ?: useDarkIcons,
    )
  }
}