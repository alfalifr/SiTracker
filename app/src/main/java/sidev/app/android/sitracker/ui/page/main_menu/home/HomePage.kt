@file:OptIn(ExperimentalPagerApi::class)

package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
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
    lowerDetailData = HomeLowerDetailData(
      duration = 400,
      startTime = "20.30",
      priority = 2,
    ),
    iconList = listOf(
      HomeTaskIconData(
        image = rememberVectorPainter(Icons.Default.Call),
        color = Color.Green,
        progress = 78 / 100f,
      )
    )
  )
}

@Composable
private fun HomeMainComp(
  title: String,
  lowerDetailData: HomeLowerDetailData,
  iconList: List<HomeTaskIconData>,
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


    HorizontalPager(
      count = iconList.size,
      modifier = Modifier.height(100.dp),
      contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
      val data = iconList[currentPage]
      IconProgressionPic(
        icon = data.image,
        mainColor = data.color,
        name = title,
        progress = data.progress,
        progressStrokeWidth = 7.dp,
        modifier = Modifier.size(100.dp),
      )
    }

    Spacer(Modifier.height(20.dp))
    HomeLowerDetail(lowerDetailData)



    /*
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
     */
  }
}

@Composable
private fun HomeLowerDetail(
  data: HomeLowerDetailData,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
      IconWithText(
        icon = Icons.Outlined.Call, //TODO: Change icon to stopwatch
        text = "${data.duration} millis", //TODO: Change the time format
      )
      if(data.startTime != null) {
        IconWithText(
          icon = Icons.Outlined.Call, //TODO: Change icon to clock
          text = data.startTime, //TODO: Change the time format
        )
      }
    }
    Spacer(Modifier.height(15.dp))
    IconWithText(
      icon = Icons.Outlined.Call, //TODO: Change icon to bookmark
      text = "Priority #${data.priority}", //TODO: Change the time format
    )
  }
}