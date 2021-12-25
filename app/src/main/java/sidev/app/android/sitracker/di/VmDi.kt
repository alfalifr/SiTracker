package sidev.app.android.sitracker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.AddEditTaskScheduleViewModel
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.schedule_info.AddEditScheduleInfoViewModel
import sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info.AddEditTaskInfoViewModel
import sidev.app.android.sitracker.ui.page.count_down.CountDownViewModel
import sidev.app.android.sitracker.ui.page.task_detail.TaskDetailViewModel
import sidev.app.android.sitracker.ui.page.main_menu.home.HomeViewModel
import sidev.app.android.sitracker.ui.page.main_menu.today_schedule.TodayScheduleViewModel
import sidev.app.android.sitracker.ui.page.schedule_detail.ScheduleDetailViewModel
import sidev.app.android.sitracker.ui.page.schedule_list.ScheduleListViewModel
import sidev.app.android.sitracker.util.SuppressLiteral

interface VmDi {
  fun homeViewModel(): HomeViewModel
  fun todayScheduleViewModel(): TodayScheduleViewModel
  fun taskDetailViewModel(): TaskDetailViewModel
  fun scheduleListViewModel(): ScheduleListViewModel
  fun scheduleDetailViewModel(): ScheduleDetailViewModel
  fun countDownViewModel(): CountDownViewModel
  fun addEditTaskScheduleViewModel(): AddEditTaskScheduleViewModel
  fun addEditTaskInfoViewModel(): AddEditTaskInfoViewModel
  fun addEditScheduleInfoViewModel(): AddEditScheduleInfoViewModel
}

interface AndroidVmDi: VmDi, ViewModelProvider.Factory


open class VmDiImpl(
  //private val diGraph: DiGraph,
  private val useCaseDi: UseCaseDi,
  private val uiUseCaseDi: UiUseCaseDi,
  protected open val coroutineScope: CoroutineScope? = null,
): AndroidVmDi {

  override fun homeViewModel(): HomeViewModel = HomeViewModel(
    queryUseCase = useCaseDi.queryUseCase(),
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    recommendationUseCase = useCaseDi.recommendationUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    dbEnumUseCase = useCaseDi.dbEnumUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun todayScheduleViewModel(): TodayScheduleViewModel =TodayScheduleViewModel(
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    queryUseCase = useCaseDi.queryUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    scheduleItemUseCase = useCaseDi.scheduleItemUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun taskDetailViewModel(): TaskDetailViewModel = TaskDetailViewModel(
    queryUseCase = useCaseDi.queryUseCase(),
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    calendarUseCase = useCaseDi.calendarUseCase(),
    calendarUiUseCase = uiUseCaseDi.calendarUiUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun scheduleListViewModel(): ScheduleListViewModel = ScheduleListViewModel(
    queryUseCase =  useCaseDi.queryUseCase(),
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun scheduleDetailViewModel(): ScheduleDetailViewModel = ScheduleDetailViewModel(
    queryUseCase = useCaseDi.queryUseCase(),
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    dbEnumUseCase = useCaseDi.dbEnumUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun countDownViewModel(): CountDownViewModel = CountDownViewModel(
    queryUseCase = useCaseDi.queryUseCase(),
    queryJointUseCase = useCaseDi.queryJointUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    scheduleProgressUseCase = useCaseDi.scheduleProgressUseCase(),
    coroutineScope = coroutineScope,
  )

  override fun addEditTaskScheduleViewModel(): AddEditTaskScheduleViewModel = AddEditTaskScheduleViewModel()

  override fun addEditTaskInfoViewModel(): AddEditTaskInfoViewModel = AddEditTaskInfoViewModel(
    formValidationUseCase = useCaseDi.formValidationUseCase(),
    iconUseCase = useCaseDi.iconUseCase()
  )

  override fun addEditScheduleInfoViewModel(): AddEditScheduleInfoViewModel = AddEditScheduleInfoViewModel()

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
    modelClass.isAssignableFrom(HomeViewModel::class.java) -> homeViewModel()
    modelClass.isAssignableFrom(TodayScheduleViewModel::class.java) -> todayScheduleViewModel()
    modelClass.isAssignableFrom(TaskDetailViewModel::class.java) -> taskDetailViewModel()
    modelClass.isAssignableFrom(ScheduleListViewModel::class.java) -> scheduleListViewModel()
    modelClass.isAssignableFrom(ScheduleDetailViewModel::class.java) -> scheduleDetailViewModel()
    modelClass.isAssignableFrom(CountDownViewModel::class.java) -> countDownViewModel()
    modelClass.isAssignableFrom(AddEditTaskScheduleViewModel::class.java) -> addEditTaskScheduleViewModel()
    modelClass.isAssignableFrom(AddEditTaskInfoViewModel::class.java) -> addEditTaskInfoViewModel()
    modelClass.isAssignableFrom(AddEditScheduleInfoViewModel::class.java) -> addEditScheduleInfoViewModel()
    else -> throw IllegalArgumentException("Unknown `modelClass` ($modelClass)")
  } as T
}