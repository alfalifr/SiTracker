package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.toColorInt

@ExperimentalGraphicsApi
@Composable
fun ColorPickerDialog(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  initColor: Color? = null,
  onColorSelected: (Color) -> Unit,
) {
  var selectedColor by remember { mutableStateOf<Color?>(null) }
  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = {
      Text(
        "Pick your color",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
      )
    },
    text = {
      ClassicColorPicker(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(.8f),
        color = initColor ?: Color.Red,
      ) {
        selectedColor = it.toColor() //Color(it.toColorInt())
      }
    },
    buttons = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Button(
          onClick = onDismiss,
        ) {
          Text("Cancel")
        }
        Spacer(Modifier.width(10.dp))
        Button(
          onClick = {
            if(
              selectedColor != null
              && selectedColor != initColor
            ) {
              onColorSelected(selectedColor!!)
            }
            onDismiss()
          },
          enabled = selectedColor != null,
        ) {
          Text("Select")
        }
      }
    },
  )
}