package sidev.app.android.sitracker.ui.page.main_menu.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import sidev.app.android.sitracker.core.data.local.dao.TaskDao
import sidev.app.android.sitracker.core.data.local.model.Task

class HomeViewModel(
  private val taskDao: TaskDao,
): ViewModel() {
  //private val _taskTitle = MutableLiveData<String>()
  //val taskTitle

  private val _recommendedTasks = MutableLiveData<List<Task>>()

  val activeTaskIndex = MutableLiveData<Int>()
  private val _validatedActiveTaskIndex = Transformations.map(activeTaskIndex) {
    ensureStateValid()
    it
  }

  val activeTaskTitle: LiveData<String> = Transformations.map(_validatedActiveTaskIndex) {
    _recommendedTasks.value!![it].name
  }
/*
  val activeLowerDetailData: LiveData<HomeLowerDetailData> = Transformations.map(_validatedActiveTaskIndex) {
    _recommendedTasks.value!![it].run {
      HomeLowerDetailData(
        priority = priority,
      )
    }
  }
 */

  private fun ensureStateValid() {
    assert(_recommendedTasks.value != null) {
      "`_recommendedTasks.value` == null"
    }
    assert(activeTaskIndex.value.let {
      it != null || it in _recommendedTasks.value!!.indices
    }) {
      """`activeTaskIndex.value` == null or `activeTaskIndex.value` outside `_recommendedTasks` indices
        |current `activeTaskIndex.value` = '${activeTaskIndex.value}'
      """.trimMargin()
    }
  }
}