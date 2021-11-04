package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.layout.TitleIconLayout

//TODO: Extract logic to common function
@Composable
fun MainMenuItemLayout(
  title: String?,
  ignoreContentPadding: Boolean = false,
  content: @Composable (contentPadding: Dp) -> Unit,
) {
  TitleIconLayout(
    title = title,
    ignoreContentPadding = ignoreContentPadding,
    content = content,
  )
  /*
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
   */
}