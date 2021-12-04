package sidev.app.android.sitracker.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import sidev.app.android.sitracker.util.Texts.formatTimeToClock

abstract class CountDownTicker(
  private val millisInFuture: Long,
  private val millisInterval: Long,
) {

  companion object {
    private const val MSG = 1
  }
  /**
   * Millis since the count down is paused until finish.
   */
  private var pauseRemaining: Long = 0L

  /**
   * Millis of system timestamp which the count down stops.
   */
  private var stopInFuture: Long = 0L

  /**
   * Whether the count down has started or not.
   * It can be useful to mark the pause event since the count down has started but is not running.
   */
  private var hasStarted = false

  /**
   * Whether the count down is currently running or not.
   */
  private var isRunning = false
/*
  /**
   * Whether [pauseRemaining] is a fixed millis or not.
   * It can be useful to mark checkpoint from external source (other than this class [pause]).
   */
  private var isPauseTimeFixed = false
 */


  @Synchronized
  fun start() {
    if(millisInFuture <= 0) {
      callOnFinish()
      return
    }
    if(isRunning) {
      return
    }
    if(!hasStarted) {
      stopInFuture = SystemClock.elapsedRealtime() + millisInFuture
      hasStarted = true
    } else {
      println("CountDownTicker.start() !hasStarted pauseRemaining = ${formatTimeToClock(pauseRemaining)}")
      stopInFuture = SystemClock.elapsedRealtime() + pauseRemaining
    }
    startHandler(true)
  }

  @Synchronized
  fun pause(captureTimeRemaining: Boolean = true) {
    //val before = pauseRemaining
    if(hasStarted && captureTimeRemaining) {
      pauseRemaining = stopInFuture - SystemClock.elapsedRealtime()
    }
    //println("CountDownTicker.pause() hasStarted = $hasStarted before = ${formatTimeToClock(before)} after = ${formatTimeToClock(pauseRemaining)}")
    startHandler(false)
  }

  @Synchronized
  fun stop() {
    hasStarted = false
    startHandler(false)
  }

  private fun startHandler(start: Boolean) {
    isRunning = start
    if(start) {
      handler.sendMessage(handler.obtainMessage(MSG))
    } else {
      handler.removeMessages(MSG)
    }
  }

  private fun callOnFinish() {
    isRunning = false
    hasStarted = false
    onFinish()
  }

  @Synchronized
  fun setStart(millisRemaining: Long) {
    println("CountDownTicker.setStart() millisRemaining = $millisRemaining millisInFuture = $millisInFuture")
    if(millisRemaining <= millisInFuture) {
      stopInFuture = SystemClock.elapsedRealtime() + millisRemaining
      pauseRemaining = millisRemaining
    }
  }

/*
  @Synchronized
  fun setCheckpoint(millisRemaining: Long?) {
    if(millisRemaining == null || millisRemaining > millisInFuture) {
      isPauseTimeFixed = false
    } else {
      isPauseTimeFixed = true
      pauseRemaining = millisRemaining
    }
  }

  @Synchronized
  fun setCheckpointFromElapsed(millisElapsed: Long?) = setCheckpoint(
    if(millisElapsed == null) null
    else millisInFuture - millisElapsed
  )
 */

  /**
   * Callback fired on regular interval.
   * @param millisUntilFinish The amount of time until finished.
   */
  abstract fun onTick(millisUntilFinish: Long)

  /**
   * Callback fired when the time is up.
   */
  abstract fun onFinish()


  private val handler: Handler by lazy {
    object: Handler(Looper.getMainLooper()) {
      /**
       * Subclasses must implement this to receive messages.
       */
      override fun handleMessage(msg: Message) {
        if(!isRunning) {
          return
        }

        val millisLeft = stopInFuture - SystemClock.elapsedRealtime()

        if(millisLeft <= 0) {
          callOnFinish()
        } else {
          val lastTickStart = SystemClock.elapsedRealtime()
          onTick(millisLeft)

          // `onTick` must take time to execute
          val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
          var delay: Long

          if(millisLeft < millisInterval) {
            // delay until finish
            delay = millisLeft - lastTickDuration

            // if `onTick` takes longer than `millisLeft`,
            // just trigger `onFinish` without delay.
            if(delay < 0) delay = 0
          } else {
            delay = millisInterval - lastTickDuration

            // if `onTick` takes longer than the count down interval,
            // just skip this tick wait for the next.
            while(delay < 0) delay += millisInterval
          }

          sendMessageDelayed(obtainMessage(MSG), delay)
        }
      }
    }
  }
}