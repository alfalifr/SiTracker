package sidev.app.android.sitracker.ui.page.schedule_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.ui.component.LargeSurface
import sidev.app.android.sitracker.ui.component.LoadingPlaceholder
import sidev.app.android.sitracker.ui.layout.TitleIconLayout
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.util.defaultViewModel

@Composable
fun ScheduleListPage(
  taskId: Int,
  navController: NavController = rememberNavController(),
  viewModel: ScheduleListViewModel = defaultViewModel(),
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    viewModel.loadData(taskId)
  }

  val taskName = viewModel.header
    .collectAsState(initial = null).value
  val dataList = viewModel.scheduleList
    .collectAsState(initial = null).value

  LoadingPlaceholder(key = dataList) { dataList ->
    TitleIconLayout(title = taskName) {
      items(dataList.size) {
        ScheduleItem(
          dataList[it],
          navController,
        )
      }
    }
  }
}

@Composable
private fun ScheduleItem(
  data: TaskScheduleListItem,
  navController: NavController,
) {
  LargeSurface {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
        .clickable {
          Route.ScheduleDetailPage.go(
            navController, data.scheduleId,
          )
        },
    ) {
      Text(
        text = data.name,
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.Bold,
      )
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = data.preferredTime,
          style = MaterialTheme.typography.body2,
        )
        Text(
          text = data.preferredDay,
          style = MaterialTheme.typography.body2,
          textAlign = TextAlign.End,
        )
      }
    }
  }
}