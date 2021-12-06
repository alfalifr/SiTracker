@file:OptIn(ExperimentalAnimationApi::class)
package sidev.app.android.sitracker.ui.page.schedule_detail

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.ui.component.*
import sidev.app.android.sitracker.ui.layout.TitleIconLayout
import sidev.app.android.sitracker.util.Const
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.defaultViewModel
import sidev.app.android.sitracker.util.model.Direction

private val stdEmptyPanelHeight = 100.dp

@Composable
fun ScheduleDetailPage(
  scheduleId: Int,
  viewModel: ScheduleDetailViewModel = defaultViewModel(),
  navController: NavController = rememberNavController(),
  onIconClick: ((taskId: Int) -> Unit)? = null,
) {
  LaunchedEffect(key1 = Unit) {
    delay(500) // I don't know why but without delay, this side effect doesn't have effect.
    println("ScheduleDetailPage LaunchedEffect scheduleId = $scheduleId")
    viewModel.load(scheduleId)
  }
  val title = viewModel.scheduleLabel
    .collectAsState(initial = null).value
  val iconData = viewModel.taskIcon
    .collectAsState(initial = null).value

  val headerData = viewModel.headerData
    .collectAsState(initial = null).value
  val preferredTimes = viewModel.preferredTimes
    .collectAsState(initial = null).value
  val preferredDays = viewModel.preferredDays
    .collectAsState(initial = null).value

  println("""
    ScheduleDetailPage title = $title
    iconData = $iconData
  """.trimIndent())
///*
  LoadingPlaceholder(key = iconData) { iconData ->
    TitleIconLayout(
      title = title,
      icon = painterResource(id = iconData.iconResId),
      iconModifier = Modifier.clickable {
        onIconClick?.invoke(iconData.itemId)
      },
    ) {
      animatedHorizontalSliding(Direction.LEFT) {
        println("headerData = $headerData")
        HeaderPanel(data = headerData)
      }

      item { Spacer(Modifier.height(15.dp)) }

      animatedHorizontalSliding(Direction.LEFT) {
        PreferredTimePanel(data = preferredTimes)
        Spacer(Modifier.height(15.dp))
      }

      item { Spacer(Modifier.height(15.dp)) }

      animatedHorizontalSliding(Direction.LEFT) {
        PreferredDayPanel(data = preferredDays)
      }
    }
  }
// */
}

@Composable
private fun HeaderPanel(data: ScheduleDetailHeaderUiData?) {
  LargeSurface(
    Modifier
      .fillMaxWidth()
      .sizeIn(minHeight = stdEmptyPanelHeight),
  ) {
    LoadingPlaceholder(
      key = data,
      loadingText = null,
    ) { data ->
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
}

@Composable
private fun PreferredTimePanel(data: ScheduleDetailPreferredTimeUi?) {
  LargeSurface(
    Modifier
      .fillMaxWidth(),
      //.sizeIn(minHeight = stdEmptyPanelHeight),
  ) {
    LoadingPlaceholder(
      key = data,
      loadingText = null,
    ) { data ->
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
            Modifier.padding(start = Const.stdSpacerDp)
              .fillMaxWidth(),
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
}
