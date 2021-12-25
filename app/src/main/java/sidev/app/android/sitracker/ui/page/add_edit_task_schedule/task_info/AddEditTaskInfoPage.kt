package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.util.defaultViewModel

@Composable
fun AddEditTaskInfoPage(
  taskId: Int?,
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskInfoViewModel = defaultViewModel(),
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
      TextField(
        label = {
           Text("Task Name *")
        },
        value = "",
        onValueChange = { s: String -> },
      )
      TextField(
        label = {
           Text("Default Priority")
        },
        value = "",
        onValueChange = { s: String -> },
      )
      TextField(
        label = {
           Text("Description")
        },
        value = "",
        onValueChange = { s: String -> },
      )
    }
  }
}