package sidev.app.android.sitracker

import org.junit.Assert.assertEquals
import org.junit.Test
import sidev.app.android.sitracker.core.data.local.model.*
import sidev.app.android.sitracker.util.model.UnclosedLongRange
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*
import java.util.concurrent.TimeUnit

class CobTest {

  val tasks = listOf(
    Task(0, "Code 20 Lines", 1, "desc0", 0, "#FFF"),
    Task(1, "Read Life", 2, "desc1", 0, "#FFF"),
    Task(2, "Runaway", 3, "desc2", 0, "#FFF"),
  )

  val scheduleProgressNumber = listOf<Pair<Long, Long>>(
    100L to 2500L, 30L to 88L, 10L to 50L,
  )
  val schedules = tasks.mapIndexed { i, task ->
    Schedule(
      id = i,
      taskId = task.id,
      label = "Label #$i",
      progressTypeId = 0,
      intervalId = 0,
      totalProgress = scheduleProgressNumber[i].second,
    )
  }

  //val sdf = SimpleDateFormat("dd-MM-yyyy")
  //fun getTimeLong(dateStr: String) = sdf.parse(dateStr.)

  val now = Date()
  val nowLong = Date() //1635097869874L

  fun getTimeLong(
    addDiff: Long = 0,
    unit: TimeUnit = TimeUnit.DAYS,
    now: Date = this.now,
  ): Long =
    now.time + unit.toMillis(addDiff)

  val activeDates = listOf<ActiveDate>(
    ActiveDate(schedules[0].id, getTimeLong(-500, now = nowLong), getTimeLong(1, now = nowLong)),
    ActiveDate(schedules[1].id, getTimeLong(1, now = nowLong), getTimeLong(2, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(-1, now = nowLong)),
    ActiveDate(schedules[2].id, getTimeLong(-3, now = nowLong), getTimeLong(1, now = nowLong)),
  )

  val scheduleProgress = listOf<ScheduleProgress>(
    ScheduleProgress(0, activeDates[0].scheduleId, activeDates[0].startDate, activeDates[0].endDate ?: (now.time + 100), scheduleProgressNumber[0].first),
    ScheduleProgress(1, activeDates[1].scheduleId, activeDates[1].startDate, activeDates[1].endDate ?: (now.time + 100), scheduleProgressNumber[1].first),
    ScheduleProgress(2, activeDates[2].scheduleId, activeDates[2].startDate, activeDates[2].endDate ?: (now.time + 100), scheduleProgressNumber[2].first),
    ScheduleProgress(3, activeDates[3].scheduleId, activeDates[3].startDate, activeDates[3].endDate ?: (now.time + 100), scheduleProgressNumber[2].first),
  )

  val preferredTimes = listOf<PreferredTime>(
    PreferredTime(TimeUnit.HOURS.toMillis(9), null, schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(23), schedules[0].id),
    //PreferredTime(TimeUnit.MINUTES.toMillis(20), TimeUnit.HOURS.toMillis(1), schedules[0].id),
    PreferredTime(TimeUnit.HOURS.toMillis(23), null, schedules[2].id),
    PreferredTime(TimeUnit.HOURS.toMillis(0), TimeUnit.HOURS.toMillis(4), schedules[1].id),
  )

  val preferredDay = listOf<PreferredDay>(
    PreferredDay(3, schedules[2].id),
    PreferredDay(3, schedules[1].id),
  )


  @Test
  fun dummyTest() {
    assert(TestDummy.scheduleProgress.all { schProg ->
      TestDummy.schedules.find { it.id == schProg.scheduleId } != null
    }) {
      "There are some `ScheduleProgress.scheduleId` with no pair with `Schedule.id`"
    }
    assert(TestDummy.schedules.all { sch ->
      TestDummy.tasks.find { it.id == sch.taskId } != null
    }) {
      "There are some `Schedule.taskId` with no pair with `Task.id`"
    }

    println("TestDummy.tasks = ${
      TestDummy.tasks.joinToStringLongList()
    }")
    println("TestDummy.schedules = ${
      TestDummy.schedules.joinToStringLongList()
    }")
    println("TestDummy.scheduleProgress = ${
      TestDummy.scheduleProgress.joinToStringLongList()
    }")
  }

  @Test
  fun activeFilterTest() {
    // ====== REQ DATA =======

    println("tasks = ${
      tasks.joinToStringLongList()
    }")
    println("activeDates = ${
      activeDates.joinToStringLongList()
    }")
    println("schedules = ${
      schedules.joinToStringLongList()
    }")
    println("scheduleProgress = ${
      scheduleProgress.joinToStringLongList()
    }")

    // ====== TEST =======
    val nowDateLong = Date().time

    val filteredActiveDates = TestDummy.filterActiveDates(
      activeDates,
      nowDateLong,
    )
    assertEquals(listOf(activeDates[0], activeDates[3]), filteredActiveDates)

    val activeSchedules = TestDummy.filterActiveSchedules(
      schedules = schedules,
      filteredActiveDates = filteredActiveDates,
      nowDateLong,
    )
    assertEquals(listOf(schedules[0], schedules[2]), activeSchedules)

    val activeTasks = TestDummy.filterActiveTask(
      tasks = tasks,
      activeSchedules,
      filteredActiveDates,
      nowDateLong,
    )
    assertEquals(listOf(tasks[0], tasks[2]), activeTasks)

    val activeProgress = TestDummy.filterActiveProgress(
      progress = scheduleProgress,
      nowDateLong,
    )
    assertEquals(listOf(scheduleProgress[0], scheduleProgress[3]), activeProgress)

    println("filteredActiveDates = ${
      filteredActiveDates.joinToStringLongList()
    }")
    println("activeSchedules = ${
      activeSchedules.joinToStringLongList()
    }")
    println("activeTasks = ${
      activeTasks.joinToStringLongList()
    }")
    println("activeProgress = ${
      activeProgress.joinToStringLongList()
    }")
  }

/*
  @Test
  fun progressImportanceTest() {
    val joins = TestDummy.getProgressJoint(
      tasks = tasks,
      schedules = schedules,
      progresses = scheduleProgress,
      activeDates = activeDates,
      preferredTimes = preferredTimes,
      preferredDays = preferredDay,
    )
    val now = this.now.time

    println("importances = ${
      joins.joinToStringLongList {
        val importance = it.importance
        val factor = importance.factor
        val default = importance.toString()
        // 
        //          |isActive = ${importance.factor.isActive()}
        """$default 
          |calculated = ${importance.getImportance(now, it.progress.actualProgress)}
          |tiFactor = ${factor.tiFactor(now)}
          |tdFactor = ${factor.tdFactor(now)}
          |prefFactor = ${factor.prefFactor(now)}
          |pFactor = ${factor.pFactor(it.progress.actualProgress)}
          |prFactor = ${factor.prFactor}""".trimMargin()
      }
    }")
  }
 */


  @Test
  fun fractionTest() {
    val end = 10000.0
    val start = end - 10000.0
    val now = 9000.0

    val startToEnd = end - start
    val startToNow = now - start

    val fraction = startToNow / startToEnd

    println("start = $start")
    println("now = $now")
    println("end = $end")

    println("startToEnd = $startToEnd")
    println("startToNow = $startToNow")
    println("fraction = $fraction")
  }

  @Test
  fun timeTest() {
    val locDate = LocalDateTime.now()
      LocalDate.ofEpochDay(ChronoField.EPOCH_DAY.range().maximum)
    locDate.dayOfMonth


    val instant = Instant.now()

/*
    LocalDate.now().format()

    println("locDate = $locDate locDate.year= ${locDate.year} locDate.dayOfMonth = ${locDate.dayOfMonth} locDate.monthValue= ${locDate.monthValue}")


 */
    //val date = Date(millis)

    //println("date  = $date")
/*
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
      .withLocale(Locale.ENGLISH)
      .withZone(ZoneId.systemDefault())
// */


    val formatter = //DateTimeFormatter.ofPattern("yyyy-MM-dd hh.ss")
      DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val str = formatter.format(locDate)

    println("str = $str")

    println(Date(Long.MAX_VALUE))
    println(Date())
    println(Long.MAX_VALUE)
    println(Date().time)
  }


  @Test
  fun filterAndMapTest() {
    data class Person(
      val id: Int,
      val name: String,
      val age: Int,
    )

    val people = listOf(
      Person(0, "aku", 10),
      Person(2, "ello", 11),
      Person(3, "Bro", 21),
      Person(4, "He", 31),
    )

    val above18Names = people.filterAndMap {
      take(it.age >= 18)
      it.name
    }
    val expectedAbove18Names = people
      .filter { it.age >= 18 }
      .map { it.name }

    assertEquals(expectedAbove18Names, above18Names)


    val underAgeAges = people.filterAndMap {
      take(it.age < 18)
      it.age
    }
    val expectedUnderAgeAges = people
      .filter { it.age < 18 }
      .map { it.age }

    assertEquals(expectedUnderAgeAges, underAgeAges)


    val underAgeNames = people.filterAndMap {
      if(it.age < 18) {
        take()
      }
      it.name
    }
    val expectedUnderAgeNames = people
      .filter { it.age < 18 }
      .map { it.name }

    assertEquals(expectedUnderAgeNames, underAgeNames)

    println("above18Names = $above18Names")
    println("underAgeAges = $underAgeAges")
    println("underAgeNames = $underAgeNames")
  }


  @Test
  fun unclosedLongRangeTest() {
    val range1 = UnclosedLongRange(10, 20)
    val range2 = UnclosedLongRange(14, null)
    val range3 = UnclosedLongRange(17, 20)


    assert(14 in range1)
    assert(9 !in range1)

    assert(14 in range2)
    assert(1100 in range2)
    assert(11 !in range2)

    assert(range3 in range1)
    assert(range3 in range2)
    assert(range2 !in range1)
    assert(range1 !in range2)
    assert(range1 !in range3)
  }
}