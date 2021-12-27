package sidev.app.android.sitracker.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*

class CoroutineJobManager(
  lifecycle: Lifecycle? = null
): LifecycleObserver {
  private val _jobs = mutableMapOf<String, Job>()
  val jobs: Map<String, Job>
    get() = _jobs

  var lifecycle: Lifecycle? = lifecycle
    set(v) {
      v?.addObserver(this) ?: run {
        field?.removeObserver(this)
      }
      field = v
    }

  /**
   * Returns currently active [Job],
   * whether new or old one (when [cancelPrevious] is `true` but still active).
   */
  fun launch(
    scope: CoroutineScope,
    key: String,
    cancelPrevious: Boolean = true,
    block: suspend CoroutineScope.() -> Unit,
  ): Job {
    _jobs[key]?.let {
      when {
        cancelPrevious -> it.cancel()
        it.isActive -> return it
      }
    }
    return scope.launch(block = block).also {
      _jobs[key] = it
    }
  }

  fun cancelJob(key: String, cause: CancellationException? = null) {
    _jobs[key]?.cancel(cause)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun cancelAllJobs(cause: CancellationException? = null) {
    _jobs.values.forEach { it.cancel(cause) }
  }
}


interface AppCoroutineScope: CoroutineScope {
  fun CoroutineJobManager.launch(
    key: String,
    cancelPrevious: Boolean = true,
    block: suspend AppCoroutineScope.() -> Unit,
  ): Job = this.launch(
    this@AppCoroutineScope,
    key,
    cancelPrevious,
  ) {
    this@AppCoroutineScope.block()
  }
}

fun CoroutineScope.toAppScope(): AppCoroutineScope =
  object : AppCoroutineScope, CoroutineScope by this {}