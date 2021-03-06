package sidev.app.android.sitracker.ui.page.add_edit_task_schedule.task_info

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.core.domain.model.AppIcon
import sidev.app.android.sitracker.ui.component.IconColorMode
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.component.LoadingPlaceholder
import sidev.app.android.sitracker.ui.theme.OppositeDark
import sidev.app.android.sitracker.util.Const

@ExperimentalFoundationApi
@Composable
fun IconSelectionDialog(
  icons: List<AppIcon>?,
  modifier: Modifier = Modifier,
  initIcon: AppIcon? = null,
  color: Color = OppositeDark,
  onItemSelected: ((AppIcon) -> Unit)? = null,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = {
      Text(
        "Pick an Icon",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        //color = MaterialTheme.,
      )
    },
    text = {
      LoadingPlaceholder(key = icons) { icons ->
        LazyVerticalGrid(
          cells = GridCells.Adaptive(Const.iconSizeDp),
          contentPadding = PaddingValues(10.dp),
          modifier = Modifier
            //.fillMaxHeight(1f)
            .padding(top = 30.dp),
        ) {
          items(icons.size) { i ->
            val icon = icons[i]
            IconProgressionPic(
              icon = painterResource(id = icon.resId),
              mainColor = color,
              name = null, //TODO: localize string
              iconMode = IconColorMode.COLORED_BG,
              progress = if(icon.id == initIcon?.id) 1f else 0f,
              progressStrokeColor = MaterialTheme.colors.primary,
              modifier = Modifier
                .size(Const.iconSizeDp)
                .let {
                  if(onItemSelected != null) it.clickable {
                    onItemSelected(icon)
                  } else it
                }
            )
          }
        }
      }
    },
    buttons = {
      /*
      Button(onClick = {
        onItemSelected?.invoke()
      }) {

      }
       */
    },
  )
}