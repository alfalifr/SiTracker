@file:OptIn(ExperimentalPagerApi::class)

package sidev.app.android.sitracker.ui.page.main_menu.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.DefaultLoading
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.component.IconWithText
import sidev.app.android.sitracker.ui.model.IconProgressionUiData
import sidev.app.android.sitracker.util.DataMapper.toUiData
import sidev.app.android.sitracker.util.defaultViewModel
import sidev.app.android.sitracker.util.loge
import sidev.app.android.sitracker.util.maxSquareSideLen
import sidev.app.android.sitracker.util.pagerTransformation
import kotlin.math.absoluteValue


@Composable
fun HomePage(
  navController: NavController = rememberNavController(),
  viewModel: HomeViewModel = defaultViewModel(),
  onItemClick: ((progressId: Int) -> Unit)? = null, //TODO: pair `HomePage.onItemClick`
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.getActiveSchedules()
    viewModel.activeTaskIndex.value = 0
  }

  //loge("HomePage: induk")

  HomePageMainComp(viewModel)
}


@Composable
private fun HomePageMainComp(
  viewModel: HomeViewModel = defaultViewModel()
) {

  val title = viewModel.activeTaskTitle
    .collectAsState(initial = null).value
  val lowerDetailData = viewModel.activeLowerDetailData
    .collectAsState(initial = null).value
  val iconList = viewModel.iconResIds
    .collectAsState(initial = null).value
    ?.toUiData()

  val pagerState = rememberPagerState()

  LaunchedEffect(key1 = pagerState.currentPage) {
    viewModel.activeTaskIndex.value = pagerState.currentPage
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxSize(),
  ) {
    if(title != null) {
      Text(
        text = title,
        style = MaterialTheme.typography.h4,
      )
    } else {
      DefaultLoading()
    }
    //Spacer(Modifier.height(15.dp))

    if(iconList != null) {
      BoxWithConstraints {
        val iconLen = maxSquareSideLen * .80f
        val iconPadding = iconLen * .30f

        HorizontalPager(
          count = iconList.size,
          modifier = Modifier
            .height(iconLen)
            .fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = iconPadding),
          state = pagerState,
        ) { page ->
          val data = iconList[page]
          loge("HomePage: HorizontalPage Item currentPage = $currentPage currentPageOffset = $currentPageOffset it = $page this = $this data= $data")
          IconProgressionPic(
            icon = data.image,
            mainColor = data.color,
            name = title,
            progress = data.progress,
            progressStrokeWidth = 7.dp,
            modifier = Modifier
              .size(iconLen)
              //.background(Color.Blue)
              .pagerTransformation(
                pagerScope = this,
                page = page,
              ),
          )
        }
      }
    } else {
      DefaultLoading()
    }

    if(lowerDetailData != null) {
      //Spacer(Modifier.height(20.dp))
      HomeLowerDetail(lowerDetailData)
    }
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
        icon = painterResource(id = R.drawable.ic_timer),
        text = "${data.duration} millis", //TODO: Change the time format
      )
      if(data.startTime != null) {
        IconWithText(
          icon = painterResource(id = R.drawable.ic_clock),
          text = data.startTime, //TODO: Change the time format
        )
      }
    }
    Spacer(Modifier.height(15.dp))
    IconWithText(
      icon = painterResource(id = R.drawable.ic_bookmark),
      text = "Priority #${data.priority}", //TODO: Change the time format
    )
  }
}




/*

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
      IconProgressionUiData(
        image = rememberVectorPainter(Icons.Default.Call),
        color = Color.Green,
        progress = 78 / 100f,
      ),
      IconProgressionUiData(
        image = rememberVectorPainter(Icons.Default.Person),
        color = Color.Green,
        progress = 48 / 100f,
      )
    )
  )
}

@Composable
private fun HomeMainComp(
  title: String?,
  lowerDetailData: HomeLowerDetailData?,
  iconList: List<IconProgressionUiData>?,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    //TODO: Change `IconProgressionPic` to `HorizontalPager` here
    //HorizontalPager() {} ...





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
 */