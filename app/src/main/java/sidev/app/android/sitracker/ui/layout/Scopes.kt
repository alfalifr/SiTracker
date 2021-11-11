@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.layout

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import sidev.app.android.sitracker.ui.component.HorizontalSlidingTransition
import sidev.app.android.sitracker.ui.nav.ComposableNavData
import sidev.app.android.sitracker.ui.nav.Route
import sidev.app.android.sitracker.util.model.Direction

interface MainScaffoldScope: LazyListScope {
  fun animatedHorizontalSliding(
    slidingDirection: Direction,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
  ) {
    item {
      HorizontalSlidingTransition(
        slidingDirection = slidingDirection,
        content = content,
      )
    }
  }
  fun animatedHorizontalSlidings(
    count: Int,
    slidingDirection: Direction,
    key: ((Int) -> Unit)? = null,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit,
  ) {
    items(
      count = count,
      key = key,
    ) { i ->
      HorizontalSlidingTransition(
        slidingDirection = slidingDirection,
        content = { content(i) },
      )
    }
  }
}

interface MainMenuContentScope: MainScaffoldScope {
  val index: Int
  val navComposableData: ComposableNavData

  //TODO: Sliding effect from item #2 to item #1 still get left direction (expected is right).
  // perhaps it has something to do with `popUpTo` in BottomNavBar navigation
  fun getSlidingDirection(
    prevRouteString: String?,
  ): Direction {
    val prevRoute =
      if(prevRouteString == null) null
      else Route.getMainMenuContentRoutes()
        .find { it.completeRoute == prevRouteString }

    return if(prevRoute?.let { it.index > index } != false) {
      Direction.LEFT
    } else Direction.RIGHT
  }

  fun mainMenuItem(
    //navComposableData: ComposableNavData,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
  ) {
    animatedHorizontalSliding(
      slidingDirection = getSlidingDirection(
        navComposableData.prevNavBackStackEntry?.destination?.route
      ),
      content = content,
    )
  }

  fun mainMenuItems(
    count: Int,
    //navComposableData: ComposableNavData,
    key: ((Int) -> Unit)? = null,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit,
  ) {
    animatedHorizontalSlidings(
      count = count,
      key = key,
      slidingDirection = getSlidingDirection(
        navComposableData.prevNavBackStackEntry?.destination?.route
      ),
      content = content,
    )
  }
}

private class MainScaffoldScopeImpl(
  private val lazyListScope: LazyListScope,
): MainScaffoldScope, LazyListScope by lazyListScope

private class MainMenuContentScopeImpl(
  override val index: Int,
  override val navComposableData: ComposableNavData,
  private val lazyListScope: LazyListScope,
): MainMenuContentScope, LazyListScope by lazyListScope


fun LazyListScope.toMainScaffoldScope(): MainScaffoldScope =
  MainScaffoldScopeImpl(this)

fun LazyListScope.toMainMenContentScope(
  index: Int,
  navComposableData: ComposableNavData,
): MainMenuContentScope =
  MainMenuContentScopeImpl(
    index = index,
    navComposableData = navComposableData,
    lazyListScope = this,
  )