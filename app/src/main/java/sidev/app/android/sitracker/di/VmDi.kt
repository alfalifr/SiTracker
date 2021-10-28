package sidev.app.android.sitracker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import sidev.app.android.sitracker.ui.page.main_menu.home.HomeViewModel
import sidev.app.android.sitracker.util.SuppressLiteral

interface VmDi {
  fun homeViewModel(): HomeViewModel
}

interface AndroidVmDi: VmDi, ViewModelProvider.Factory


open class VmDiImpl(
  //private val diGraph: DiGraph,
  private val useCaseDi: UseCaseDi,
  protected open val coroutineScope: CoroutineScope? = null,
): AndroidVmDi {

  override fun homeViewModel(): HomeViewModel = HomeViewModel(
    recommendationUseCase = useCaseDi.recommendationUseCase(),
    iconUseCase = useCaseDi.iconUseCase(),
    coroutineScope = coroutineScope,
  )

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
    else -> throw IllegalArgumentException("Unknown `modelClass` ($modelClass)")
  } as T
}