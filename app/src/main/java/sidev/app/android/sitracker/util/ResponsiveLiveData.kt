package sidev.app.android.sitracker.util

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

open class ResponsiveLiveData<T>: LiveData<T> {
  constructor(): super()
  constructor(value: T): super(value)

  private var onFirstChangedVariation: Observer<T>? = null
  private var onFirstChanged = Observer<T> {
    isDataReady = false
    onFirstChangedVariation?.onChanged(it)
  }

  var isDataReady: Boolean = false
    private set(v) {
      field = v
      onDataReady?.invoke(v)
    }
  private var onDataReady: ((Boolean) -> Unit)? = null

  private var addedObserverCount = 0
  private var isPredefinedObserversBeingChanged = false
  private var isPredefinedObserversReady = false

  private var onLastChangedVariation: Observer<T>? = null
  private val onLastDataChanged = Observer<T> {
    isDataReady = true
    onLastChangedVariation?.onChanged(it)
  }

  init {
    addPredefinedObservers()
  }


  fun setOnFirstChangedObserver(observer: Observer<T>?) {
    onFirstChangedVariation = observer
  }
  fun setOnLastChangedObserver(observer: Observer<T>?) {
    onLastChangedVariation = observer
  }
  fun setOnDataReadyListener(l: ((Boolean) -> Unit)?) {
    onDataReady = l
  }


  override fun observe(
    owner: LifecycleOwner,
    observer: Observer<in T>
  ) {
    removePredefinedObserver(onLastDataChanged)
    super.observe(owner, observer)
    addPredefinedObserver(onLastDataChanged)
    addOutsideObserverCount()
  }
  override fun observeForever(observer: Observer<in T>) {
    doWhenNotInternalChanged {
      removePredefinedObserver(onLastDataChanged)
    }
    super.observeForever(observer)
    doWhenNotInternalChanged {
      addPredefinedObserver(onLastDataChanged)
    }
    addOutsideObserverCount()
  }

  override fun removeObserver(observer: Observer<in T>) {
    super.removeObserver(observer)
    decOutsideObserverCount()
  }

  protected fun internalChange(block: () -> Unit) {
    isPredefinedObserversBeingChanged = true
    block()
    isPredefinedObserversBeingChanged = false
  }
  protected fun doWhenNotInternalChanged(block: () -> Unit) {
    if(!isPredefinedObserversBeingChanged) {
      block()
    }
  }

  protected fun removePredefinedObserver(observer: Observer<in T>) = internalChange {
    removeObserver(observer)
  }
  protected fun addPredefinedObserver(observer: Observer<in T>) = internalChange {
    observeForever(observer)
  }

  protected fun addOutsideObserverCount() = doWhenNotInternalChanged {
    addedObserverCount++
  }
  protected fun decOutsideObserverCount() = doWhenNotInternalChanged {
    addedObserverCount--
  }

  /**
   * Called when the number of active observers change from 1 to 0.
   *
   *
   * This does not mean that there are no observers left, there may still be observers but their
   * lifecycle states aren't [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED]
   * (like an Activity in the back stack).
   *
   *
   * You can check if there are observers via [.hasObservers].
   */
  @CallSuper
  override fun onInactive() {
    if(!hasAddedObserver()) {
      removePredefinedObservers()
    }
    super.onInactive()
  }

  /**
   * Called when the number of active observers change from 0 to 1.
   *
   *
   * This callback can be used to know that this LiveData is being used thus should be kept
   * up to date.
   */
  @CallSuper
  override fun onActive() {
    if(!hasAddedObserver() && !isPredefinedObserversReady) {
      addPredefinedObservers()
    }
    super.onActive()
  }


  fun hasAddedObserver(): Boolean = addedObserverCount > 0

  private fun removePredefinedObservers() = internalChange {
    removeObserver(onFirstChanged)
    removeObserver(onLastDataChanged)
    isPredefinedObserversReady = false
  }
  private fun addPredefinedObservers() = internalChange {
    observeForever(onFirstChanged)
    observeForever(onLastDataChanged)
    isPredefinedObserversReady = true
  }
}


open class MutableResponsiveLiveData<T>: ResponsiveLiveData<T> {
  constructor(): super()
  constructor(value: T): super(value)

  public override fun postValue(value: T?) {
    super.postValue(value)
  }

  public override fun setValue(value: T?) {
    super.setValue(value)
  }
}


/**
 * [ResponsiveLiveData] subclass that mimics [MediatorLiveData].
 */
@SuppressLint("RestrictedApi")
class MediatorReponsiveLiveData<T>: MutableResponsiveLiveData<T>() {
  private val sources = SafeIterableMap<LiveData<*>, Source<*>>()

  class Source<T>(
    private val liveData: LiveData<T>,
    val observer: Observer<T>,
  ): Observer<T> by observer {
    fun plug() {
      liveData.observeForever(this)
    }
    fun unplug() {
      liveData.removeObserver(this)
    }
  }

  fun <I> addSource(source: LiveData<I>, observer: Observer<I>) {
    val src = Source(source, observer)
    val existing = sources.putIfAbsent(source, src)
    if(existing != null) {
      if(existing.observer != observer) {
        throw IllegalArgumentException(
          "The `source` was already added with different `observer`"
        )
      }
      return
    }
    if(hasActiveObservers()) {
      src.plug()
    }
  }

  fun <I> removeSource(source: LiveData<I>) {
    sources.remove(source)?.unplug()
  }

  override fun onActive() {
    super.onActive()
    // This is intended safe call (?.) because
    // this method can be called in initiation of super class.
    sources?.forEach { (_, src) ->
      src.plug()
    }
  }
  override fun onInactive() {
    super.onInactive()
    for((_, src) in sources) {
      src.unplug()
    }
  }
}