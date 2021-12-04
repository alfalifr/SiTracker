package sidev.app.android.sitracker.ui.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import sidev.app.android.sitracker.util.Texts

/**
 * Function that replaces [content] with [placeholder]
 * if [key] is null, and otherwise.
 */
@Composable
fun <T> Placeholder(
  key: T?,
  placeholder: (@Composable () -> Unit)?,
  precondition: ((T) -> Boolean)? = null,
  content: @Composable (T) -> Unit,
) {
  if(key == null || precondition?.invoke(key) == false) {
    placeholder?.invoke()
  } else {
    content(key)
  }
}


@SuppressLint("ModifierParameter")
@Composable
fun <T> LoadingPlaceholder(
  key: T?,
  loadingModifier: Modifier = Modifier,
  loadingText: String? = Texts.defaultLoadingText,
  loadingSpaceBetween: Dp? = null,
  precondition: ((T) -> Boolean)? = null,
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
  precondition = precondition,
  content = content,
)


/**
 * A placeholder that simply hide the [content]
 * when [key] is null.
 */
@Composable
fun <T> EmptyPlacehoder(
  key: T?,
  precondition: ((T) -> Boolean)? = null,
  content: @Composable (T) -> Unit,
) = Placeholder(
  key = key,
  placeholder = null,
  precondition = precondition,
  content = content,
)