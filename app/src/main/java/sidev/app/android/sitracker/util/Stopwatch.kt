package sidev.app.android.sitracker.util

/**
 * All time unit this class uses is nano second (1 sec = 10^9 nanos),
 * except for some properties that have been given note in doc.
 * This is just simple class to measure elapsed time.
 * This class doesn't have any synchronization
 * and perhaps is **not thread safe**
 */
class Stopwatch {
  var start: Long = 0
    private set

  var end: Long = start
    private set

  @Volatile
  var isRunning = false
    private set

  val currentElapsedTime: Long
    get() = if(!isRunning) end - start
      else System.nanoTime() - start

  val currentElapsedTimeInMillis: Long
    get() = currentElapsedTime / 1_000_000L

  fun reset() {
    start = 0
    end = start
  }

  fun start() {
    start = System.nanoTime()
    end = start
    isRunning = true
  }

  fun stop() {
    end = System.nanoTime()
    isRunning = false
  }
}