package sidev.app.android.sitracker.core.domain.usecase

import sidev.app.android.sitracker.core.data.local.model.ActiveDate
import sidev.app.android.sitracker.util.mergeIf
import sidev.app.android.sitracker.util.mergeIfOverlaps
import sidev.app.android.sitracker.util.overlaps

interface TimeUseCase {
  fun mergeActiveDates(
    activeDates: List<ActiveDate>,
  ): List<ActiveDate>
}


class TimeUseCaseImpl: TimeUseCase {
  override fun mergeActiveDates(activeDates: List<ActiveDate>): List<ActiveDate> {
    /*
    /**
     * Returns false if there is no any modification to [activeDates]
     */
    fun mergeInOneIteration(activeDates: MutableList<ActiveDate>): Boolean {
      if(activeDates.isEmpty()) {
        return false
      }
      val itr = activeDates.listIterator()
      var mod = 0
      var current = itr.next()

      for(activeDate in itr) {
        val merge = current.mergeIfOverlaps(activeDate)
        current = activeDate
        if(merge != null) {
          activeDates[0] = merge
          itr.remove()
          current = merge
          mod++
        }
      }
      return mod > 0
    }

    val result = activeDates.toMutableList()
    while(mergeInOneIteration(result)) {
      // do nothing cuz operation is done in `mergeInOneIteration`.
    }
    return result
     */

    return activeDates.mergeIf(
      condition = { a, b ->
        a overlaps b
      },
      merge = { a, b ->
        a.mergeIfOverlaps(b)!!
      },
    )
  }
}