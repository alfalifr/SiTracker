@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.layout

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import sidev.app.android.sitracker.ui.component.SlidingTransition
import sidev.app.android.sitracker.util.model.Direction

interface MainScaffoldScope: LazyListScope {
  fun animatedSliding(
    slidingDirection: Direction,
    content: @Composable LazyAnimatedItemScope.() -> Unit,
  ) {
    item {
      SlidingTransition(
        slidingDirection = slidingDirection,
        content = {
           lazyAnimatedItemScope(
             this@item, this,
           ).content()
        },
      )
    }
  }

  fun animatedSlidings(
    count: Int,
    slidingDirection: Direction,
    key: ((Int) -> Unit)? = null,
    content: @Composable LazyAnimatedItemScope.(Int) -> Unit,
  ) {
    items(
      count = count,
      key = key,
    ) { i ->
      SlidingTransition(
        slidingDirection = slidingDirection,
        content = {
          lazyAnimatedItemScope(
            this@items, this,
          ).content(i)
        },
      )
    }
  }
}

interface MainMenuContentScope: MainScaffoldScope {
  val index: Int
  val prevIndex: Int?
  //val navComposableData: ComposableNavData

  //TODO: Sliding effect from item #2 to item #1 still get left direction (expected is right).
  // perhaps it has something to do with `popUpTo` in BottomNavBar navigation
  fun getSlidingDirection(
    //prevRouteString: String?,
    //prevIndex: Int,
  ): Direction {
    println("getSlidingDirection prevIndex= $prevIndex index= $index")
    return if(prevIndex != null) {
      if(prevIndex!! > index) Direction.RIGHT
      else Direction.LEFT
    } else Direction.UP
  }
  /*
  {
    val prevRoute =
      if(prevRouteString == null) null
      else Route.getMainMenuContentRoutes()
        .find { it.completeRoute == prevRouteString }

    return if(prevRoute?.let { it.index > index } != false) {
      Direction.RIGHT
    } else Direction.LEFT
  }
   */

  fun mainMenuItem(
    //navComposableData: ComposableNavData,
    content: @Composable LazyAnimatedItemScope.() -> Unit,
  ) {
    animatedSliding(
      slidingDirection = getSlidingDirection(
        //navComposableData.prevNavBackStackEntry?.destination?.route
      ),
      content = content,
    )
  }

  fun mainMenuItems(
    count: Int,
    //navComposableData: ComposableNavData,
    key: ((Int) -> Unit)? = null,
    content: @Composable LazyAnimatedItemScope.(Int) -> Unit,
  ) {
    animatedSlidings(
      count = count,
      key = key,
      slidingDirection = getSlidingDirection(
        //navComposableData.prevNavBackStackEntry?.destination?.route
      ),
      content = content,
    )
  }
}

interface LazyAnimatedItemScope: LazyItemScope, AnimatedVisibilityScope


private class MainScaffoldScopeImpl(
  private val lazyListScope: LazyListScope,
): MainScaffoldScope, LazyListScope by lazyListScope

private class MainMenuContentScopeImpl(
  override val index: Int,
  //override val navComposableData: ComposableNavData,
  override val prevIndex: Int?,
  private val lazyListScope: LazyListScope,
): MainMenuContentScope, LazyListScope by lazyListScope


private class LazyAnimatedItemScopeImpl(
  private val lazyItemScope: LazyItemScope,
  private val animatedVisibilityScope: AnimatedVisibilityScope,
): LazyAnimatedItemScope,
  LazyItemScope by lazyItemScope,
  AnimatedVisibilityScope by animatedVisibilityScope


fun LazyListScope.toMainScaffoldScope(
): MainScaffoldScope = MainScaffoldScopeImpl(this)


/*
fun MainScaffoldScope.toMainMenuContentScope(
  index: Int,
  navComposableData: ComposableNavData,
): MainMenuContentScope = toMainMenuContentScope(
  index = index,
  navComposableData = navComposableData,
)
 */

fun LazyListScope.toMainMenuContentScope(
  index: Int,
  prevIndex: Int?,
  //navComposableData: ComposableNavData,
): MainMenuContentScope =
  MainMenuContentScopeImpl(
    index = index,
    prevIndex = prevIndex,
    //navComposableData = navComposableData,
    lazyListScope = this,
  )

fun lazyAnimatedItemScope(
  lazyItemScope: LazyItemScope,
  animatedVisibilityScope: AnimatedVisibilityScope
): LazyAnimatedItemScope = LazyAnimatedItemScopeImpl(
  lazyItemScope, animatedVisibilityScope,
)