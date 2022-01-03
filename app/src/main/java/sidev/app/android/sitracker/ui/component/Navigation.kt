package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import sidev.app.android.sitracker.R

@Composable
fun NextPrevNav(
  prevText: String?,
  nextText: String?,
  modifier: Modifier = Modifier.fillMaxWidth(),
  withPrevIcon: Boolean = true,
  withNextIcon: Boolean = true,
  onPrevClick: (() -> Unit)? = null,
  onNextClick: (() -> Unit)? = null,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    @Composable
    fun InnerButton(
      text: String,
      isNext: Boolean,
      modifier: Modifier = Modifier,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
      ) {
        if(withPrevIcon && !isNext) {
          Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "",
            Modifier.rotate(180f),
          )
        }
        Text(text)
        if(withNextIcon && isNext) {
          Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "",
          )
        }
      }
    }

    if(prevText != null) {
      InnerButton(
        text = prevText,
        isNext = false,
        modifier = Modifier.let {
          if(onPrevClick == null) it
          else it.clickable(onClick = onPrevClick)
        },
      )
    }
    if(nextText != null) {
      InnerButton(
        text = nextText,
        isNext = true,
        modifier = Modifier.let {
          if(onNextClick == null) it
          else it.clickable(onClick = onNextClick)
        },
      )
    }
  }
}