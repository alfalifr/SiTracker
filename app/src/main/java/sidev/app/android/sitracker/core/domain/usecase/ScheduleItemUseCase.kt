package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.domain.model.*
import sidev.app.android.sitracker.util.Texts
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.util.concurrent.TimeUnit


interface ScheduleItemUseCase {
  /**
   * Extract and join data in [ScheduleJoint] into list of [ScheduleItemData].
   */
  fun extractTaskItemSchedules(
    progressJoint: ScheduleJoint,
  ): List<ScheduleItemData>

  /**
   * Same like [extractTaskItemSchedules], but this method
   * extract and join all results from [extractTaskItemSchedules]
   * into 1 list.
   */
  fun getTaskItemSchedules(
    progressJoints: List<ScheduleJoint>,
  ): List<ScheduleItemData> = progressJoints.flatMap { extractTaskItemSchedules(it) }


  /**
   * Order and group [taskItemSchedules] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.timeRange].
   *
   * The algorithm works by grouping the [ScheduleItemData.timeRange].
   * If there is an overlap between items, then they are in one group.
   * Items that have little different between item 1 (early one) end time
   * and item 2 (late one) start time will also become in one group.
   */
  fun orderTaskItemScheduleByTime(
    scheduleItems: List<ScheduleItemData>,
  ): List<ScheduleItemGroupData>

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.task.name].
   */
  fun orderTaskItemScheduleByName(
    scheduleItems: List<ScheduleItemData>,
  ): List<ScheduleItemGroupData>

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.task.priority].
   */
  fun orderTaskItemScheduleByPriority(
    scheduleItems: List<ScheduleItemData>,
  ): List<ScheduleItemGroupData>

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.progress].
   */
  fun orderTaskItemScheduleByProgress(
    scheduleItems: List<ScheduleItemData>,
  ): List<ScheduleItemGroupData>

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by the [order].
   */
  fun orderTaskItemScheduleBy(
    scheduleItems: List<ScheduleItemData>,
    order: ScheduleItemGroupOrder,
  ): List<ScheduleItemGroupData> = when(order) {
    ScheduleItemGroupOrder.BY_TIME -> orderTaskItemScheduleByTime(scheduleItems)
    ScheduleItemGroupOrder.BY_NAME -> orderTaskItemScheduleByName(scheduleItems)
    ScheduleItemGroupOrder.BY_PRIORITY -> orderTaskItemScheduleByPriority(scheduleItems)
    ScheduleItemGroupOrder.BY_PROGRESS -> orderTaskItemScheduleByProgress(scheduleItems)
  }
}



//TODO: Optimize clustering (grouping) algo in future.
class ScheduleItemUseCaseImpl(
  private val iconUseCase: IconUseCase,
): ScheduleItemUseCase {
  /**
   * Extract and join data in [ProgressJoint] into list of [ScheduleItemData].
   */
  override fun extractTaskItemSchedules(
    progressJoint: ScheduleJoint
  ): List<ScheduleItemData> = with(progressJoint) {
    /*
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
     */

    val scheduleItems = mutableListOf<ScheduleItemData>()

    /*
    fun createTaskCompData(timeRange: UnclosedLongRange?) = TaskCompData(
      icon = prefixIcon,
      title = task.name,
      desc = timeRange?.diff()?.let {
        Texts.formatDurationToShortest(it)
      },
      postfixIconData = postfixIconData,
      isPostfixIconDataColorSameAsMainColor = true,
    )
     */

    for(preferredTime in preferredTimes) {
      val timeRange = UnclosedLongRange(
        start = preferredTime.startTime,
        end = preferredTime.endTime,
      )

      //val taskCompData = createTaskCompData(timeRange)

      scheduleItems += ScheduleItemData(
        this, timeRange
      )
    }
    if(scheduleItems.isEmpty()) {
      scheduleItems += ScheduleItemData(
        scheduleJoint = this,
        timeRange = null,
      )
    }
    return scheduleItems
  }

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.timeRange].
   *
   * The algorithm works by grouping the [ScheduleItemData.timeRange].
   * If there is an overlap between items, then they are in one group.
   * Items that have little different between item 1 (early one) end time
   * and item 2 (late one) start time will also become in one group.
   */
  override fun orderTaskItemScheduleByTime(
    scheduleItems: List<ScheduleItemData>
  ): List<ScheduleItemGroupData> {
    var currentGroup = 0
    var doesCurrentlyHavePreferredTime = false
    var currentCheckpoint: Long? = null
    var isCheckpointStart = false

    return scheduleItems.sortedBy {
      it.timeRange?.start
    }.groupBy {
      //1. If `timeRange` is null, then no need to check whether it overlaps with other or not.
      if(it.timeRange == null) {
        return@groupBy currentGroup
      }

      //2. If `it` began to have `timeRange`, then simply mark the boolean to true
      //   and don't forget to increase the group number.
      if(!doesCurrentlyHavePreferredTime) {
        doesCurrentlyHavePreferredTime = true
        currentGroup++
      }

      val currentStart = it.timeRange.start

      //3. If previous checkpoint (`currentCheckpoint`) null,
      //   it means `it` is the first item (or previous `timeRange` is null).
      //   If it is `null` then there is no need to calculate the overlap.
      if(currentCheckpoint != null) {
        val bufferTime =
          if(!isCheckpointStart) 0L
          else TimeUnit.MINUTES.toMillis(45) //Add some buffer if the `currentCheckpoint` is the previous start time.
              // It is because to look better.

        // If `it` start time doesn't overlap the previous checkpoint,
        // then increase the group number. It means `it` belongs to different group.
        if(currentStart > currentCheckpoint!! + bufferTime) {
          currentGroup++
        }
      }

      //4. Assign `currentCheckpoint` with current `timeRange` end time.
      currentCheckpoint = it.timeRange.end
      if(currentCheckpoint == null) {
        // If `timeRange.end` is null, then assign `currentCheckpoint` with start time so that `currentCheckpoint` won't be null.
        isCheckpointStart = true
        currentCheckpoint = it.timeRange.start
      }

      currentGroup
    }.map { (_, taskItemScheduleList) ->
      val startTime = taskItemScheduleList.first().timeRange?.start
      var headStr: String

      if(startTime == null) {
        headStr = "All day" //TODO: Localize string
      } else {
        headStr = Texts.formatTimeToShortest(startTime)
        val endTime = taskItemScheduleList.last().timeRange!!.end
        if(endTime == startTime) {
          headStr += " - ${Texts.formatDurationToShortest(endTime)}"
        }
      }

      ScheduleItemGroupData(
        schedules = taskItemScheduleList,
        header = headStr,
      )
    }
  }

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.task.name].
   */
  override fun orderTaskItemScheduleByName(
    scheduleItems: List<ScheduleItemData>
  ): List<ScheduleItemGroupData> = scheduleItems
    .groupBy {
      it.scheduleJoint.task.name.firstOrNull()
    }.map { (key, value) ->
      ScheduleItemGroupData(
        schedules = value,
        header = key?.toString() ?: "..."
      )
    }


  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.task.priority].
   */
  override fun orderTaskItemScheduleByPriority(
    scheduleItems: List<ScheduleItemData>
  ): List<ScheduleItemGroupData> = scheduleItems
    .groupBy {
      it.scheduleJoint.task.priority
    }.map { (key, value) ->
      ScheduleItemGroupData(
        schedules = value,
        header = "# $key",
      )
    }

  /**
   * Order and group [scheduleItems] into list
   * of [ScheduleItemGroupData] by [ScheduleItemData.scheduleJoint.progress].
   */
  override fun orderTaskItemScheduleByProgress(
    scheduleItems: List<ScheduleItemData>
  ): List<ScheduleItemGroupData> {
    //Divided into 5 parts
    fun getProgressClass(fraction: Double): Int = when {
      fraction <= 20.0 -> 1
      fraction in 20.0..40.0 -> 2
      fraction in 40.0..60.0 -> 3
      fraction in 60.0..80.0 -> 4
      else -> 5
      //fraction >= 80.0 -> 5
    }

    fun getProgressClassString(klass: Int): String = when(klass) {
      1 -> "0% - 20%"
      2 -> "20% - 40%"
      3 -> "40% - 60%"
      4 -> "60% - 80%"
      else -> "80% - 100%"
    }
    return scheduleItems.groupBy { taskItemSchedule ->
      val fraction = taskItemSchedule.scheduleJoint.progress?.let {
        it.actualProgress.toDouble() /
          taskItemSchedule.scheduleJoint.schedule.totalProgress
      } ?: 0.0
      getProgressClass(fraction)
    }.map { (key, value) ->
      ScheduleItemGroupData(
        schedules = value,
        header = getProgressClassString(key)
      )
    }
  }
}