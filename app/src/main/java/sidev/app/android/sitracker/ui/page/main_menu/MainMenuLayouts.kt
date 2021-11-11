package sidev.app.android.sitracker.ui.page.main_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.layout.*

//TODO: Extract logic to common function
@Composable
fun MainMenuItemLayout(
  title: String?,
  index: Int,
  ignoreContentPadding: Boolean = false,
  content: MainMenuContentScope.(contentPadding: Dp) -> Unit,
) {
  TitleIconLayout(
    title = title,
    ignoreContentPadding = ignoreContentPadding,
    content = {
      toMainMenContentScope(index).content(it)
    },
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