package sidev.app.android.sitracker.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.*

@Database(
  entities = [
    Task::class,
    Schedule::class,
    ActiveDate::class,
    PreferredTime::class,
    PreferredDay::class,
    ScheduleProgress::class,
    Interval::class,
    ProgressType::class,
  ],
  version = 1,
  exportSchema = false,
)
abstract class AppDb: RoomDatabase() {
  abstract fun taskDao(): TaskDao
  abstract fun scheduleDao(): ScheduleDao
  abstract fun activeDateDao(): ActiveDateDao
  abstract fun preferredTimeDao(): PreferredTimeDao
  abstract fun preferredDayDao(): PreferredDayDao
  abstract fun scheduleProgressDao(): ScheduleProgressDao
  abstract fun intervalDao(): IntervalDao
  abstract fun progressTypeDao(): ProgressTypeDao

  companion object {
    private var instance: AppDb? = null
    fun create(context: Context): AppDb = instance ?: run {
      instance = Room.databaseBuilder(context, AppDb::class.java, "database.db")
        .build()
      instance!!
    }
    fun close() = instance?.close()
  }
}