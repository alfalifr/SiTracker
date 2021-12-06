package sidev.app.android.sitracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import sidev.app.android.sitracker.ui.component.SystemOverlay

private val DarkColorPalette = darkColors(
  primary = GreenLight,
  primaryVariant = GreenDark,
  secondary = Yellow,
  background = Color(0xFF2C2C2C),
  surface = Color.Black,
)

private val LightColorPalette = lightColors(
  primary = GreenLight,
  primaryVariant = GreenDark,
  secondary = Yellow,
  background = Color(0xFFDFDFDF),
  surface = Color.White,

  /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  statusBarColor: Color = BlackTrans4,
  navBarColor: Color = BlackTrans4,
  content: @Composable (PaddingValues) -> Unit
) {
  val colors = if(darkTheme) {
    DarkColorPalette
  } else {
    LightColorPalette
  }

  val typography = if(darkTheme) DarkTypography
  else LightTypography

  MaterialTheme(
    colors = colors,
    typography = typography,
    shapes = Shapes,
  ) {
    ProvideWindowInsets {
      SystemOverlay(
        statusBarColor = statusBarColor,
        navBarColor = navBarColor,
        statusBarUseDarkIcon = false,
        navBarUseDarkIcon = false,
      )
      content(
        rememberInsetsPaddingValues(
          insets = LocalWindowInsets.current.systemBars,
        )
      )
    }
  }
}