package sidev.app.android.sitracker.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import sidev.app.android.sitracker.R

val Nunito = FontFamily(
  Font(R.font.nunito_regular),
  Font(R.font.nunito_italic, style = FontStyle.Italic),

  Font(R.font.nunito_bold, weight = FontWeight.Bold),
  Font(R.font.nunito_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),

  Font(R.font.nunito_light, weight = FontWeight.Light),
  Font(R.font.nunito_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),

  Font(R.font.nunito_semi_bold, weight = FontWeight.SemiBold),
  Font(R.font.nunito_semi_bold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
)

val Arvo = FontFamily(
  Font(R.font.arvo_regular),
  Font(R.font.arvo_italic, style = FontStyle.Italic),

  Font(R.font.arvo_bold, weight = FontWeight.Bold),
  Font(R.font.arvo_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)