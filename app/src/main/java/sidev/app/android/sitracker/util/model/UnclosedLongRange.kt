package sidev.app.android.sitracker.util.model

data class UnclosedLongRange(
  val start: Long,
  /**
   * This is inclusive
   */
  val end: Long?,
) {
  init {
    if(end != null && end < start) {
      throw IllegalArgumentException(
        "`end` not null and must be greater than equal to `start` (end >= start)"
      )
    }
  }

  operator fun contains(number: Long): Boolean =
    number >= start && (end == null || number <= end)

  operator fun contains(number: Number): Boolean = number.toLong().let {
    it >= start && (end == null || it <= end)
  }

  operator fun contains(other: UnclosedLongRange): Boolean =
    other.start >= start &&
      (end == null ||
        (other.end != null && other.end <= end)
        )

  fun diff(): Long? = if(end == null) null
    else end - start
}