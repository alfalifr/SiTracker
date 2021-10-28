package sidev.app.android.sitracker.di

import android.app.Application
import android.content.Context
import sidev.app.android.sitracker.core.data.local.AppDb
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.PreferredDay
import sidev.app.android.sitracker.core.data.local.model.PreferredTime



interface DaoDi {
  fun taskDao(context: Context): TaskDao
  fun scheduleDao(context: Context): ScheduleDao
  fun activeDateDao(context: Context): ActiveDateDao
  fun preferredTimeDao(context: Context): PreferredTime
  fun preferredDayDao(context: Context): PreferredDay
  fun scheduleProgressDao(context: Context): ScheduleProgressDao
  fun intervalDao(context: Context): IntervalDao
  fun progressTypeDao(context: Context): ProgressTypeDao
}


object DaoDiImpl: DaoDi {
  override fun taskDao(context: Context): TaskDao = AppDb.create(context).taskDao()
  override fun scheduleDao(context: Context): ScheduleDao = AppDb.create(context).scheduleDao()
  override fun activeDateDao(context: Context): ActiveDateDao = AppDb.create(context).activeDateDao()
  override fun preferredTimeDao(context: Context): PreferredTime = AppDb.create(context).preferredTimeDao()
  override fun preferredDayDao(context: Context): PreferredDay = AppDb.create(context).preferredDayDao()
  override fun scheduleProgressDao(context: Context): ScheduleProgressDao = AppDb.create(context).scheduleProgressDao()
  override fun intervalDao(context: Context): IntervalDao = AppDb.create(context).intervalDao()
  override fun progressTypeDao(context: Context): ProgressTypeDao = AppDb.create(context).progressTypeDao()
}