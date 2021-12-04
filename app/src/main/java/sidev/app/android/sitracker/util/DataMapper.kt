package sidev.app.android.sitracker.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import sidev.app.android.sitracker.R
import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.core.domain.usecase.IconUseCase
import sidev.app.android.sitracker.ui.model.*
import sidev.app.android.sitracker.ui.theme.GreenLight

object DataMapper {
  @Composable
  fun IconProgressionPicData.toUiData(): IconProgressionPicUiData = IconProgressionPicUiData(
    image = painterResource(id = resId),
    color = Color(color),
    progress = progressFraction,
  )
  @Composable
  fun IconProgressionTextData.toUiData(): IconProgressionTextUiData = IconProgressionTextUiData(
    text = text,
    color = Color(color),
    progress = progressFraction,
  )
  @Composable
  fun IconProgressionData.toUiData(): IconProgressionUiData = when(this) {
    is IconProgressionPicData -> toUiData()
    is IconProgressionTextData -> toUiData()
  }

  @Composable
  fun IconPicData.toUiData(): IconPicUiData = when(this) {
    is IconProgressionPicData -> toUiData()
    else -> IconPicUiData(
      image = painterResource(id = resId),
      color = Color(color),
    )
  }

  @Composable
  fun List<IconProgressionPicData>.toPicUiData(): List<IconProgressionPicUiData> = map {
    it.toUiData()
  }
  @Composable
  fun List<IconProgressionTextData>.toTextUiData(): List<IconProgressionTextUiData> = map {
    it.toUiData()
  }
  @Composable
  fun List<IconProgressionData>.toUiData(): List<IconProgressionUiData> = map {
    it.toUiData()
  }

  fun ScheduleItemData.toUiData(
    iconUseCase: IconUseCase,
  ): ScheduleItemDataUi = with(scheduleJoint) {
    val prefixIcon = iconUseCase.getIconProgressionData(this)

    val progressFraction = progress?.actualProgress?.let {
      it.toFloat() / schedule.totalProgress
    } ?: 0f

    val postfixIconData: IconProgressionData = when {
      progressFraction < 1f -> IconProgressionTextData(
        text = Texts.formatProgress(progressFraction),
        color = getHexString(
          getScoreColor(progressFraction, 1).toArgb()
        )
          .also { println("TaskItemSchedule.toUiData color = $it") }
        ,
        progressFraction = progressFraction,
      )
      else -> IconProgressionPicData(
        resId = R.drawable.ic_check,
        color = getHexString(GreenLight.toArgb())
          .also { println("TaskItemSchedule.toUiData check color = $it") }
        ,
        progressFraction = null,
        desc = task.name,
      )
    }

    ScheduleItemDataUi(
      id = schedule.id,
      icon = prefixIcon,
      title = task.name,
      desc = timeRange?.diff()?.let {
        Texts.formatDurationToShortest(it)
      },
      postfixIconData = postfixIconData,
      isPostfixIconDataColorSameAsMainColor = false,
    )
  }

  fun ScheduleItemGroupData.toUiData(
    iconUseCase: IconUseCase,
  ): ScheduleItemGroupUi = ScheduleItemGroupUi(
    schedules = schedules.map { it.toUiData(iconUseCase) },
    header = header,
  )

/*
  fun ScheduleJoint.toTaskItemSchedule(
    iconUseCase: IconUseCase,
  ): List<TaskItemSchedule> {
    val prefixIcon = iconUseCase.getIconProgressionData(this)

    val progressFraction = progress?.actualProgress?.let {
      it.toFloat() / schedule.totalProgress
    } ?: 0f

    val postfixIconData: IconProgressionData = when {
      progressFraction < 1f -> IconProgressionTextData(
        text = Texts.formatProgress(progressFraction),
        color = task.color,
        progressFraction = progressFraction,
      )
      else -> IconProgressionPicData(
        resId = R.drawable.ic_check,
        color = task.color,
        progressFraction = progressFraction,
      )
    }

    val taskItemSchedules = mutableListOf<TaskItemSchedule>()

    fun createTaskCompData(timeRange: UnclosedLongRange?) = TaskCompData(
      icon = prefixIcon,
      title = task.name,
      desc = timeRange?.diff()?.let {
        Texts.formatDurationToShortest(it)
      },
      postfixIconData = postfixIconData,
      isPostfixIconDataColorSameAsMainColor = true,
    )

    for(preferredTime in preferredTimes) {
      val timeRange = UnclosedLongRange(
        start = preferredTime.startTime,
        end = preferredTime.endTime,
      )

      val taskCompData = createTaskCompData(timeRange)

      taskItemSchedules += TaskItemSchedule(
        taskCompData, timeRange
      )
    }
    if(taskItemSchedules.isEmpty()) {
      taskItemSchedules += TaskItemSchedule(
        taskCompData = createTaskCompData(null),
        timeRange = null,
      )
    }
    return taskItemSchedules
  }
 */
}