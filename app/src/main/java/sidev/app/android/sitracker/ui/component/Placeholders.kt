package sidev.app.android.sitracker.ui.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import sidev.app.android.sitracker.util.Const

/**
 * Function that replaces [content] with [placeholder]
 * if [key] is null, and otherwise.
 */
@Composable
fun <T> Placeholder(
  key: T?,
  placeholder: @Composable () -> Unit,
  content: @Composable (T) -> Unit,
) {
  if(key == null) {
    placeholder()
  } else {
    content(key)
  }
}


@SuppressLint("ModifierParameter")
@Composable
fun <T> LoadingPlaceholder(
  key: T?,
  loadingModifier: Modifier = Modifier,
  loadingText: String? = Const.defaultLoadingText,
  loadingSpaceBetween: Dp? = null,
  content: @Composable (T) -> Unit,
) = Placeholder(
  key = key,
  placeholder = {
    DefaultLoading(
      modifier = loadingModifier,
      text = loadingText,
      spaceBetween = loadingSpaceBetween,
    )
  },
  content = content,
)