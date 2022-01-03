@file:OptIn(
  ExperimentalGraphicsApi::class,
)
package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.core.domain.model.IconPicData
import sidev.app.android.sitracker.ui.component.AppOutlinedTextField
import sidev.app.android.sitracker.ui.component.IconColorMode
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.ui.theme.OppositeBrightnessColor
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.util.*

@ExperimentalFoundationApi
@Composable
fun AddEditTaskInfoPage(
  taskId: Int?,
  navController: NavController = rememberNavController(),
  viewModel: AddEditTaskScheduleViewModel = defaultViewModel(),
) {
  LaunchedEffect(key1 = Unit) {
    delay(500)
    //println("AddEditTaskInfoPage LaunchedEffect")
    viewModel.randomTaskColor()
    if(taskId != null) {
      viewModel.readExistingTask(taskId)
    }
  }

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

    var isIconDialogShown by remember { mutableStateOf(false) }
    var isColorPickerDialogShown by remember { mutableStateOf(false) }
    println("isIconDialogShown = $isIconDialogShown iconData = $iconData")

    if(isIconDialogShown) {
      val allIcon = viewModel.allAvailableIcons
        .collectAsState(initial = null)
        .value
      IconSelectionDialog(
        modifier = Modifier.fillMaxSize(.8f),
        icons = allIcon,
        initIcon = viewModel.selectedIcon.collectAsState().value,
        onItemSelected = {
          viewModel.selectedIcon.value = it
          isIconDialogShown = false
        },
        onDismiss = {
          isIconDialogShown = false
        },
      )
    }
    if(isColorPickerDialogShown) {
      //@SuppressLint("StateFlowValueCalledInComposition")
      ColorPickerDialog(
        modifier = Modifier.fillMaxSize(.8f),
        initColor = viewModel.taskColor.collectAsState().value,
        onDismiss = {
          isColorPickerDialogShown = false
        },
        onColorSelected = {
          viewModel.taskColor.value = it
        },
      )
    }

    IconDisplay(
      iconData, taskName,
      onIconItemClick = {
        isIconDialogShown = true
      },
      onColorItemClick = {
        isColorPickerDialogShown = true
      },
    )

    Column(
      Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      //println("AddEditTaskInfoPage column redraw")

      AppOutlinedTextField(
        label = "Task Name *",
        value = viewModel.taskName,
        validity = viewModel.taskNameValid,
        errorMessage = "Length must be at least 3",
      )
      AppOutlinedTextField(
        label = "Default Priority",
        value = viewModel.defaultPriority,
        validity = viewModel.defaultPriorityValid,
        errorMessage = "Must be integer",
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

    val pagesMask = viewModel.pagesMask
      .collectAsState()
      .value

    println("AddEditTaskInfoPage pagesMask = $pagesMask")

    if(pagesMask.hasOtherMaskThan(AddEditTaskScheduleViewModel.TASK_INFO_MASK)) {
      val scheduleId = viewModel.scheduleId
        .collectAsState(initial = null)
        .value
      Box(Modifier.fillMaxWidth()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(10.dp)
            .clickable {
              Route.AddEditScheduleInfoPage.go(
                navController, scheduleId,
              )
            }
        ) {
          Text(Texts.next)
          Spacer(Modifier.width(10.dp))
          Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = OppositeDark,
          )
        }
      }
    }
  }
}

@Composable
private fun IconDisplay(
  iconData: IconPicData?,
  taskName: String?,
  onIconItemClick: (() -> Unit)? = null,
  onColorItemClick: (() -> Unit)? = null,
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
      iconMode = IconColorMode.COLORED_BG,
      modifier = Modifier
        .size(Const.iconSizeDp * 1.9f).let {
          if(onIconItemClick != null) it.clickable {
            onIconItemClick()
          } else it
        },
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
        onClick = onIconItemClick,
      )
      IconItemField(
        text = "Color",
        cardColor = iconData?.color?.let { Color(it) }
          ?: OppositeDark,
        contentDescription = null,
        cardStrokeColor = OppositeDark,
        onClick = onColorItemClick,
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
  onClick: (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.let {
      if(onClick != null) it.clickable(onClick = onClick)
      else it
    },
  ) {
    val cardSize = 30.dp
    Card(
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.size(cardSize),
      backgroundColor = cardColor,
      border = cardStrokeColor?.let {
        BorderStroke(
          width = 3.dp,
          color = it
        )
      },
    ) {
      if(iconResId != null) {
        Icon(
          painter = painterResource(id = iconResId),
          contentDescription = contentDescription,
          tint = iconColor ?: OppositeBrightnessColor(cardColor),
          modifier = Modifier
            .size(cardSize - 20.dp)
            .padding(5.dp),
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