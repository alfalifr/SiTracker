package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.component.IconWithText

@Composable
fun HomePage() {

}

@Composable
@Preview
private fun HomeMainComp_preview() {
  HomeMainComp(
    title = "Code 20 Lines",
    duration = 400,
    startTime = "20.30",
    priority = 1,
  )
}

@Composable
private fun HomeMainComp(
  title: String,
  duration: Long,
  startTime: String,
  priority: Int,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.h4,
    )
    Spacer(Modifier.height(15.dp))
    //TODO: Change `IconProgressionPic` to `HorizontalPager` here
    //HorizontalPager() {} ...
    IconProgressionPic(
      icon = Icons.Outlined.Call,
      mainColor = Color.Green,
      name = title,
      progress = 78 / 100f,
      progressStrokeWidth = 7.dp,
      modifier = Modifier.size(100.dp),
    )

    Spacer(Modifier.height(20.dp))
    Row(
      horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
      IconWithText(
        icon = Icons.Outlined.Call, //TODO: Change icon to stopwatch
        text = "$duration millis", //TODO: Change the time format
      )
      IconWithText(
        icon = Icons.Outlined.Call, //TODO: Change icon to clock
        text = startTime, //TODO: Change the time format
      )
    }
    Spacer(Modifier.height(15.dp))
    IconWithText(
      icon = Icons.Outlined.Call, //TODO: Change icon to bookmark
      text = "Priority #$priority", //TODO: Change the time format
    )
  }
}