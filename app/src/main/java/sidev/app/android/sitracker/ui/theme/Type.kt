package sidev.app.android.sitracker.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
// Set of Material typography styles to start with
val Typography = Typography(
  body1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
  )
  /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)
 */


// Set of Material typography styles to start with
val LightTypography = Typography(
  defaultFontFamily = Nunito,
  body1 = TextStyle(
    //fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = Color.Black,
  ),
  /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val TextStyle.dark: TextStyle
  get() = copy(
    fontFamily = DefaultFontFamily,
    color = Color.White,
  )
val TextStyle.light: TextStyle
  get() = copy(
    fontFamily = DefaultFontFamily,
    color = Color.Black,
  )


val DarkTypography: Typography
  @Composable
  get() = Typography(
    defaultFontFamily = DefaultFontFamily,
    /*
    body1 = DefaultDarkTextStyle.copy(
      //fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
    ),
     */
    body1 = MaterialTheme.typography.body1.dark,
    body2 = MaterialTheme.typography.body2.dark,
    h1 = MaterialTheme.typography.h1.dark,
    h2 = MaterialTheme.typography.h2.dark,
    h3 = MaterialTheme.typography.h3.dark,
    h4 = MaterialTheme.typography.h4.dark,
    h5 = MaterialTheme.typography.h5.dark,
    h6 = MaterialTheme.typography.h6.dark,
  )