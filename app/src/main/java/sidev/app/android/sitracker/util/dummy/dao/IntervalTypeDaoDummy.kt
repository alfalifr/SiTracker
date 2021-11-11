package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.IntervalTypeDao
import sidev.app.android.sitracker.core.data.local.model.IntervalType
import sidev.app.android.sitracker.util.dummy.Dummy

object IntervalTypeDaoDummy: IntervalTypeDao {
  override fun getAll(): Flow<List<IntervalType>> = flow {
    emit(Dummy.intervals)
  }

  override fun getById(id: Int): Flow<IntervalType?> = flow {
    emit(
      Dummy.intervals.find { it.id == id }
    )
  }

  override fun getByIds(ids: Set<Int>): Flow<List<IntervalType>> = flow {
    emit(
      Dummy.intervals.filter { it.id in ids }
    )
  }
}