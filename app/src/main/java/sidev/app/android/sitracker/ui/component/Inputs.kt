package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow


/**
 * [validity] value is `true` if the value inside this field is valid.
 * [errorMessage] only appears when it is not null and [validity] value is `true`.
 */
@Composable
fun AppOutlinedTextField(
  value: Flow<String?>,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  label: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
) {
  Column {
    val isValid = validity?.let {
      it.drop(1)
        .collectAsState(initial = true)
        .value
    } ?: true

    OutlinedTextField(
      modifier = modifier,
      label = label,
      value = value.collectAsState("").value ?: "",
      onValueChange = {
        if(value is MutableStateFlow) {
          value.value = it
        }
      },
      isError = !isValid,
      trailingIcon = trailingIcon,
      leadingIcon = leadingIcon,
      enabled = value is MutableStateFlow
    )
    if(!isValid && errorMessage != null) {
      Text(
        errorMessage,
        color = MaterialTheme.colors.error,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.padding(
          top = 5.dp,
          start = 5.dp,
        ),
      )
    }
  }
}

@Composable
fun AppOutlinedTextField(
  value: Flow<String?>,
  label: String,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
) = AppOutlinedTextField(
  value = value,
  validity = validity,
  errorMessage = errorMessage,
  label = {
    Text(label)
  },
  modifier = modifier,
  leadingIcon = leadingIcon,
  trailingIcon = trailingIcon,
)

@Composable
fun AppOutlinedTextField(
  value: String,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
) = AppOutlinedTextField(
  value = flow { emit(value) },
  validity = validity,
  errorMessage = errorMessage,
  label = label,
  modifier = modifier,
  leadingIcon = leadingIcon,
  trailingIcon = trailingIcon,
)

@Composable
fun AppOutlinedTextField(
  value: String,
  label: String,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
) = AppOutlinedTextField(
  value = value,
  validity = validity,
  errorMessage = errorMessage,
  label = { Text(label) },
  modifier = modifier,
  leadingIcon = leadingIcon,
  trailingIcon = trailingIcon,
)