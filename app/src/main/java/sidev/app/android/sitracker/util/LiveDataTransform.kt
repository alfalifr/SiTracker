package sidev.app.android.sitracker.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LiveDataTransform {
  fun <I, O> map(source: LiveData<I>, transform: (I?) -> O?): LiveData<O> =
    rawMap(source, transform)

  fun <I, O> rawMap(source: LiveData<I>, transform: (I?) -> O?): MediatorReponsiveLiveData<O> =
    MediatorReponsiveLiveData<O>().apply {
      addSource(source) {
        value = transform(it)
      }
    }


  fun <I, O> switchMap(source: LiveData<I>, transform: (I?) -> LiveData<O>?): LiveData<O> =
    rawSwitchMap(source, transform)

  fun <I, O> rawSwitchMap(source: LiveData<I>, transform: (I?) -> LiveData<O>?): MediatorReponsiveLiveData<O> =
    MediatorReponsiveLiveData<O>().apply {
      addSource(source, object: Observer<I> {
        private var producerLiveData: LiveData<O>? = null

        override fun onChanged(t: I) {
          if(producerLiveData != null) {
            removeSource(producerLiveData!!)
          }
          producerLiveData = transform(t)?.also { producer ->
            addSource(producer) {
              value = it
            }
          }
        }
      })
    }
}