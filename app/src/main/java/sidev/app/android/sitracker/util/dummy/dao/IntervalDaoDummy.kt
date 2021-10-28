package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.IntervalDao
import sidev.app.android.sitracker.core.data.local.model.Interval
import sidev.app.android.sitracker.util.dummy.Dummy

object IntervalDaoDummy: IntervalDao {
  override fun getAll(): Flow<List<Interval>> = flow {
    emit(Dummy.intervals)
  }
}