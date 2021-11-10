package sidev.app.android.sitracker.util.dummy

import kotlinx.coroutines.CoroutineScope
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.di.*
import sidev.app.android.sitracker.util.dummy.dao.*

object DiGraphDummy: DiGraph {
  var coroutineScope: CoroutineScope? = null

  override fun daoDi(): DaoDi = DaoDiDummy
  override fun useCaseDi(): UseCaseDi = UseCaseDiImpl(daoDi())
  override fun uiUseCaseDi(): UiUseCaseDi = UiUseCaseDiImpl()
  override fun vmDi(): AndroidVmDi = VmDiDummy
}

object DaoDiDummy: DaoDi {
  override fun taskDao(): TaskDao = TaskDaoDummy
  override fun scheduleDao(): ScheduleDao = ScheduleDaoDummy
  override fun activeDateDao(): ActiveDateDao = ActiveDateDaoDummy
  override fun preferredTimeDao(): PreferredTimeDao = PreferredTimeDaoDummy
  override fun preferredDayDao(): PreferredDayDao = PreferredDayDaoDummy
  override fun scheduleProgressDao(): ScheduleProgressDao = ScheduleProgressDaoDummy
  override fun intervalDao(): IntervalDao = IntervalDaoDummy
  override fun progressTypeDao(): ProgressTypeDao = ProgressTypeDaoDummy
}

object VmDiDummy: VmDiImpl(
  useCaseDi = DiGraphDummy.useCaseDi(),
  uiUseCaseDi = DiGraphDummy.uiUseCaseDi(),
) {
  override val coroutineScope: CoroutineScope?
    get() {
      println("VmDiDummy.coroutineScope DiGraphDummy.coroutineScope = ${DiGraphDummy.coroutineScope}")
      return DiGraphDummy.coroutineScope
    }
}