package sidev.app.android.sitracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.util.colorContrast

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)


val Red = Color(0xFFEA0C4B)
val GreenDark = Color(0xFF044532)
val GreenLight = Color(0xFF1EA141)
val GreenLighter = Color(0xFFA5FF33)
val Yellow = Color(0xFFFFF100)


val Grey1 = Color(0xFFDCDCDC)
val Grey2 = Color(0xFFBFBFBF)
val Grey3 = Color(0xFFA0A0A0)
val Grey4 = Color(0xFF868686)
val Grey5 = Color(0xFF656565)


val BlackTrans1 = Color(0xCE000000)
val BlackTrans2 = Color(0xA1000000)
val BlackTrans3 = Color(0x79000000)
val BlackTrans4 = Color(0x52000000)
val BlackTrans5 = Color(0x2C000000)
val BlackTrans6 = Color(0x0D000000)

val WhiteTrans1 = Color(0xCEFFFFFF)
val WhiteTrans2 = Color(0xA1FFFFFF)
val WhiteTrans3 = Color(0x79FFFFFF)
val WhiteTrans4 = Color(0x52FFFFFF)
val WhiteTrans5 = Color(0x2CFFFFFF)
val WhiteTrans6 = Color(0x0DFFFFFF)


val OppositeDark: Color
  @Composable
  get() = if(isSystemInDarkTheme()) Color.White else Color.Black

fun OppositeDark(isDark: Boolean): Color =
  if(isDark) Color.White else Color.Black


val FollowingDark: Color
  @Composable
  get() = if(isSystemInDarkTheme()) Color.Black else Color.White

fun FollowingDark(isDark: Boolean): Color =
  if(isDark) Color.Black else Color.White


val TransOppositeDarkColor1: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans1
  else BlackTrans1

fun TransOppositeDarkColor1(isDark: Boolean): Color =
  if(isDark) WhiteTrans1
  else BlackTrans1


val TransOppositeDarkColor2: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans2
  else BlackTrans2

fun TransOppositeDarkColor2(isDark: Boolean): Color =
  if(isDark) WhiteTrans2
  else BlackTrans2


val TransOppositeDarkColor3: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans3
  else BlackTrans3

fun TransOppositeDarkColor3(isDark: Boolean): Color =
  if(isDark) WhiteTrans3
  else BlackTrans3


val TransOppositeDarkColor4: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans4
  else BlackTrans4

fun TransOppositeDarkColor4(isDark: Boolean): Color =
  if(isDark) WhiteTrans4
  else BlackTrans4


val TransOppositeDarkColor5: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans5
  else BlackTrans5

fun TransOppositeDarkColor5(isDark: Boolean): Color =
  if(isDark) WhiteTrans5
  else BlackTrans5




val TransFollowingDarkColor1: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans1
  else WhiteTrans1

fun TransFollowingDarkColor1(isDark: Boolean): Color =
  if(isDark) BlackTrans1
  else WhiteTrans1


val TransFollowingDarkColor2: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans2
  else WhiteTrans2

fun TransFollowingDarkColor2(isDark: Boolean): Color =
  if(isDark) BlackTrans2
  else WhiteTrans2


val TransFollowingDarkColor3: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans3
  else WhiteTrans3

fun TransFollowingDarkColor3(isDark: Boolean): Color =
  if(isDark) BlackTrans3
  else WhiteTrans3


val TransFollowingDarkColor4: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans4
  else WhiteTrans4

fun TransFollowingDarkColor4(isDark: Boolean): Color =
  if(isDark) BlackTrans4
  else WhiteTrans4


val TransFollowingDarkColor5: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans5
  else WhiteTrans5

fun TransFollowingDarkColor5(isDark: Boolean): Color =
  if(isDark) BlackTrans5
  else WhiteTrans5


//*
fun OppositeBrightnessColor(color: Color): Color {
  /*
  val sum = with(color) {
    red + green + blue
  }
   */
  val contrast = colorContrast(
    rgb1 = Triple(0,0,0), //black
    rgb2 = Triple(
      (color.red * 255).toInt(),
      (color.green * 255).toInt(),
      (color.blue * 255).toInt(),
    )
  )
// minimal recommended contrast ratio is 4.5, or 3 for larger font-sizes

  return if(contrast >= 4.5) Color.Black
  else Color.White
}
// */

@Composable
@Preview
private fun OppositeBrightnessColor_preview() {
  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp)
  ) {
    @Composable
    fun ColorTile(color: Color) {
      Text(
        "r:${color.red} g:${color.green} b:${color.blue}",
        Modifier.background(color).
          padding(10.dp),
        color = OppositeBrightnessColor(color),
      )
    }

    ColorTile(
      Color(
        red = 15,
        green = 44,
        blue = 0,
        alpha = 255
      )
    )
    ColorTile(
      Color(
        red = 252,
        green = 168,
        blue = 143,
        alpha = 255
      )
    )

    ColorTile(
      Color(
        red = 255,
        green = 0,
        blue = 0,
      )
    )
    ColorTile(
      Color(
        red = 0,
        green = 255,
        blue = 0,
      )
    )
    ColorTile(
      Color(
        red = 0,
        green = 0,
        blue = 255,
      )
    )

    ColorTile(
      Color(
        red = 34,
        green = 172,
        blue = 29,
        alpha = 255
      )
    )
    ColorTile(
      Color(
        red = 37,
        green = 255,
        blue = 0,
        alpha = 255
      )
    )
    ColorTile(
      Color(
        red = 252,
        green = 214,
        blue = 30,
        alpha = 255
      )
    )
    ColorTile(
      Color(
        red = 55,
        green = 0,
        blue = 255,
        alpha = 255
      )
    )
    ColorTile(
      Color(
        red = 255,
        green = 251,
        blue = 0,
        alpha = 255
      )
    )
  }
}