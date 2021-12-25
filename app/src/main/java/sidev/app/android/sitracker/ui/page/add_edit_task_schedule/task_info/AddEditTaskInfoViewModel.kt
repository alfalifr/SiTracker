package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import sidev.app.android.sitracker.core.domain.model.AppIcon
import sidev.app.android.sitracker.core.domain.usecase.FormValidationUseCase
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.colorInt
import sidev.app.android.sitracker.util.getHexString

class AddEditTaskInfoViewModel(
  private val formValidationUseCase: FormValidationUseCase,
  private val iconUseCase: IconUseCase,
): ViewModel() {

  val allAvailableIcons: Flow<List<AppIcon>> = flow {
    emit(iconUseCase.getAllAvailableIcons())
  }
  val selectedIcon = MutableStateFlow<AppIcon?>(null)

  val taskColor = MutableStateFlow<Color?>(null)
  private val taskColorStr: Flow<String?> = taskColor.map { color ->
    color?.colorInt?.let {
      getHexString(it)
    }
  }

  val taskNameInput = MutableStateFlow<String?>(null)
  val taskNameMsg: Flow<Boolean> = taskNameInput.map {
    formValidationUseCase.validateStrLen(it)
  }

  val defaultPriorityInput = MutableStateFlow<String?>(null)
  val defaultPriorityMsg: Flow<Boolean> = defaultPriorityInput.map {
    formValidationUseCase.validateNumeric(it)
  }

  val descriptionInput = MutableStateFlow<String?>(null)
  val descriptionMsg: Flow<Boolean> = descriptionInput.map {
    true // cuz this field is optional.
  }
}