package sidev.app.android.sitracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import sidev.app.android.sitracker.util.LiveDataTransform
import sidev.app.android.sitracker.util.MediatorReponsiveLiveData
import sidev.app.android.sitracker.util.MutableResponsiveLiveData

class LiveDataTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Test
  fun responsiveLiveDataTest() {
    val liveData = MutableResponsiveLiveData<Int>()

    var observedValue1: Int? = null

    val addedObserver1 = Observer<Int> {
      observedValue1 = it
    }

    // `observedValue1` value must change BEFORE
    // this onLast observer has been notified.
    liveData.setOnLastChangedObserver {
      assertEquals(1, observedValue1)
      println("onLast value = ${liveData.value} observedValue1 = $observedValue1")
    }

    liveData.observeForever(addedObserver1)

    // `observedValue1` value must change AFTER
    // this onFirst observer has been notified.
    liveData.setOnFirstChangedObserver {
      assertEquals(null, observedValue1)
      println("onFirst value = ${liveData.value} observedValue1 = $observedValue1")
    }

    // Change the value of the live data.
    liveData.value = 1
  }

  @Test
  fun mediatorResponsiveLiveDataTest() {
    val srcLiveData = MutableLiveData<String>()

    val liveData = MediatorReponsiveLiveData<Int>()

    var observedVal1: Int? = null
    val observer1 = Observer<String> {
      observedVal1 = it?.toInt()
    }

    liveData.addSource(srcLiveData, observer1)

    srcLiveData.value = "1"

    // If `srcLiveData` value was changed,
    // it must affect the observer of `liveData`.
    assertEquals(1, observedVal1)

    liveData.removeSource(srcLiveData)

    srcLiveData.value = "2"

    // Value of `liveData` must be the same as before
    // because `srcLiveData` was removed.
    assertEquals(1, observedVal1)

    // but value of `srcLiveData` must have changed
    assertEquals("2", srcLiveData.value)
  }

  @Test
  fun transformationMapTest() {
    val srcLiveData = MutableLiveData<String>()

    val liveData = LiveDataTransform.map(srcLiveData) {
      it?.toInt()
    }

    // First, make sure `liveData` value is null
    assertEquals(null, liveData.value)

    srcLiveData.value = "1"

    // `liveData` value must have changed after
    // `srcLiveData` value changed
    assertEquals(1, liveData.value)

    srcLiveData.value = "23"

    // The same test for the second time
    assertEquals(23, liveData.value)
  }

  @Test
  fun transformationSwitchMapTest() {
    val srcLiveData = MutableLiveData<String>()
    val producerLiveData = MutableLiveData<Int>()

    srcLiveData.observeForever {
      producerLiveData.value = it?.toInt()
    }

    val liveData = LiveDataTransform.switchMap(srcLiveData) {
      producerLiveData
    }

    // First, make sure `liveData` value is null
    assertEquals(null, liveData.value)

    srcLiveData.value = "1"

    // `liveData` value must have changed after
    // `srcLiveData` value changed
    assertEquals(1, liveData.value)

    srcLiveData.value = "18"

    // The same test for the second time
    assertEquals(18, liveData.value)
  }
}