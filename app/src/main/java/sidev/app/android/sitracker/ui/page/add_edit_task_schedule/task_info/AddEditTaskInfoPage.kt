package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.ui.component.AppOutlinedTextField
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.util.defaultViewModel

@Composable
fun AddEditTaskInfoPage(
  taskId: Int?,
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(15.dp),
  ) {
    Spacer(Modifier.height(20.dp))
    Text(
      "Task Info",
      style = MaterialTheme.typography.h6,
      fontWeight = FontWeight.Bold,
    )
    Column(
      Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      println("AddEditTaskInfoPage column redraw")

      AppOutlinedTextField(
        label = "Task Name *",
        value = viewModel.taskName,
        validityFlow = viewModel.taskNameValid,
      )
      AppOutlinedTextField(
        label = "Default Priority",
        value = viewModel.defaultPriority,
        validityFlow = viewModel.defaultPriorityValid,
      )
      AppOutlinedTextField(
        label = "Description",
        value = viewModel.description,
        validityFlow = viewModel.descriptionValid,
      )
      /*
      OutlinedTextField(
        label = {
           Text("Task Name *")
        },
        value = viewModel.taskName
          .collectAsState("").value ?: "",
        onValueChange = {
          println("TaskName onChange it = $it")
          viewModel.taskName.value = it
        },
        isError = viewModel.taskNameValid
          .collectAsState(initial = false)
          .value
          .not()
      )
      OutlinedTextField(
        label = {
           Text("Default Priority")
        },
        value = viewModel.defaultPriority.value ?: "",
        onValueChange = {
          viewModel.defaultPriority.value = it
        },
      )
      OutlinedTextField(
        label = {
           Text("Description")
        },
        value = viewModel.description.value ?: "",
        onValueChange = {
          viewModel.description.value = it
        },
      )
       */
    }
  }
}