package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DefaultLoading(
  modifier: Modifier = Modifier,
  text: String? = "Loading...", //TODO: replace with resource string.
  spaceBetween: Dp? = null,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    CircularProgressIndicator()
    if(text != null) {
      Spacer(modifier = Modifier.height(spaceBetween ?: 10.dp))
      Text(text = text)
    }
  }
}