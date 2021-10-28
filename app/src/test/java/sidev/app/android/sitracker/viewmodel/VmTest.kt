package sidev.app.android.sitracker.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import sidev.app.android.sitracker.core.domain.model.ProgressImportanceJoint
import sidev.app.android.sitracker.joinToStringLongList
import sidev.app.android.sitracker.util.CoroutineTestRule
import sidev.app.android.sitracker.util.dummy.DiGraphDummy

class VmTest {
/*
  @get:Rule
  val rule = CoroutineTestRule(testDispatcher)
// */

  companion object {
    val job = Job()
    val testDispatcher = TestCoroutineDispatcher()
    val testScope = TestCoroutineScope(job + testDispatcher)

    @JvmStatic
    @BeforeClass
    fun classSetup() {
      DiGraphDummy.coroutineScope = testScope
      println("classSetup()")
    }

    @JvmStatic
    @AfterClass
    fun classFinish() {
      DiGraphDummy.coroutineScope = null
      println("classFinish()")
    }
  }

  @Test
  fun homeViewModelTest() = testScope.runBlockingTest {
    val vm = DiGraphDummy.vmDi().homeViewModel().apply {
      //var isFinished = false
      var title: String? = null
      var index: Int? = null
      var importancesVal: List<ProgressImportanceJoint>? = null
      var sortedImportancesVal: List<ProgressImportanceJoint>? = null

      val job1 = launch {
        println("Hello launch")
        activeTaskTitle.collect {
          println("title = $it")
          title = it
        }
      }

      // I launch the second coroutine because `collect` will suspend the current coroutine forever.
      val job2 = launch {
        println("Hello launch 2")
        activeTaskIndex.collect {
          println("active index = $it")
          index = it
        }
      }

      val job3 = launch {
        sortedImportances.collect {
          println("sortedImportancesVal = ${it.joinToStringLongList()}")
          sortedImportancesVal = it
        }
      }

      val job4 = launch {
        importances.collect {
          println("importancesVal = ${it.joinToStringLongList()}")
          importancesVal = it
        }
      }

      getActiveSchedules()
      activeTaskIndex.value = 0
/*
      val job5 = launch {
        while(importancesVal == null) {
          delay(500)
        }
      }
 */

      while(title == null
        || index == null
        || importancesVal == null
        || sortedImportancesVal == null
      ) {
        delay(500)
      }
      //while(!isFinished) { }
      println("finished")
      job1.cancel()
      job2.cancel()
      job3.cancel()
      job4.cancel()
    }
  }
}