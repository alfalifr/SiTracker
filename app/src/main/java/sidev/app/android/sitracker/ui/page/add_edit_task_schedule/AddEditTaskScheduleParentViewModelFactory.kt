package sidev.app.android.sitracker.ui.page.add_edit_task_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sidev.app.android.sitracker.di.AndroidVmDi
import sidev.app.android.sitracker.di.VmDi
import sidev.app.android.sitracker.util.SuppressLiteral

class AddEditTaskScheduleParentViewModelFactory(
  private val vm: AddEditTaskScheduleViewModel
): ViewModelProvider.Factory {
  /**
   * Creates a new instance of the given `Class`.
   *
   *
   *
   * @param modelClass a `Class` whose instance is requested
   * @param <T>        The type parameter for the ViewModel.
   * @return a newly created ViewModel
  </T> */
  @Suppress(SuppressLiteral.UNCHECKED_CAST)
  override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
    modelClass.isAssignableFrom(AddEditTaskScheduleViewModel::class.java) -> vm
    else -> throw IllegalArgumentException(
      "Unknown `modelClass` of '$modelClass' in `AddEditTaskScheduleViewModelFactory`"
    )
  } as T
}