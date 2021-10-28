package sidev.app.android.sitracker.di

import android.app.Application
import android.content.Context
import sidev.app.android.sitracker.core.data.local.AppDb
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.PreferredDay
import sidev.app.android.sitracker.core.data.local.model.PreferredTime



interface DaoDi {
  fun taskDao(): TaskDao
  fun scheduleDao(): ScheduleDao
  fun activeDateDao(): ActiveDateDao
  fun preferredTimeDao(): PreferredTimeDao
  fun preferredDayDao(): PreferredDayDao
  fun scheduleProgressDao(): ScheduleProgressDao
  fun intervalDao(): IntervalDao
  fun progressTypeDao(): ProgressTypeDao
}


class DaoDiImpl(private val context: Context): DaoDi {
  override fun taskDao(): TaskDao = AppDb.create(context).taskDao()
  override fun scheduleDao(): ScheduleDao = AppDb.create(context).scheduleDao()
  override fun activeDateDao(): ActiveDateDao = AppDb.create(context).activeDateDao()
  override fun preferredTimeDao(): PreferredTimeDao = AppDb.create(context).preferredTimeDao()
  override fun preferredDayDao(): PreferredDayDao = AppDb.create(context).preferredDayDao()
  override fun scheduleProgressDao(): ScheduleProgressDao = AppDb.create(context).scheduleProgressDao()
  override fun intervalDao(): IntervalDao = AppDb.create(context).intervalDao()
  override fun progressTypeDao(): ProgressTypeDao = AppDb.create(context).progressTypeDao()
}