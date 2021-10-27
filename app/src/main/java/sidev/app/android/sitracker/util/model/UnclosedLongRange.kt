package sidev.app.android.sitracker.util.model

data class UnclosedLongRange(
  val start: Long,
  /**
   * This is inclusive
   */
  val end: Long?,
) {
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
}