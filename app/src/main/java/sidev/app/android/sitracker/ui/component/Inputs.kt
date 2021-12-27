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


/**
 * [validity] value is `true` if the value inside this field is valid.
 * [errorMessage] only appears when it is not null and [validity] value is `true`.
 */
@Composable
fun AppOutlinedTextField(
  value: MutableStateFlow<String?>,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  label: (@Composable () -> Unit)? = null,
) {
  Column {
    val isValid = validity?.let {
      it.collectAsState(initial = false).value
    } ?: true

    OutlinedTextField(
      modifier = modifier,
      label = label,
      value = value.collectAsState("").value ?: "",
      onValueChange = {
        value.value = it
      },
      isError = !isValid,
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
  value: MutableStateFlow<String?>,
  modifier: Modifier = Modifier,
  validity: Flow<Boolean>? = null,
  errorMessage: String? = null,
  label: String,
) = AppOutlinedTextField(
  value = value,
  validity = validity,
  errorMessage = errorMessage,
  label = {
    Text(label)
  },
  modifier = modifier,
)