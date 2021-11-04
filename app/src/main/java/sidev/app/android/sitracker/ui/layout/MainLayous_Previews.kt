package sidev.app.android.sitracker.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.model.ActionData


@Composable
@Preview
fun TitleIconLayout_preview() {
  TitleIconLayout(
    title = "This is header afuh aifjaof ", // afaf afag asfa afa afafagag agag  afagijaf iafaafaf
    icon = rememberVectorPainter(image = Icons.Rounded.Star),
    iconColor = Color.Green,
    actionData = listOf(
      ActionData(
        icon = rememberVectorPainter(image = Icons.Rounded.Person),
        name = "Contact",
        color = Color.Blue,
      ) {},
      ActionData(
        icon = rememberVectorPainter(image = Icons.Rounded.Star),
        name = "Favorite",
        color = Color.Red,
      ) {},
    ),
    ignoreContentPadding = false,
  ) {
    BoxWithConstraints {
      val contentHeight = maxHeight * 2f
      //println("Preview maxHeight = $maxHeight contentHeight = $contentHeight")
      DefaultText(
        text = "This is the content",
        modifier = Modifier
          .background(Color.Red)
          .fillMaxWidth()
          .height(1000.dp),
      )
    }
  }
}


@Composable
@Preview
fun TitleIconLayout_preview_withoutIcon() {
  TitleIconLayout(
    title = "This is header afuh aifjaof ", // afaf afag asfa afa afafagag agag  afagijaf iafaafaf
    //iconColor = Color.Green,
    /*
    actionData = listOf(
      ActionData(
        icon = rememberVectorPainter(image = Icons.Rounded.Person),
        name = "Contact",
        color = Color.Blue,
      ) {},
      ActionData(
        icon = rememberVectorPainter(image = Icons.Rounded.Star),
        name = "Favorite",
        color = Color.Red,
      ) {},
    ),
     */
    ignoreContentPadding = false,
  ) {
    BoxWithConstraints {
      val contentHeight = maxHeight * 2f
      println("Preview maxHeight = $maxHeight contentHeight = $contentHeight")
      DefaultText(
        text = "This is the content",
        modifier = Modifier
          .background(Color.Red)
          .fillMaxWidth()
          .height(1000.dp),
      )
    }
  }
}