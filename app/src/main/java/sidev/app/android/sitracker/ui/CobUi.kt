package sidev.app.android.sitracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
@Preview
fun AutoScaleText_preview() {
  if(LocalInspectionMode.current) {
    AutoScaleText(
      text = "Hello ges",
      modifier = Modifier
        .size(
          height = Dp.Unspecified,
          width = 70.dp,
        ).background(
          color = Color.Blue
        )
    )
  }
}


@Composable
fun AutoScaleText(
  text: String,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = TextStyle.Default,
) {
  var txtStyleState by remember { mutableStateOf(textStyle) }
  var readyToDraw by remember { mutableStateOf(false) }

  Text(
    text = text,
    style = txtStyleState,
    modifier = modifier.drawWithContent {
      if(readyToDraw) {
        drawContent()
      }
    },
    softWrap = false,
    onTextLayout = {
      if(it.didOverflowWidth) {
        txtStyleState = txtStyleState.copy(
          fontSize = txtStyleState.fontSize * .9,
        )
      } else {
        readyToDraw = true
      }
    }
  )
}