package sidev.app.android.sitracker.core.domain.model

import sidev.app.android.sitracker.util.Texts

enum class ProgressTypes(
  val id: Int,
  val label: String,
) {

  TEMPORAL(0, "temporal") {
    override fun formatProgress(progress: Long): String =
      Texts.formatTimeToShortest(progress)
  },
  OCCURENCE(1, "occurence") {
    override fun formatProgress(progress: Long): String = "$progress x"
  },
  ;

  companion object {
    operator fun get(id: Int): ProgressTypes = values().find { it.id == id }
      ?: throw IllegalArgumentException(
        "No such `ProgressTypes` with `id` of '$id'"
      )
  }

  abstract fun formatProgress(progress: Long): String
}

/**
 * [length] is measured in days.
 */
enum class IntervalTypes(
  val id: Int,
  val label: String,
  val length: Int,
) {

  DAILY(0, "Daily", 1),
  WEEKLY(1, "Weekly", 7),
  MONTHLY(2, "Monthly", 30),
  ANNUALLY(3, "Annually", 365),
  ;

  companion object {
    operator fun get(id: Int): IntervalTypes = values().find { it.id == id }
      ?: throw IllegalArgumentException(
        "No such `IntervalTypes` with `id` of '$id'"
      )
  }

  open fun getName(): String = label //TODO: Localize
}