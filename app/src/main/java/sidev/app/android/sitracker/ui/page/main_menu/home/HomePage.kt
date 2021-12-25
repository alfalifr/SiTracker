@file:OptIn(ExperimentalPagerApi::class)

package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.*
import sidev.app.android.sitracker.ui.layout.LazyAnimatedItemScope
import sidev.app.android.sitracker.ui.nav.ComposableNavData
import sidev.app.android.sitracker.ui.nav.MainMenuItemNavData
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.ui.page.main_menu.MainMenuItemLayout
import sidev.app.android.sitracker.util.DataMapper.toPicUiData
import sidev.app.android.sitracker.util.DataMapper.toUiData
import sidev.app.android.sitracker.util.DefaultToast
import sidev.app.android.sitracker.util.defaultViewModel
import sidev.app.android.sitracker.util.maxSquareSideLen
import sidev.app.android.sitracker.util.pagerTransformation


@Composable
fun HomePage(
  //navData: ComposableNavData,
  navData: MainMenuItemNavData,
  //index: Int,
  //prevIndex: Int,
  //navController: NavController = rememberNavController(),
  viewModel: HomeViewModel = defaultViewModel(),
  //onItemClick: ((scheduleId: Int) -> Unit)? = null, //TODO: pair `HomePage.onItemClick`
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.getActiveSchedules()
    viewModel.activeTaskIndex.value = 0
  }

  //loge("HomePage: induk")
  MainMenuItemLayout(
    title = "What to do?",
    navData = navData,
    ignoreContentPadding = true,
    modifier = Modifier.fillMaxSize(),
    //navData = navData,
  ) {
    mainMenuItem {
      HomePageMainComp(
        //navData.parentNavController!!,
        navController = navData.navData.parentNavController!!,
        viewModel = viewModel,
        scope = this,
        //onItemClick,
      )
    }
  }
}


@Composable
private fun HomePageMainComp(
  navController: NavController,
  viewModel: HomeViewModel = defaultViewModel(),
  scope: LazyAnimatedItemScope,
  //onItemClick: ((scheduleId: Int) -> Unit)? = null,
) {

  val title = viewModel.activeTaskTitle
    .collectAsState(initial = null).value
  val lowerDetailData = viewModel.activeLowerDetailData
    .collectAsState(initial = null).value
  val itemData = viewModel.itemDataList
    .collectAsState(initial = null).value

  val pagerState = rememberPagerState()

  LaunchedEffect(key1 = pagerState.currentPage) {
    viewModel.activeTaskIndex.value = pagerState.currentPage
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxSize(),
  ) {
    EmptyPlacehoder(key = title) {
      Text(
        text = it,
        style = MaterialTheme.typography.h4,
      )
    }
    //Spacer(Modifier.height(15.dp))

    LoadingPlaceholder(
      key = itemData,
      loadingModifier = scope.run {
        Modifier.fillParentMaxSize()
      },
    ) { itemData ->
      BoxWithConstraints {
        val iconLen = maxSquareSideLen * .80f
        val iconPadding = iconLen * .30f

        //TODO: Set pager height as high as screen height so that user can easily reach scrollable part.
        HorizontalPager(
          count = itemData.size,
          modifier = Modifier
            .height(iconLen)
            .fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = iconPadding),
          state = pagerState,
        ) { page ->
          val data = itemData[page]
          val icon = data.first.toUiData()
          //loge("HomePage: HorizontalPage Item currentPage = $currentPage currentPageOffset = $currentPageOffset it = $page this = $this data= $data")

          val ctx = LocalContext.current

          IconProgressionPic(
            icon = icon.image,
            mainColor = icon.color,
            name = title,
            progress = icon.progress,
            progressStrokeWidth = 7.dp,
            modifier = Modifier
              .size(iconLen)
              .clickable {
                val scheduleId = data.second

                println("HomePage onItemClick scheduleId = $scheduleId")
                DefaultToast(
                  ctx,
                  "scheduleId = $scheduleId"
                )
                Route.CountDownPage.go(
                  navController,
                  scheduleId,
                )

                //onItemClick?.invoke(data.second)
              }
              //.background(Color.Blue)
              .pagerTransformation(
                pagerScope = this,
                page = page,
              ),
          )
        }
      }
    }

    EmptyPlacehoder(key = lowerDetailData) {
      HomeLowerDetail(it)
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
        text = data.duration,
      )
      if(data.startTime != null) {
        IconWithText(
          icon = painterResource(id = R.drawable.ic_clock),
          text = data.startTime,
        )
      }
    }
    Spacer(Modifier.height(15.dp))
    IconWithText(
      icon = painterResource(id = R.drawable.ic_bookmark),
      text = data.priority,
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