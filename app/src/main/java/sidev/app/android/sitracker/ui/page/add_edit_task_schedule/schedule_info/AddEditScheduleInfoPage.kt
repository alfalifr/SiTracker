package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.schedule_info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.AppOutlinedTextField
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.util.*

@Composable
fun AddEditScheduleInfoPage(
  scheduleId: Int?,
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    if(scheduleId != null) {
      viewModel.readExistingSchedule(scheduleId)
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    Spacer(Modifier.height(20.dp))
    Text(
      "Schedule Info",
      style = MaterialTheme.typography.h6,
      fontWeight = FontWeight.Bold,
    )

    Column(
      Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      AppOutlinedTextField(
        label = "Task",
        value = viewModel.taskName,
        validity = viewModel.taskNameValid,
        trailingIcon = { DropdownTrailingIcon() },
      )
      AppOutlinedTextField(
        label = "Label *",
        value = viewModel.scheduleLabel,
        validity = viewModel.scheduleLabelValid,
      )

      val progressTypeName = viewModel.selectedProgressType
        .collectAsState()
        .value
      AppOutlinedTextField(
        label = "Progress Kind",
        value = progressTypeName?.label ?: "",
        trailingIcon = { DropdownTrailingIcon() },
      )
    }


    val pagesMask = viewModel.pagesMask
      .collectAsState()
      .value

    println("AddEditScheduleInfoPage pagesMask = $pagesMask")

    AddEditTaskScheduleViewModel.apply {
      Box(Modifier.fillMaxWidth()) {
        if(pagesMask.hasMask(TASK_INFO_MASK)) {
          val taskId = viewModel.taskId
            .collectAsState(initial = null)
            .value
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .padding(10.dp)
              .clickable {
                Route.AddEditTaskInfoPage.go(
                  navController, taskId,
                  singleTop = true,
                )
              }
          ) {
            Icon(
              painter = painterResource(id = R.drawable.ic_arrow_right),
              contentDescription = "",
              tint = OppositeDark,
              modifier = Modifier.rotate(180f),
            )
            Spacer(Modifier.width(10.dp))
            Text(Texts.previous)
          }
        }
      }
    }
  }
}

@Composable
private fun DropdownTrailingIcon() {
  Icon(
    painter = painterResource(id = R.drawable.ic_arrow_right),
    contentDescription = "",
    modifier = Modifier.rotate(90f),
  )
}