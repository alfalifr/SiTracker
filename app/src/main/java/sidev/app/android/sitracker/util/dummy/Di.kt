package sidev.app.android.sitracker.util.dummy

import android.content.Context
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.PreferredDay
import sidev.app.android.sitracker.core.data.local.model.PreferredTime
import sidev.app.android.sitracker.di.DaoDi


object DaoDiDummy: DaoDi {
  override fun taskDao(context: Context): TaskDao {
    TODO("Not yet implemented")
  }

  override fun scheduleDao(context: Context): ScheduleDao {
    TODO("Not yet implemented")
  }

  override fun activeDateDao(context: Context): ActiveDateDao {
    TODO("Not yet implemented")
  }

  override fun preferredTimeDao(context: Context): PreferredTime {
    TODO("Not yet implemented")
  }

  override fun preferredDayDao(context: Context): PreferredDay {
    TODO("Not yet implemented")
  }

  override fun scheduleProgressDao(context: Context): ScheduleProgressDao {
    TODO("Not yet implemented")
  }

  override fun intervalDao(context: Context): IntervalDao {
    TODO("Not yet implemented")
  }

  override fun progressTypeDao(context: Context): ProgressTypeDao {
    TODO("Not yet implemented")
  }
}