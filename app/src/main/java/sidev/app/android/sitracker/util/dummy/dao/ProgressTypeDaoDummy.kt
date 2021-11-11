package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sidev.app.android.sitracker.core.data.local.dao.ProgressTypeDao
import sidev.app.android.sitracker.core.data.local.model.ProgressType
import sidev.app.android.sitracker.util.dummy.Dummy

object ProgressTypeDaoDummy: ProgressTypeDao {
  override fun getAll(): Flow<List<ProgressType>> = flow {
    emit(Dummy.progressTypes)
  }

  override fun getById(id: Int): Flow<ProgressType?> = flow {
    emit(
      Dummy.progressTypes.find { it.id == id }
    )
  }

  override fun getByIds(ids: Set<Int>): Flow<List<ProgressType>> = flow {
    emit(
      Dummy.progressTypes.filter { it.id in ids }
    )
  }
}