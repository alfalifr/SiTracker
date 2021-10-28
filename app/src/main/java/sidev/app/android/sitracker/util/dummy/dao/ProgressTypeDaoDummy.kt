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
}