package sidev.app.android.sitracker.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope

interface DiGraph {
  fun daoDi(): DaoDi
  fun useCaseDi(): UseCaseDi
  fun uiUseCaseDi(): UiUseCaseDi
  fun vmDi(): AndroidVmDi
}

class DiGraphImpl(
  private val context: Context,
  private val coroutineScope: CoroutineScope? = null,
): DiGraph {
  override fun daoDi(): DaoDi = DaoDiImpl(context)
  override fun useCaseDi(): UseCaseDi = UseCaseDiImpl(daoDi())
  override fun uiUseCaseDi(): UiUseCaseDi = UiUseCaseDiImpl()
  override fun vmDi(): AndroidVmDi = VmDiImpl(
    useCaseDi = useCaseDi(),
    uiUseCaseDi = uiUseCaseDi(),
    coroutineScope = coroutineScope,
  )
}