package sidev.app.android.sitracker.ui.page.schedule_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.IconWithText
import sidev.app.android.sitracker.ui.component.IconWithTexts
import sidev.app.android.sitracker.ui.component.LargeSurface
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.Texts

@Composable
fun ScheduleDetailPage() {
  TODO("Continue `ScheduleDetailPage`")
}

@Composable
private fun HeaderPanel(data: ScheduleDetailHeaderUiData) {
  LargeSurface {
    Column(
      verticalArrangement = Arrangement.spacedBy(Const.stdSpacerDp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        IconWithText(
          icon = painterResource(id = R.drawable.ic_timer),
          text = data.totalProgress,
        )
        IconWithText(
          icon = painterResource(id = R.drawable.ic_frequency),
          text = data.interval,
        )
      }
      IconWithTexts(
        icon = painterResource(id = R.drawable.ic_calendar),
        iconContentDescription = Texts.activeDates,
        texts = data.activeDates.map { Texts.intervalStr(it) }.toTypedArray(),
      )
    }
  }
}

@Composable
private fun PreferredTimePanel(data: ScheduleDetailPreferredTimeUi) {
  LargeSurface {
    val titleFun = @Composable {
      Text(
        Texts.preferredTimes,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
      )
    }
    val arrangement = Arrangement.spacedBy(Const.stdSpacerDp)

    if(data.preferredTimes.size >= 2) {
      Column(
        verticalArrangement = arrangement,
      ) {
        titleFun()

        /*
        This section divides preferred time into 2 columns (left and right).
        The order of preferred times is ascending with the start column (left column) first
        then end column (right column).

        This section DOESN'T SORT the contents of `data.preferredTimes`.
         */
        Row(
          Modifier.padding(start = Const.stdSpacerDp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          val columnSize = data.preferredTimes.size / 2
          val columnExtraSize = data.preferredTimes.size % 2

          @Composable
          fun _ContentText(preferredTime: Pair<String, String?>) {
            Text(
              text = Texts.intervalStr(preferredTime),
              style = MaterialTheme.typography.body2,
            )
          }

          @Composable
          fun _InnerColumn(start: Int, end: Int) {
            Column(
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.spacedBy(Const.stdSpacerDp),
            ) {
              for(i in start until end) {
                _ContentText(data.preferredTimes[i])
              }
            }
          }

          //For start (left) column.
          _InnerColumn(start = 0, end = columnSize + columnExtraSize)

          //For end (right) column.
          val endStart = (columnSize + columnExtraSize)
          _InnerColumn(start = endStart, end = endStart + columnSize)
        }
      }
    } else {
      //When the preferred time size is 1 or less.
      val preferredTimeContent: @Composable (Any) -> Unit = {
        titleFun()
        Text(
          if(data.preferredTimes.isEmpty()) Texts.noPreferredTimes
          else Texts.intervalStr(data.preferredTimes.first()),
          style = MaterialTheme.typography.body2,
        )
      }

      if(data.preferredTimes.isEmpty()) {
        Column(
          verticalArrangement = arrangement,
          content = preferredTimeContent,
        )
      } else {
        Row(
          horizontalArrangement = arrangement,
          content = preferredTimeContent,
        )
      }
    }
  }
}

@Composable
private fun PreferredDayPanel(data: ScheduleDetailPreferredDayUi) {
  LargeSurface {
    Column(

    ) {
      Text(
        Texts.preferredDays,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
      )
      Spacer(Modifier.height(Const.stdSpacerDp))
      //Row
    }
  }
}