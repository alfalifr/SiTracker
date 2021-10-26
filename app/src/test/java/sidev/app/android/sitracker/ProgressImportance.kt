package sidev.app.android.sitracker

data class ProgressImportance(
  val progressId: Int,
  //val importance: Double,
  val factor: ProgressImportanceFactor,
) {
  fun getImportance(timeNow: Long, progress: Long): Double =
    factor.calculateImportance(timeNow, progress)
}
