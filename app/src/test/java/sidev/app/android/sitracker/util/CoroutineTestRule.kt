package sidev.app.android.sitracker.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CoroutineTestRule(
  val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
): TestWatcher() {
  /**
   * Invoked when a test is about to start
   */
  override fun starting(description: Description?) {
    super.starting(description)
    Dispatchers.setMain(dispatcher)
  }

  /**
   * Invoked when a test method finishes (whether passing or failing)
   */
  override fun finished(description: Description?) {
    super.finished(description)
    Dispatchers.resetMain()
    dispatcher.cleanupTestCoroutines()
  }
}