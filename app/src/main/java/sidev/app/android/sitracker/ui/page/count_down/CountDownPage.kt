package sidev.app.android.sitracker.ui.page.count_down

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.*
import sidev.app.android.sitracker.ui.theme.FollowingDark
import sidev.app.android.sitracker.util.Color
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.defaultViewModel

@Composable
fun CountDownPage(
  scheduleId: Int,
  viewModel: CountDownViewModel = defaultViewModel(),
  navController: NavController = rememberNavController(),
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.loadFromDb(scheduleId)
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxSize(),
  ) {
    val iconData = viewModel.iconData
      .collectAsState(initial = null).value

    val progress = viewModel.progress
      .collectAsState(initial = 0f).value

    LoadingPlaceholder(key = iconData) {
      IconProgressionPic(
        icon = painterResource(id = it.resId),
        mainColor = Color(it.color),
        name = it.desc,
        progress = progress,
        modifier = Modifier.size(60.dp) //TODO: Set responsiveness
      )
    }

    Spacer(Modifier.height(20.dp))

    val countDownStr = viewModel.countDownProgressStr
      .collectAsState(initial = null).value

    EmptyPlacehoder(key = countDownStr) {
      Text(
        text = it,
        style = MaterialTheme.typography.h3,
      )
    }

    val checkpointStr = viewModel.checkpointCountDownProgressStr
      .collectAsState(initial = null).value

    EmptyPlacehoder(key = checkpointStr) {
      IconWithText(
        icon = painterResource(id = R.drawable.ic_flag_outline),
        text = it,
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(20.dp),
      modifier = Modifier.heightIn(
        max = 50.dp, //TODO: Set responsiveness
      )
    ) {
      IconProgressionPic(
        mainColor = FollowingDark,
        icon = painterResource(id = R.drawable.ic_reset),
        name = Texts.reset,
        modifier = Modifier.clickable {
          viewModel.resetCountDown()
        },
      )
      val isCounting = viewModel.isCounting
        .collectAsState(initial = false).value
      IconProgressionPic(
        mainColor = FollowingDark,
        icon = painterResource(
          if(isCounting) R.drawable.ic_pause
          else R.drawable.ic_play
        ),
        name = Texts.play,
        modifier = Modifier.clickable {
          viewModel.playCountDown()
        }
      )
      IconProgressionPic(
        mainColor = FollowingDark,
        icon = painterResource(id = R.drawable.ic_flag),
        name = Texts.setCheckpoint,
        modifier = Modifier.clickable {
          viewModel.setCheckpoint()
        }
      )
    }
  }
}

