package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuItemLayout(
  title: String?,
  content: @Composable (upperContentPadding: PaddingValues) -> Unit,
) {
  Box {
    content(
      PaddingValues(15.dp)
    )
    if(title != null) {
      Text(
        text = title,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(10.dp),
      )
      //Spacer(Modifier.height(15.dp))
    }
  }
}