package sidev.app.android.sitracker.ui.component

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * [validityFlow] value is `true` if the value inside this field is valid.
 */
@Composable
fun AppOutlinedTextField(
  value: MutableStateFlow<String?>,
  modifier: Modifier = Modifier,
  validityFlow: Flow<Boolean>? = null,
  label: (@Composable () -> Unit)? = null,
) {
  OutlinedTextField(
    modifier = modifier,
    label = label,
    value = value.collectAsState("").value ?: "",
    onValueChange = {
      value.value = it
    },
    isError = validityFlow?.let {
      it.collectAsState(initial = false)
        .value
        .not()
    } ?: false,
  )
}

@Composable
fun AppOutlinedTextField(
  value: MutableStateFlow<String?>,
  modifier: Modifier = Modifier,
  validityFlow: Flow<Boolean>? = null,
  label: String,
) = AppOutlinedTextField(
  value = value,
  validityFlow = validityFlow,
  label = {
    Text(label)
  },
  modifier = modifier,
)