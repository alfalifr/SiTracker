package sidev.app.android.sitracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

val FollowingDark: Color
  @Composable
  get() = if(isSystemInDarkTheme()) Color.Black else Color.White


val TransOppositeDarkColor1: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans1
  else BlackTrans1

val TransOppositeDarkColor2: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans2
  else BlackTrans2

val TransOppositeDarkColor3: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans3
  else BlackTrans3

val TransOppositeDarkColor4: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans4
  else BlackTrans4

val TransOppositeDarkColor5: Color
  @Composable
  get() = if(isSystemInDarkTheme()) WhiteTrans5
  else BlackTrans5


val TransFollowingDarkColor1: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans1
  else WhiteTrans1

val TransFollowingDarkColor2: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans2
  else WhiteTrans2

val TransFollowingDarkColor3: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans3
  else WhiteTrans3

val TransFollowingDarkColor4: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans4
  else WhiteTrans4

val TransFollowingDarkColor5: Color
  @Composable
  get() = if(isSystemInDarkTheme()) BlackTrans5
  else WhiteTrans5
