package sidev.app.android.sitracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import sidev.app.android.sitracker.ui.nav.AppNavGraph
import sidev.app.android.sitracker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        AppNavGraph()
      }
    }
  }
}