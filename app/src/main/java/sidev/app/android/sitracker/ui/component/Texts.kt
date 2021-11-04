package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DefaultText(
  text: String,
  modifier: Modifier = Modifier.fillMaxSize(),
) {
  Box(modifier) {
    Text(
      text,
      Modifier.align(Alignment.Center),
    )
  }
}