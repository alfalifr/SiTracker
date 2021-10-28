package sidev.app.android.sitracker

import sidev.app.android.sitracker.util.SuppressLiteral

interface ListModItrScope {
  fun take(bool: Boolean)
  fun take() = take(true)
}


/**
 * By default, the transformed result by [transform]
 * won't be inserted to result if [ListModItrScope.take]
 * isn't called.
 */
fun <E, R> List<E>.filterAndMap(transform: ListModItrScope.(E) -> R): List<R> {
  if(isEmpty()) {
    return emptyList()
  }

  var mustTake: Boolean
  val results = mutableListOf<R>()

  val scope = object: ListModItrScope {
    @Suppress(SuppressLiteral.UNCHECKED_CAST)
    override fun take(bool: Boolean) {
      mustTake = bool
    }
  }

  mapNotNull {  }

  for(e in this) {
    mustTake = false
    val e2 = scope.transform(e)
    if(mustTake) {
      results += e2
    }
  }
  return results
}