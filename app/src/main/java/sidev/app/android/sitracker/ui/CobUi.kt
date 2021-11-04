package sidev.app.android.sitracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout


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
        )
        .background(
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


@Composable
@Preview
fun ConstraintLayout_preview() {
  ConstraintLayout(
    Modifier.width(100.dp)
  ) {
    val right = createRef()
    Text(
      "Kanan",
      Modifier.constrainAs(right) {
        this.end.linkTo(parent.end)
      }
    )

    val left = createRef()

    Text(text = "kiri",
      Modifier.constrainAs(left) {
        start.linkTo(parent.start)
      }
    )
  }
}


@Composable
@Preview
fun ExceededColumn_preview() {
  BoxWithConstraints {
    val screenHeight = maxHeight
    val scrollState = rememberScrollState()
    //scrollState.maxValue = 1
    println("scrollState.value = ${scrollState.value} scrollState.maxValue = ${scrollState.maxValue}")
    Column(
      Modifier
        //.height(50.dp)
        .verticalScroll(scrollState)
    ) {
      Text("scrollState.maxValue = ${scrollState.maxValue}",
        Modifier
          .height(screenHeight * 0.8f)
          .background(Color.Red)
      )
      Text("Helo bro",
        Modifier
          .height(screenHeight * 0.5f)
          .background(Color.Blue)
      )
    }
  }
}


@Composable
@Preview
fun ExceededColumnWithLayout_preview() {
  val scrollState = rememberScrollState()
  Layout(
    modifier = Modifier
      .height(50.dp)
      .verticalScroll(scrollState),
    content = {
      Text("scrollState.maxValue = ${scrollState.maxValue}",
        Modifier
          .height(30.dp)
          .background(Color.Red)
      )
      Text("Helo bro",
        Modifier
          .height(30.dp)
          .background(Color.Blue)
      )
    },
    measurePolicy = { measurables, constraints ->
      val place1 = measurables[0].measure(constraints)
      val place2 = measurables[1].measure(constraints)

      layout(
        width = constraints.maxWidth,
        height = constraints.maxHeight,
      ) {
        place1.place(0, 0)
        place2.place(0, place1.height)
      }
    }
  )
}