package sidev.app.android.sitracker.util.dummy.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import sidev.app.android.sitracker.core.data.local.dao.*
import sidev.app.android.sitracker.core.data.local.model.Task
import sidev.app.android.sitracker.util.dummy.Dummy


object TaskDaoDummy: TaskDao {
  override fun getAll(): Flow<List<Task>> = flow {
    emit(Dummy.tasks)
  }

  override fun getById(id: Int): Flow<Task?> = getAll().map {
    it.find { it.id == id }
  }

  override fun getByIds(ids: Set<Int>): Flow<List<Task>> = getAll().map {
    it.filter { it.id in ids }
  }

  override fun getOrderedByPriority(limit: Int): Flow<List<Task>> = getAll().map {
    it.sortedByDescending { it.priority }.take(limit)
  }

  override fun getNotInIdsOrderedByPriority(
    excludedIds: Set<Int>,
    limit: Int
  ): Flow<List<Task>> = getAll().map {
    it.filter { it.id !in excludedIds }
      .take(limit)
  }

  override fun insert(task: Task): Flow<Int> = flow { emit(1) }

  override fun delete(task: Task): Flow<Int> = flow { emit(1) }

  override fun update(newTask: Task) {}
}