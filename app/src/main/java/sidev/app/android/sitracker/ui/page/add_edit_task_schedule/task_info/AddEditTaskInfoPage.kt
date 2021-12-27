package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import sidev.app.android.sitracker.core.domain.model.IconPicData
import sidev.app.android.sitracker.ui.component.AppOutlinedTextField
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.ui.theme.OppositeBrightnessColor
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.util.Color
import sidev.app.android.sitracker.util.Const
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

    val iconData = viewModel.taskIconData
      .collectAsState(initial = null)
      .value
    val taskName = viewModel.taskName
      .collectAsState(initial = null)
      .value
    IconDisplay(iconData, taskName)

    Column(
      Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      println("AddEditTaskInfoPage column redraw")

      val blankStringMsg = "Length must be at least 3"

      AppOutlinedTextField(
        label = "Task Name *",
        value = viewModel.taskName,
        validity = viewModel.taskNameValid,
        errorMessage = blankStringMsg,
      )
      AppOutlinedTextField(
        label = "Default Priority",
        value = viewModel.defaultPriority,
        validity = viewModel.defaultPriorityValid,
        errorMessage = blankStringMsg,
      )
      AppOutlinedTextField(
        label = "Description",
        value = viewModel.description,
        validity = viewModel.descriptionValid,
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

@Composable
private fun IconDisplay(
  iconData: IconPicData?,
  taskName: String?,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(15.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconProgressionPic(
      icon = iconData?.resId?.let { painterResource(id = it) }
        ?: ColorPainter(Color.Transparent),
      mainColor = iconData?.color?.let { Color(it) }
        ?: OppositeDark,
      name = taskName,
      modifier = Modifier.size(Const.iconSizeDp * 1.9f),
    )
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      IconItemField(
        text = "Icon",
        cardColor = OppositeDark,
        contentDescription = null,
        iconResId = iconData?.resId,
      )
      IconItemField(
        text = "Color",
        cardColor = iconData?.color?.let { Color(it) }
          ?: OppositeDark,
        contentDescription = null,
        cardStrokeColor = OppositeDark,
      )
    }
  }
}

@Composable
private fun IconItemField(
  text: String,
  cardColor: Color,
  contentDescription: String?,
  cardStrokeColor: Color? = null,
  iconColor: Color? = null,
  @DrawableRes iconResId: Int? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val cardSize = 30.dp
    Card(
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.size(cardSize),
      backgroundColor = cardColor,
      border = cardStrokeColor?.let {
        BorderStroke(
          width = 10.dp,
          color = it
        )
      },
    ) {
      if(iconResId != null) {
        Icon(
          painter = painterResource(id = iconResId),
          contentDescription = contentDescription,
          tint = iconColor ?: OppositeBrightnessColor(cardColor),
          modifier = Modifier.size(cardSize - 10.dp),
        )
      }
    }
    Spacer(Modifier.width(10.dp))
    Text(
      text,
      fontWeight = FontWeight.Bold,
    )
  }
}