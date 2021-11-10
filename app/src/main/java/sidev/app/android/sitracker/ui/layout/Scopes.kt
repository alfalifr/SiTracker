package sidev.app.android.sitracker.ui.layout

import androidx.compose.foundation.lazy.LazyListScope

interface MainScaffoldScope: LazyListScope

private class MainScaffoldScopeImpl(
  private val lazyListScope: LazyListScope,
): MainScaffoldScope, LazyListScope by lazyListScope


fun LazyListScope.toMainScaffoldScope(): MainScaffoldScope =
  MainScaffoldScopeImpl(this)